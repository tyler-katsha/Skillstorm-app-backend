package com.skillstorm.skillstorm.websockets;


import com.skillstorm.skillstorm.dto.Room;
import com.skillstorm.skillstorm.mappers.RoomMapper;
import com.skillstorm.skillstorm.model.DuelRoom;
import org.springframework.stereotype.Component;

import java.util.Queue;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ThreadLocalRandom;

@Component
public class DuelRoomManager {

    // Active matches with 2 players
    private final ConcurrentHashMap<UUID, DuelRoom> activeRooms = new ConcurrentHashMap<>();

    // Private rooms waiting for a 2nd player
    private final ConcurrentHashMap<UUID,Integer> pendingPrivateRooms = new ConcurrentHashMap<>();

    private final Queue<Integer> waitingPlayers = new ConcurrentLinkedQueue<>();

    private final RoomMapper mapper;
    public DuelRoomManager(RoomMapper mapper){
        this.mapper = mapper;
    }

    public synchronized Room findOrCreateRoom(Integer usernameId){

        // Prevent duplicate queue entries from the same user
        if(waitingPlayers.contains(usernameId)){
            return null;

        }

        while(!waitingPlayers.isEmpty()){
            Integer opponentId = waitingPlayers.poll();

            if(opponentId == null || opponentId.equals(usernameId)){
                continue;
            }

            boolean player1IsHost = ThreadLocalRandom.current().nextBoolean();
            int player1 = player1IsHost ? usernameId : opponentId;
            int player2 = player1IsHost ? opponentId : usernameId;

            UUID roomId = UUID.randomUUID();
            
            DuelRoom duelRoom = DuelRoom.builder()
                    .roomCode(roomId)
                    .player1(player1)
                    .player2(player2)
                    .isPublic(true)
                    .build();

            activeRooms.put(roomId,duelRoom);

            return Room.builder()
                    .roomId(roomId)
                    .player1(player1)
                    .player2(player2)
                    .build();
        }

        waitingPlayers.add(usernameId);
        return null;
    }

    public synchronized UUID createPrivateRoom(Integer hostPlayerId){
        UUID roomId = UUID.randomUUID();

        pendingPrivateRooms.put(roomId,hostPlayerId);
        return roomId;
    }
    public synchronized Room joinPrivateRoom(Integer usernameId,String roomCode){
        if (usernameId == null || roomCode == null || roomCode.isBlank()) {
            return null;
        }
        UUID roomId;

        try{
            roomId = UUID.fromString(roomCode);
        } catch (IllegalArgumentException e){
            return null;
        }

        Integer hostPlayerId = pendingPrivateRooms.get(roomId);

        if (hostPlayerId == null) {
            // Room does not exist or is already full
            return null;
        }
        if (hostPlayerId.equals(usernameId)) {
            // Cannot join your own room as opponent
            return null;
        }

        pendingPrivateRooms.remove(roomId);

        boolean player1IsHost = ThreadLocalRandom.current().nextBoolean();
        int player1 = player1IsHost ? hostPlayerId : usernameId;
        int player2 = player1IsHost ? usernameId : hostPlayerId;

        DuelRoom duelRoom = DuelRoom.builder()
                .roomCode(roomId)
                .player1(player1)
                .player2(player2)
                .isPublic(true)
                .build();

        activeRooms.put(roomId,duelRoom);

            return Room.builder()
                    .roomId(UUID.fromString(roomCode))
                    .player1(player1)
                    .player2(player2)
                    .build();
        }

    public DuelRoom getDuelRoom(String roomCode){
        return activeRooms.get(UUID.fromString(roomCode));
    }

    public Room getRoom(String roomId){
        return mapper.mapToOriginalRoom(activeRooms.get(UUID.fromString(roomId)));
    }

    public synchronized boolean removeWaitingPlayer(int playerId){
        pendingPrivateRooms.values().removeIf(id -> id.equals(playerId));
        return waitingPlayers.remove(playerId);
    }

    public DuelRoom removeRoom(String roomCode){
        try{
            UUID roomId = UUID.fromString(roomCode);
            pendingPrivateRooms.remove(roomId);
            return activeRooms.remove(roomId);
        } catch (IllegalArgumentException e){
            return null;
        }
    }
}
