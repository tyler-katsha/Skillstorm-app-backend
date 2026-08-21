package com.skillstorm.skillstorm.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Player {

    private String playerId;
    private String username;
    @Builder.Default
    private Integer score = 0;
    @Builder.Default
    private Integer currentQuestionIndex = 0;
    private Boolean isHost;
    private Boolean isReady;
    private Boolean isGuest;

    @Builder.Default
    private Integer quizzesTaken = 0;
    @Builder.Default
    private Integer quizzesWon = 0;
    @Builder.Default
    private Integer streak = 0;
    private Integer ranking;
    @Builder.Default
    private List<QuestionAttempt> attempts = new ArrayList<>();
}
