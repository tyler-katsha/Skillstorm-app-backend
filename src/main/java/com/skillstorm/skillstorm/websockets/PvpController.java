package com.skillstorm.skillstorm.websockets;

import com.skillstorm.skillstorm.dto.GameEventResponse;
import com.skillstorm.skillstorm.dto.RoomVisibility;
import com.skillstorm.skillstorm.enums.GameEventType;
import com.skillstorm.skillstorm.events.AnswerSubmittedEvent;
import com.skillstorm.skillstorm.events.PlayerReadyEvent;
import com.skillstorm.skillstorm.model.DuelRoom;
import com.skillstorm.skillstorm.model.QuestionAttempt;
import com.skillstorm.skillstorm.oauth.UserPrincipal;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class PvpController {

    private final DuelRoomManager manager;
    private final ApplicationEventPublisher publisher;
    @MessageMapping("/duel/{roomId}/ready")
    public void setReady(@DestinationVariable String roomId, Authentication authentication) {
        UserPrincipal principal = (UserPrincipal) authentication.getPrincipal();
        publisher.publishEvent(new PlayerReadyEvent(roomId, principal.getUserId(), true));
    }

    @MessageMapping("/duel/{roomId}/unready")
    public void setUnready(@DestinationVariable String roomId, Authentication authentication) {
        UserPrincipal principal = (UserPrincipal) authentication.getPrincipal();
        publisher.publishEvent(new PlayerReadyEvent(roomId, principal.getUserId(), false));
    }

    @MessageMapping("/duel/{roomId}/submit")
    public void submitAnswer(@DestinationVariable String roomId, @Payload QuestionAttempt attempt, Authentication authentication) {
        UserPrincipal principal = (UserPrincipal) authentication.getPrincipal();
        publisher.publishEvent(new AnswerSubmittedEvent(roomId, principal.getUserId(), attempt));
    }

    @MessageMapping("/duel/{roomId}/visibility")
    @SendTo("/topic/duel/{roomId}/visibility")
    public GameEventResponse changeRoomVisibility(@DestinationVariable String roomId, @Payload RoomVisibility dto) {

        DuelRoom duelRoom = manager.getDuelRoom(roomId);
        if (duelRoom == null || dto.getIsPublic() == null) {
            return null;
        }

        duelRoom.setIsPublic(dto.getIsPublic());
        GameEventType eventType = Boolean.TRUE.equals(dto.getIsPublic())
                ? GameEventType.PUBLIC_GAME
                : GameEventType.PRIVATE_GAME;

        return new GameEventResponse(eventType, duelRoom);
    }
}