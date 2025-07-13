package com.example.server.config;

import com.example.server.Game;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import com.badlogic.gdx.backends.headless.HeadlessApplication;

@Component
public class AppConfig {
    @Bean
    public HeadlessApplication getApplication(Game game) {
        return new HeadlessApplication(game);
    }
}
