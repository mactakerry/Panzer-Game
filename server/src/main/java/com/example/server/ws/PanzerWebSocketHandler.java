package com.example.server.ws;

import com.badlogic.gdx.utils.Array;
import com.example.server.service.JwtService;
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

    private final JwtService jwtService;

    private WebSocketEventListener webSocketEventListener;

    public PanzerWebSocketHandler(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        String token = getTokenFromSession(session);
        if (!jwtService.isTokenValid(token)) {
            session.close(CloseStatus.SESSION_NOT_RELIABLE);
            return;
        }

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

    private String getTokenFromSession(WebSocketSession session) {
        String query = session.getUri().getQuery();
        if (query == null) return null;

        for (String param : query.split("&")) {
            if (param.startsWith("token=")) {
                return param.substring(6);
            }
        }
        return null;
    }


}
