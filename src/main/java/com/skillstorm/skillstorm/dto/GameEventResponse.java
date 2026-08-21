package com.skillstorm.skillstorm.dto;

import com.skillstorm.skillstorm.enums.GameEventType;
import com.skillstorm.skillstorm.model.DuelRoom;
import com.skillstorm.skillstorm.model.Player;
import com.skillstorm.skillstorm.model.QuestionAttempt;
import lombok.Data;

@Data
public class GameEventResponse {

    private final GameEventType gameEventType;
    private QuestionAttempt attempt;
    private Player player;
    private DuelRoom duelRoom;

    public GameEventResponse(GameEventType gameEventType) {
        this.gameEventType = gameEventType;
    }

    public GameEventResponse(GameEventType gameEventType, QuestionAttempt attempt) {
        this.gameEventType = gameEventType;
        this.attempt = attempt;
    }

    public GameEventResponse(GameEventType gameEventType, Player winner) {
        this.gameEventType = gameEventType;
        this.player = winner;
    }


    public GameEventResponse(GameEventType gameEventType, DuelRoom duelRoom) {
        this.gameEventType = gameEventType;
        this.duelRoom = duelRoom;
    }
}
