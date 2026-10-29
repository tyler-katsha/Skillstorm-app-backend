package com.skillstorm.skillstorm.events;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data // Getter, Setter, toString
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PlayerReadyEvent {

    private String roomId;
    private Integer playerId;
    private Boolean isReady;
}
