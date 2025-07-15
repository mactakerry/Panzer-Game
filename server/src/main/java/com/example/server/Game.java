package com.example.server;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.Json;
import com.example.server.dto.NetworkMessage;
import com.example.server.ws.PanzerWebSocketHandler;
import com.example.server.ws.WebSocketEventListener;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;

@Slf4j
@Component
public class Game extends ApplicationAdapter {
    private final PanzerWebSocketHandler panzerWebSocketHandler;

    Array<NetworkMessage> events = new Array<>();

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
                events.add(networkMessage);
            }

            @Override
            public void onDisconnected(WebSocketSession session) {
            }

            @Override
            public void onMessage(WebSocketSession session, String message) {
                Json json = new Json();
                NetworkMessage networkMessage = json.fromJson(NetworkMessage.class, message);

                events.add(networkMessage);

            }
        });
    }

    @Override
    public void render() {
        for (WebSocketSession session : panzerWebSocketHandler.getSessions()) {
            try {
                for (NetworkMessage event : events) {
                    String json = objectMapper.writeValueAsString(event);
                    session.sendMessage(new TextMessage(json));
                }
            } catch (IOException ioException) {
                throw new RuntimeException();
            }
        }

        events.clear();
    }
}
