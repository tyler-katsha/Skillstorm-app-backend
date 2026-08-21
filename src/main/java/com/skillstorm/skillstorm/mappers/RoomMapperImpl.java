package com.skillstorm.skillstorm.mappers;

import com.skillstorm.skillstorm.dto.Room;
import com.skillstorm.skillstorm.model.DuelRoom;
import org.springframework.stereotype.Component;

@Component
public class RoomMapperImpl implements RoomMapper{

    @Override
    public DuelRoom mapToDuelRoom(Room room) {
        return DuelRoom.builder()
                .roomCode(room.getRoomId())
                .player1(room.getPlayer1())
                .player2(room.getPlayer2())
                .isPublic(true)
                .build();
    }

    @Override
    public Room mapToOriginalRoom(DuelRoom duel) {
        return Room.builder().gameEventType(null)
                .roomId(duel.getRoomCode())
                .player1(duel.getPlayer1())
                .player2(duel.getPlayer2())
                .build();
    }
}
