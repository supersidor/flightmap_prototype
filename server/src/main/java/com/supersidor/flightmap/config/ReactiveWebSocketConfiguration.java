package com.supersidor.flightmap.config;

import com.supersidor.flightmap.websocket.CustomRequestUpgradeStrategy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.HandlerMapping;
import org.springframework.web.reactive.handler.SimpleUrlHandlerMapping;
import org.springframework.web.reactive.socket.WebSocketHandler;
import org.springframework.web.reactive.socket.server.RequestUpgradeStrategy;
import org.springframework.web.reactive.socket.server.WebSocketService;
import org.springframework.web.reactive.socket.server.support.HandshakeWebSocketService;
import org.springframework.web.reactive.socket.server.support.WebSocketHandlerAdapter;
import org.springframework.web.reactive.socket.server.upgrade.ReactorNettyRequestUpgradeStrategy;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class ReactiveWebSocketConfiguration {

    @Autowired
    @Qualifier("ReactiveWebSocketHandler")
    private WebSocketHandler webSocketHandler;

    @Autowired
    @Qualifier("ConsoleWebSocketHandler")
    private WebSocketHandler consoleWebSocketHandler;

    @Bean
    public HandlerMapping webSocketHandlerMapping() {
        Map<String, WebSocketHandler> map = new HashMap<>();
        map.put("/ws", webSocketHandler);

        SimpleUrlHandlerMapping handlerMapping = new SimpleUrlHandlerMapping();
        handlerMapping.setOrder(1);
        handlerMapping.setUrlMap(map);
        return handlerMapping;
    }

    @Bean
    public HandlerMapping webSocketConsoleHandlerMapping() {
        Map<String, WebSocketHandler> map = new HashMap<>();
        map.put("/console", consoleWebSocketHandler);

        SimpleUrlHandlerMapping handlerMapping = new SimpleUrlHandlerMapping();
        handlerMapping.setOrder(1);
        handlerMapping.setUrlMap(map);
        return handlerMapping;
    }
    @Bean
    public WebSocketHandlerAdapter handlerAdapter() {
        return new WebSocketHandlerAdapter();
    }

    //RequestUpgradeStrategy upgradeStrategy
    //    @Bean
    //    public WebSocketService webSocketService() {
    //        return new HandshakeWebSocketService(new CustomRequestUpgradeStrategy());
    //    }
}
