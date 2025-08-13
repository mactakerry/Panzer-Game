package com.example.server;

import com.badlogic.gdx.math.Vector2;
import com.example.server.dto.InputState;
import lombok.Getter;
import lombok.Setter;
import org.springframework.web.socket.WebSocketSession;


@Getter
@Setter
public class Tank {
    private long playerId;
    private Vector2 position;
    private Vector2 angle;
    private WebSocketSession session;
    private int speed = 5;

    public void updatePosition(InputState inputState) {
        if (inputState.upPressed) position.add(0, speed);
        if (inputState.downPressed) position.add(0, -speed);
        if (inputState.rightPressed) position.add(speed, 0);
        if (inputState.leftPressed) position.add(-speed, 0);
    }

    public void updateAngle(float angleDeg) {
        angle.setAngleDeg(angleDeg);
    }
}
