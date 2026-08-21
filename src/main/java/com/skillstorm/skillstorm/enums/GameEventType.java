package com.skillstorm.skillstorm.enums;

public enum GameEventType {
    PLAYER_JOINED,      // Someone entered the room
    PLAYER_LEFT,        // Someone disconnected or left
    PLAYER_READY,       // User toggled ready status
    GAME_STARTED,       // Host hit start, sending the first question
    ANSWER_SUBMITTED,   // A player locked in an answer
    SCORE_UPDATE,       // Live scoreboard refresh sent to both
    NEXT_QUESTION,      // Move to the next question index
    GAME_OVER,           // Final scores, winner declared
    WAITING_FOR_OPPONENT, // Waiting for Opponent to Join
    PRIVATE_GAME, // Private Game
    PUBLIC_GAME // Public Game
}
