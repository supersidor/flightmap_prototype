package com.supersidor.flightmap.websocket;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.supersidor.flightmap.security.UserPrincipal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.socket.WebSocketHandler;
import org.springframework.web.reactive.socket.WebSocketMessage;
import org.springframework.web.reactive.socket.WebSocketSession;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;

import static java.time.LocalDateTime.now;
import static java.util.UUID.randomUUID;

@Component("ReactiveWebSocketHandler")
@Slf4j
public class ReactiveWebSocketHandler implements WebSocketHandler {

    private static final ObjectMapper json = new ObjectMapper();

    private Flux<String> eventFlux = Flux.generate(sink -> {
        Event event = new Event(randomUUID().toString(), now().toString());
        try {
            sink.next(json.writeValueAsString(event));
        } catch (JsonProcessingException e) {
            sink.error(e);
        }
    });

    private Flux<String> intervalFlux = Flux.interval(Duration.ofMillis(1000L))
            .zipWith(eventFlux, (time, event) -> event);

    @Override
    public Mono<Void> handle(WebSocketSession webSocketSession) {
//        final Mono<UserPrincipal> userPrincipalMono = webSocketSession.getHandshakeInfo()
//                .getPrincipal()
//                .cast(UsernamePasswordAuthenticationToken.class)
//                .map(t -> (UserPrincipal) t.getPrincipal());
//
//        return webSocketSession.send(Flux.combineLatest(arr -> {
//            return (String)arr[0]+"!!!!"+((UserPrincipal)arr[1]).getEmail();
//        },intervalFlux,userPrincipalMono).map(webSocketSession::textMessage));
        //webSocketSession.receive().
//        return webSocketSession.getHandshakeInfo().getPrincipal();
//        UsernamePasswordAuthenticationToken
        return webSocketSession.send(intervalFlux
                .map(webSocketSession::textMessage))
                .and(webSocketSession.receive()
                        .map(WebSocketMessage::getPayloadAsText).log());

        /*
.doOnNext(p -> {
            log.debug("principal: {}",p);
        }).
         */
    }
}
