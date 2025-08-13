package com.example.server.dto;

import lombok.Data;
import org.springframework.web.socket.WebSocketSession;

@Data
public class TankState {
    public long playerId;
    public float angle;
    public InputState inputState;

    private WebSocketSession session;
}
