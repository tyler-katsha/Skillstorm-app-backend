package com.skillstorm.skillstorm.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DuelRoom {

    private UUID roomCode;
    private Integer player1;
    private Integer player2;
    @Builder.Default
    private Boolean isPublic = true;
}
