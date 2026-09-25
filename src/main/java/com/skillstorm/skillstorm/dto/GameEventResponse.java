package com.skillstorm.skillstorm.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.skillstorm.skillstorm.enums.GameEventType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class GameEventResponse<T> {

    private GameEventType gameEventType;
    private T payload;

    public GameEventResponse(GameEventType gameEventType) {
        this.gameEventType = gameEventType;
        this.payload = null;
    }

    public static <T> GameEventResponse<T> of(GameEventType type, T payload) {
        return new GameEventResponse<>(type, payload);
    }

    public static GameEventResponse<Void> of(GameEventType type) {
        return new GameEventResponse<>(type, null);
    }
}