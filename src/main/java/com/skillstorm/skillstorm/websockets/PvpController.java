package com.skillstorm.skillstorm.websockets;

import com.skillstorm.skillstorm.dto.GameEventResponse;
import com.skillstorm.skillstorm.dto.RoomVisibility;
import com.skillstorm.skillstorm.enums.GameEventType;
import com.skillstorm.skillstorm.model.DuelRoom;
import com.skillstorm.skillstorm.model.Player;
import com.skillstorm.skillstorm.model.QuestionAttempt;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

@Controller
public class PvpController {

    private final DuelRoomManager manager;

    public PvpController(DuelRoomManager manager){
        this.manager = manager;
    }
    @MessageMapping("/duel/{roomId}/action")
    @SendTo("/topic/duel/{roomId}")
    public Player sendPvpData(@DestinationVariable String roomId, Player player){
        return player;
    }

    @MessageMapping("/duel/{roomId}/ready")
    @SendTo("/topic/duel/{roomId}")
    public Player sendReady(@DestinationVariable String roomId,@Payload Player player){

        player.setIsReady(true);
        return player;
    }

    @MessageMapping("/duel/{roomId}/unready")
    @SendTo("/topic/duel/{roomId}")
    public Player sendUnready(@DestinationVariable String roomId,@Payload Player player){

        player.setIsReady(false);
        return player;
    }

    @MessageMapping("/duel/{roomId}/submit")
    @SendTo("/topic/duel/{roomId}")
    public GameEventResponse submitAnswer(@DestinationVariable String roomId,@Payload QuestionAttempt attempt){
        return new GameEventResponse(GameEventType.GAME_OVER,attempt);
    }

    @MessageMapping("/duel/{roomId}/next-question")
    @SendTo("/topic/duel/{roomId}")
    public GameEventResponse nextQuestion(@DestinationVariable String roomId){
        return new GameEventResponse(GameEventType.GAME_OVER);
    }

    @MessageMapping("/duel/{roomId}/game-over")
    @SendTo("/topic/duel/{roomId}")
    public GameEventResponse gameOver(@DestinationVariable String roomId,@Payload Player winner){
        return new GameEventResponse(GameEventType.GAME_OVER,winner);
    }

    @MessageMapping("/duel/{roomId}/visibility")
    @SendTo("/topic/duel/{roomId}/visibility")
    public GameEventResponse changeRoomToStatus(@DestinationVariable String roomId,@Payload RoomVisibility dto){

        DuelRoom duelRoom = manager.getDuelRoom(roomId);

        if(duelRoom == null || dto.getIsPublic() == null){
            return null;
        }
        duelRoom.setIsPublic(dto.getIsPublic());

        GameEventType eventType = Boolean.TRUE.equals(dto.getIsPublic()) ? GameEventType.PUBLIC_GAME : GameEventType.PRIVATE_GAME;

        return new GameEventResponse(eventType,duelRoom);
    }

}
