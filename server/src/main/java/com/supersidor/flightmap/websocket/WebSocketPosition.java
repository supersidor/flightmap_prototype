package com.supersidor.flightmap.websocket;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class WebSocketPosition {
    private float  altitude;
    private double latitude;
    private double longitude;
    private float  heading;
}
