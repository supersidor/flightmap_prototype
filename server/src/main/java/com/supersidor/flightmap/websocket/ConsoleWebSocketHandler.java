package com.supersidor.flightmap.websocket;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.supersidor.flightmap.security.UserPrincipal;
import com.supersidor.flightmap.service.PositionService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.socket.WebSocketHandler;
import org.springframework.web.reactive.socket.WebSocketSession;
import reactor.core.publisher.Mono;

import static java.time.LocalDateTime.now;

@Component("ConsoleWebSocketHandler")
@Slf4j
public class ConsoleWebSocketHandler implements WebSocketHandler {
    private static final ObjectMapper json = new ObjectMapper();

    private PositionService positionService;

    public ConsoleWebSocketHandler(PositionService positionService) {
        this.positionService = positionService;
    }

    @Override
    public Mono<Void> handle(WebSocketSession webSocketSession) {
        return webSocketSession.getHandshakeInfo().getPrincipal()
                .cast(UsernamePasswordAuthenticationToken.class)
                .map(t -> (UserPrincipal) t.getPrincipal())
                .flatMap( up -> {
                    return webSocketSession.receive()
                            .map(m -> m.getPayloadAsText())
                            .flatMap(msg -> positionService.send(up,msg))
                            .then();
                });
//                .flatMap( up -> {
//                   log.error("up: {}",up);
//                   return Mono.empty();
//                });
//        return webSocketSession.receive()
//                .map( m -> m.getPayloadAsText())
//                .flatMap( msg -> positionService.)
//                .then();
//        return webSocketSession.send(Mono.just("Hello world")
//                .map(m -> {
//                    try {
//                        return json.writeValueAsString(m);
//                    } catch (JsonProcessingException e) {
//                    }
//                    return "{}";
//                })
//                .map(webSocketSession::textMessage));
    }
}
