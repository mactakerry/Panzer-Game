package com.example.server;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Json;
import com.example.server.dto.NetworkMessage;
import com.example.server.dto.TankState;
import com.example.server.dto.authDTO.UpdatedTankState;
import com.example.server.ws.PanzerWebSocketHandler;
import com.example.server.ws.WebSocketEventListener;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;


import java.io.IOException;
import java.util.ArrayList;

@Slf4j
@Component
public class Game extends ApplicationAdapter {
    private final PanzerWebSocketHandler panzerWebSocketHandler;

    private final Array<NetworkMessage> events = new Array<>();
    private final ArrayList<TankState> updateStateEvents = new ArrayList<>();
    private final ArrayList<Tank> tanks = new ArrayList<>();

    private final ObjectMapper objectMapper = new ObjectMapper();

    public Game(PanzerWebSocketHandler panzerWebSocketHandler) {
        this.panzerWebSocketHandler = panzerWebSocketHandler;
    }

    @Override
    public void create() {
        panzerWebSocketHandler.setWebSocketEventListener(new WebSocketEventListener() {
            @Override
            public void onConnected(WebSocketSession session) {
                NetworkMessage networkMessage = new NetworkMessage();
                networkMessage.setType("CONSOLE");
                networkMessage.setPayload(session.getId() + " just joined");

                synchronized (events) {
                    events.add(networkMessage);
                }

                Tank newTank = new Tank();
                newTank.setPosition(new Vector2(0, 0));
                newTank.setAngle(new Vector2());
                newTank.setSession(session);
                synchronized (tanks) {
                    tanks.add(newTank);
                }
            }

            @Override
            public void onDisconnected(WebSocketSession session) {
                for (Tank tank : tanks) {
                    if (tank.getSession() == session) {
                        tanks.remove(tank);
                        return;
                    }
                }
            }

            @Override
            public void onMessage(WebSocketSession session, String message) {
                Json json = new Json();
                NetworkMessage networkMessage = json.fromJson(NetworkMessage.class, message);

                if (networkMessage.getType().equals("STATE_UPDATE")) {
                    TankState tankState = json.fromJson(TankState.class, networkMessage.getPayload());
                    tankState.setSession(session);
                    synchronized (updateStateEvents) {
                        updateStateEvents.add(tankState);
                    }
                    return;
                }

                synchronized (events) {
                    events.add(networkMessage);
                }
            }
        });
    }

    private Json json = new Json(); // один json для render
    UpdatedTankState updatedTankState = new UpdatedTankState(); // один экземпляр для каждого танка
    @Override
    public void render() {
        ArrayList<TankState> copyUpdateStateEvents = (ArrayList<TankState>) updateStateEvents.clone();

        for (TankState updateState : copyUpdateStateEvents) {
            for (Tank tank : tanks) {
                if (updateState.getSession() == tank.getSession()) {
                    tank.updatePosition(updateState.inputState);
                    tank.updateAngle(updateState.getAngle());

                    updatedTankState.playerId = updateState.playerId;
                    updatedTankState.x = tank.getPosition().x;
                    updatedTankState.y = tank.getPosition().y;
                    updatedTankState.angle = tank.getAngle().angleDeg();

                    NetworkMessage networkMessage = new NetworkMessage();
                    networkMessage.setType("STATE_UPDATE");
                    networkMessage.setPayload(json.toJson(updatedTankState));
                    events.add(networkMessage);

                }
            }
        }

        updateStateEvents.clear();

        for (WebSocketSession session : panzerWebSocketHandler.getSessions()) {
            try {
                for (NetworkMessage event : events) {
                    String json = objectMapper.writeValueAsString(event);
                    System.out.println(json);
                    session.sendMessage(new TextMessage(json));
                }
            } catch (IOException ioException) {
                throw new RuntimeException();
            }
        }

        events.clear();
    }
}
