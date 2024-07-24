package com.cj.olive.domain.Heartbeat.websocket.config;

import com.cj.olive.domain.Heartbeat.websocket.handler.HeartRateWebSocketHandler;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

@Configuration
@EnableWebSocket
public class WebSocketConfig implements WebSocketConfigurer {
    private final HeartRateWebSocketHandler heartRateWebSocketHandler;


    public WebSocketConfig(HeartRateWebSocketHandler heartRateWebSocketHandler) {
        this.heartRateWebSocketHandler = heartRateWebSocketHandler;
    }
    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(heartRateWebSocketHandler, "/ws/v1/heartbeat").setAllowedOrigins("*");
    }
}