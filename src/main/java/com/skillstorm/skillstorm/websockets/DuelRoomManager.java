package com.skillstorm.skillstorm.websockets;

import com.skillstorm.skillstorm.dto.Room;
import com.skillstorm.skillstorm.mappers.RoomMapper;
import com.skillstorm.skillstorm.model.DuelRoom;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ThreadLocalRandom;

@Component
@RequiredArgsConstructor
public class DuelRoomManager {

    // Active matches with 2 players: roomId -> DuelRoom
    private final ConcurrentHashMap<UUID, DuelRoom> activeRooms = new ConcurrentHashMap<>();

    // Private rooms waiting for an opponent: roomId -> hostPlayerId
    private final ConcurrentHashMap<UUID, Integer> pendingPrivateRooms = new ConcurrentHashMap<>();

    // Fast lookup for player -> active roomId (prevents duplicate room joins)
    private final ConcurrentHashMap<Integer, UUID> playerToRoomIndex = new ConcurrentHashMap<>();

    // Public matchmaking queue
    private final ConcurrentLinkedQueue<Integer> waitingQueue = new ConcurrentLinkedQueue<>();
    private final Set<Integer> waitingPlayerIds = ConcurrentHashMap.newKeySet();

    private final RoomMapper mapper;

    public synchronized Room findOrCreateRoom(Integer playerId) {
        if (playerId == null || waitingPlayerIds.contains(playerId) || playerToRoomIndex.containsKey(playerId)) {
            return null;
        }

        while (!waitingQueue.isEmpty()) {
            Integer opponentId = waitingQueue.poll();
            if (opponentId == null) {
                continue;
            }

            waitingPlayerIds.remove(opponentId);

            // Stale entry check: opponent might have disconnected or already joined another room
            if (opponentId.equals(playerId) || !isPlayerEligible(opponentId)) {
                continue;
            }

            boolean hostIsPlayer1 = ThreadLocalRandom.current().nextBoolean();
            int p1 = hostIsPlayer1 ? playerId : opponentId;
            int p2 = hostIsPlayer1 ? opponentId : playerId;

            UUID roomId = UUID.randomUUID();
            DuelRoom duelRoom = DuelRoom.builder()
                    .roomCode(roomId)
                    .player1(p1)
                    .player2(p2)
                    .isPublic(true)
                    .build();

            activeRooms.put(roomId, duelRoom);
            playerToRoomIndex.put(p1, roomId);
            playerToRoomIndex.put(p2, roomId);

            return Room.builder()
                    .roomId(roomId)
                    .player1(p1)
                    .player2(p2)
                    .build();
        }

        waitingQueue.add(playerId);
        waitingPlayerIds.add(playerId);
        return null;
    }

    public synchronized UUID createPrivateRoom(Integer hostPlayerId) {
        if (hostPlayerId == null || playerToRoomIndex.containsKey(hostPlayerId)) {
            return null;
        }

        UUID roomId = UUID.randomUUID();
        pendingPrivateRooms.put(roomId, hostPlayerId);
        return roomId;
    }

    public synchronized Room joinPrivateRoom(Integer guestPlayerId, String roomCode) {
        if (guestPlayerId == null || roomCode == null || roomCode.isBlank()) {
            return null;
        }

        UUID roomId = parseUuid(roomCode).orElse(null);
        if (roomId == null) {
            return null;
        }

        Integer hostPlayerId = pendingPrivateRooms.get(roomId);
        if (hostPlayerId == null || hostPlayerId.equals(guestPlayerId)) {
            return null;
        }

        pendingPrivateRooms.remove(roomId);

        boolean hostIsPlayer1 = ThreadLocalRandom.current().nextBoolean();
        int p1 = hostIsPlayer1 ? hostPlayerId : guestPlayerId;
        int p2 = hostIsPlayer1 ? guestPlayerId : hostPlayerId;

        DuelRoom duelRoom = DuelRoom.builder()
                .roomCode(roomId)
                .player1(p1)
                .player2(p2)
                .isPublic(false)
                .build();

        activeRooms.put(roomId, duelRoom);
        playerToRoomIndex.put(p1, roomId);
        playerToRoomIndex.put(p2, roomId);

        return Room.builder()
                .roomId(roomId)
                .player1(p1)
                .player2(p2)
                .build();
    }

    public DuelRoom getDuelRoom(String roomCode) {
        return parseUuid(roomCode).map(activeRooms::get).orElse(null);
    }

    public Room getRoom(String roomId) {
        DuelRoom room = getDuelRoom(roomId);
        return room != null ? mapper.mapToOriginalRoom(room) : null;
    }

    public DuelRoom getActiveRoomByPlayer(Integer playerId) {
        if (playerId == null) return null;
        UUID roomId = playerToRoomIndex.get(playerId);
        return roomId != null ? activeRooms.get(roomId) : null;
    }

    public synchronized void removeWaitingPlayer(int playerId) {
        waitingPlayerIds.remove(playerId);
        waitingQueue.remove(playerId);
        pendingPrivateRooms.values().removeIf(id -> id.equals(playerId));
    }

    public synchronized DuelRoom removeRoom(String roomCode) {
        return parseUuid(roomCode).map(roomId -> {
            pendingPrivateRooms.remove(roomId);
            DuelRoom removed = activeRooms.remove(roomId);
            if (removed != null) {
                playerToRoomIndex.remove(removed.getPlayer1());
                playerToRoomIndex.remove(removed.getPlayer2());
            }
            return removed;
        }).orElse(null);
    }

    private boolean isPlayerEligible(Integer playerId) {
        return !playerToRoomIndex.containsKey(playerId);
    }

    private Optional<UUID> parseUuid(String uuidStr) {
        try {
            return Optional.of(UUID.fromString(uuidStr));
        } catch (IllegalArgumentException | NullPointerException e) {
            return Optional.empty();
        }
    }
}