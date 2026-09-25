package com.skillstorm.skillstorm.events;

import com.skillstorm.skillstorm.enums.GameEventType;
import com.skillstorm.skillstorm.model.Player;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data // Getter, Setter, toString
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class GameEventResponse {

    private GameEventType gameEventType;
    private Player player;
}
