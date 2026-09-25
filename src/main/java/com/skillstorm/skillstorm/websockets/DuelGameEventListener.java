package com.skillstorm.skillstorm.websockets;

import com.skillstorm.skillstorm.dto.GameEventResponse;
import com.skillstorm.skillstorm.enums.GameEventType;
import com.skillstorm.skillstorm.events.AnswerSubmittedEvent;
import com.skillstorm.skillstorm.events.GameStartedEvent;
import com.skillstorm.skillstorm.events.PlayerReadyEvent;
import com.skillstorm.skillstorm.model.DuelRoom;
import lombok.RequiredArgsConstructor;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DuelGameEventListener {

    private final DuelRoomManager manager;
    private final SimpMessagingTemplate template;

    @EventListener
    public void handleGameStarted(GameStartedEvent event) {
        String destination = "/topic/duel/" + event.room().getRoomId();
        template.convertAndSend(destination, GameEventResponse.of(GameEventType.GAME_STARTED, event.room()));
    }

    @EventListener
    public void handlePlayerReady(PlayerReadyEvent event) {
        DuelRoom room = manager.getDuelRoom(event.getRoomId());

        if (room == null) return;


        // Verify the user actually belongs to this room
        if (!event.getPlayerId().equals(room.getPlayer1()) && !event.getPlayerId().equals(room.getPlayer2())) {
            return;
        }

        // Emit player ready state change to the topic
        template.convertAndSend("/topic/duel/" + event.getRoomId(), GameEventResponse.of(event.getIsReady() ? GameEventType.PLAYER_READY : GameEventType.PLAYER_UNREADY, event.getPlayerId()));
    }

    @EventListener
    public void handleAnswerSubmitted(AnswerSubmittedEvent event) {
        DuelRoom room = manager.getDuelRoom(event.roomId());
        if (room == null) {
            return;
        }

        // Process answer logic (score update, check if all questions answered)
        // e.g., if match is finished:
        // manager.removeRoom(event.roomId());
        // template.convertAndSend("/topic/duel/" + event.roomId(), new GameEventResponse(GameEventType.GAME_OVER, winner));
    }
}