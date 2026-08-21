package com.skillstorm.skillstorm.mappers;

import com.skillstorm.skillstorm.dto.Room;
import com.skillstorm.skillstorm.model.DuelRoom;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface RoomMapper {

    DuelRoom mapToDuelRoom(Room room);
    Room mapToOriginalRoom(DuelRoom room);
}
