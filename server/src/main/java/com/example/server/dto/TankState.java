package com.example.server.dto;

import lombok.Data;

@Data
public class TankState {
    private String playerId;
    private float x;
    private float y;
    private float angleX;
    private float angleY;
}
