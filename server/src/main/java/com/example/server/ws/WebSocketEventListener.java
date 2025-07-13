package com.example.server.ws;

import jakarta.websocket.Session;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;

public interface WebSocketEventListener {
    void onConnected(WebSocketSession session);
    void onDisconnected(WebSocketSession session);
    void onMessage(WebSocketSession session, String message);
}
