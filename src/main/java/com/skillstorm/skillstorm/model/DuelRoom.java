package com.skillstorm.skillstorm.model;

import lombok.*;

import java.util.UUID;

@Getter
@Setter
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
