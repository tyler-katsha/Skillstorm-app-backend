package com.skillstorm.skillstorm.websockets;

import com.skillstorm.skillstorm.dto.Room;
import com.skillstorm.skillstorm.enums.GameEventType;
import com.skillstorm.skillstorm.events.GameStartedEvent;
import com.skillstorm.skillstorm.oauth.UserPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/duel")
@RequiredArgsConstructor
public class DuelRoomController {

    private final DuelRoomManager manager;
    private final ApplicationEventPublisher publisher;

    @PostMapping("/create-private")
    public ResponseEntity<Map<String, Object>> createPrivateRoom(@AuthenticationPrincipal UserPrincipal principal) {
        UUID roomId = manager.createPrivateRoom(principal.getUserId());
        return ResponseEntity.ok(Map.of(
                "roomId", roomId.toString(),
                "status", GameEventType.WAITING_FOR_OPPONENT
        ));
    }

    @PostMapping("/join-friend")
    public ResponseEntity<Room> joinFriendRoom(
            @AuthenticationPrincipal UserPrincipal principal,
            @RequestParam("room_code") String roomCode) {

        Room room = manager.joinPrivateRoom(principal.getUserId(), roomCode);

        if (room == null) {
            return ResponseEntity.ok(Room.builder()
                    .gameEventType(GameEventType.WAITING_FOR_OPPONENT)
                    .build());
        }

        room.setGameEventType(GameEventType.GAME_STARTED);
        // Alert both players (specifically the waiting host) over WebSockets
        publisher.publishEvent(new GameStartedEvent(room));

        return ResponseEntity.ok(room);
    }

    @PostMapping("/quick-join")
    public ResponseEntity<Room> quickJoinRoom(@AuthenticationPrincipal UserPrincipal principal) {
        Room room = manager.findOrCreateRoom(principal.getUserId());

        if (room == null) {
            return ResponseEntity.ok(Room.builder()
                    .gameEventType(GameEventType.WAITING_FOR_OPPONENT)
                    .build());
        }

        room.setGameEventType(GameEventType.GAME_STARTED);
        // Notify the first matched player who is already subscribed to /topic/duel/{roomId}
        publisher.publishEvent(new GameStartedEvent(room));

        return ResponseEntity.ok(room);
    }

    @PostMapping("/leave-queue")
    public ResponseEntity<Void> leaveQueue(@AuthenticationPrincipal UserPrincipal principal) {
        manager.removeWaitingPlayer(principal.getUserId());
        return ResponseEntity.ok().build();
    }
}