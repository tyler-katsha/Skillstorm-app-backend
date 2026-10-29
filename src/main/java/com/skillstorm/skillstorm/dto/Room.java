package com.skillstorm.skillstorm.dto;

import com.skillstorm.skillstorm.enums.GameEventType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Room {
    private GameEventType gameEventType;
    private UUID roomId;
    private int player1;
    private int player2;
}
