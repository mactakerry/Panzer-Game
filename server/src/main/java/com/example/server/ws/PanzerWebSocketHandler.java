package com.example.server.ws;

import com.badlogic.gdx.utils.Array;
import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;


@Component
@Getter
@Setter
public class PanzerWebSocketHandler extends TextWebSocketHandler {
    private final Array<WebSocketSession> sessions = new Array<>();

    private WebSocketEventListener webSocketEventListener;

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        sessions.add(session);
        webSocketEventListener.onConnected(session);
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        webSocketEventListener.onMessage(session, message.getPayload());
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        sessions.removeValue(session, true);
        webSocketEventListener.onDisconnected(session);
    }


}
