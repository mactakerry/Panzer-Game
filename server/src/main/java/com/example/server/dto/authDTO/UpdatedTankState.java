package com.example.server.dto.authDTO;

public class UpdatedTankState {
    public long playerId;
    public float x;
    public float y;
    public float angle;

    @Override
    public String toString() {
        return "UpdatedTankState{" +
                "playerId=" + playerId +
                ", x=" + x +
                ", y=" + y +
                ", angle=" + angle +
                '}';
    }
}
