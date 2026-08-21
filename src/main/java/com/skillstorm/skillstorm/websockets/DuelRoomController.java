package com.skillstorm.skillstorm.websockets;

import com.skillstorm.skillstorm.dto.Room;
import com.skillstorm.skillstorm.enums.GameEventType;
import com.skillstorm.skillstorm.oauth.UserPrincipal;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/duel")
public class DuelRoomController {

    private final DuelRoomManager manager;

    public DuelRoomController(DuelRoomManager manager){
        this.manager = manager;
    }

    public ResponseEntity<Map<String,Object>> createPrivateRoom(@AuthenticationPrincipal UserPrincipal principal){
        UUID roomId = manager.createPrivateRoom(principal.getUserId());

        return ResponseEntity.ok(Map.of("roomId",roomId.toString(),"status",GameEventType.WAITING_FOR_OPPONENT));
    }
    @PostMapping("/join-friend")
    public ResponseEntity<Room> joinFriendRoom(@AuthenticationPrincipal UserPrincipal principal, @RequestParam("room_code") String roomCode){

        System.out.println("Room Code ID: " + roomCode);

        System.out.println("USER ID: " + principal.getUserId());

        Room room = manager.joinPrivateRoom(principal.getUserId(),roomCode);

        if(room == null){

            return ResponseEntity.ok(Room.builder()
                    .gameEventType(GameEventType.WAITING_FOR_OPPONENT)
                    .build());
        }

        room.setGameEventType(GameEventType.GAME_STARTED);

        System.out.println(room);
        return ResponseEntity.ok(room);
    }

    @PostMapping("/quick-join")
    public ResponseEntity<Room> quickJoinRoom(@AuthenticationPrincipal UserPrincipal principal){

        Room room = manager.findOrCreateRoom(principal.getUserId());

        if(room == null){
            return ResponseEntity.ok(Room.builder()
                    .gameEventType(GameEventType.WAITING_FOR_OPPONENT)
                    .build());
        }

        room.setGameEventType(GameEventType.GAME_STARTED);

        System.out.println(room);
        return ResponseEntity.ok(room);
    }

    @PostMapping("/leave-queue")
    public ResponseEntity<Void> leaveQueue(@AuthenticationPrincipal UserPrincipal principal){
        manager.removeWaitingPlayer(principal.getUserId());

        System.out.println(manager.removeWaitingPlayer(principal.getUserId()));
        return ResponseEntity.ok().build();
    }

}
