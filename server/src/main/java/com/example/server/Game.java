package com.example.server;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.utils.Array;
import com.example.server.ws.PanzerWebSocketHandler;
import com.example.server.ws.WebSocketEventListener;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;

@Component
public class Game extends ApplicationAdapter {
    private final PanzerWebSocketHandler panzerWebSocketHandler;

    Array<String> events = new Array<>();

    public Game(PanzerWebSocketHandler panzerWebSocketHandler) {
        this.panzerWebSocketHandler = panzerWebSocketHandler;
    }

    @Override
    public void create() {
        panzerWebSocketHandler.setWebSocketEventListener(new WebSocketEventListener() {
            @Override
            public void onConnected(WebSocketSession session) {
                events.add(session.getId() + " just joined");
            }

            @Override
            public void onDisconnected(WebSocketSession session) {
                events.add(session.getId() + " leave");
            }

            @Override
            public void onMessage(WebSocketSession session, String message) {
                events.add(session.getId() + " said: " + message);
            }
        });
    }

    @Override
    public void render() {
        for (WebSocketSession session : panzerWebSocketHandler.getSessions()) {
            try {
                for (String event : events) {
                    session.sendMessage(new TextMessage(event));
                }
            } catch (IOException ioException) {
                throw new RuntimeException();
            }
        }

        events.clear();
    }
}
