package com.example.server.ws;

import org.springframework.web.socket.WebSocketSession;

public interface WebSocketEventListener {
    void onConnected(WebSocketSession session);
    void onDisconnected(WebSocketSession session);
    void onMessage(WebSocketSession session, String message);
}
