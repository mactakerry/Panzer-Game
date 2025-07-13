package com.example.server.config;

import com.example.server.ws.PanzerWebSocketHandler;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {
    private final PanzerWebSocketHandler panzerWebSocketHandler;

    public WebSocketConfig(PanzerWebSocketHandler panzerWebSocketHandler) {
        this.panzerWebSocketHandler = panzerWebSocketHandler;
    }

    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(panzerWebSocketHandler, "/panzer-ws")
                .setAllowedOrigins("*");
    }
}