package com.supersidor.flightmap.websocket;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.socket.WebSocketHandler;
import org.springframework.web.reactive.socket.WebSocketMessage;
import org.springframework.web.reactive.socket.WebSocketSession;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;

import static java.time.LocalDateTime.now;
import static java.util.UUID.randomUUID;

@Component("ConsoleWebSocketHandler")
@Slf4j
public class ConsoleWebSocketHandler implements WebSocketHandler {
    private static final ObjectMapper json = new ObjectMapper();


    @Override
    public Mono<Void> handle(WebSocketSession webSocketSession) {
        return webSocketSession.send(Mono.just("Hello world")
                .map(m -> {
                    try {
                        return json.writeValueAsString(m);
                    } catch (JsonProcessingException e) {
                    }
                    return "{}";
                })
                .map(webSocketSession::textMessage));
    }
}
