package com.supersidor.booratino.model;

import lombok.Data;
import lombok.ToString;

@Data
@ToString
public class Position {
    String title;
    double altitude;
    double latitude;
    double longitude;
    double heading;
}
