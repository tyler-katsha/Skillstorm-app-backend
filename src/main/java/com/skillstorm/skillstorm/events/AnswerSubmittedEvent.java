package com.skillstorm.skillstorm.events;

import com.skillstorm.skillstorm.model.QuestionAttempt;

public record AnswerSubmittedEvent(String roomId, Integer playerId, QuestionAttempt attempt) {
}
