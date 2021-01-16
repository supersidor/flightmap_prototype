package com.supersidor.flightmap.websocket;

import org.springframework.core.io.buffer.NettyDataBufferFactory;
import org.springframework.http.server.reactive.AbstractServerHttpResponse;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.http.server.reactive.ServerHttpResponseDecorator;
import org.springframework.lang.Nullable;
import org.springframework.web.reactive.socket.HandshakeInfo;
import org.springframework.web.reactive.socket.WebSocketHandler;
import org.springframework.web.reactive.socket.adapter.ReactorNettyWebSocketSession;
import org.springframework.web.reactive.socket.server.upgrade.ReactorNettyRequestUpgradeStrategy;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import reactor.netty.http.server.HttpServerResponse;
import reactor.netty.http.server.WebsocketServerSpec;

import java.net.URI;
import java.util.function.Supplier;

public class CustomRequestUpgradeStrategy extends ReactorNettyRequestUpgradeStrategy {
//    @Override
//    public Mono<Void> upgrade(ServerWebExchange exchange, WebSocketHandler handler,
//                              @Nullable String subProtocol, Supplier<HandshakeInfo> handshakeInfoFactory) {
//
//        ServerHttpResponse response = exchange.getResponse();
//        HttpServerResponse reactorResponse = getNativeResponse(response);
//        HandshakeInfo handshakeInfo = handshakeInfoFactory.get();
//        NettyDataBufferFactory bufferFactory = (NettyDataBufferFactory) response.bufferFactory();
//        URI uri = exchange.getRequest().getURI();
//
//        // Trigger WebFlux preCommit actions and upgrade
//        return response.setComplete()
//                .then(Mono.defer(() -> {
//                    WebsocketServerSpec spec = buildSpec(subProtocol);
//                    return reactorResponse.sendWebsocket((in, out) -> {
//                        ReactorNettyWebSocketSession session =
//                                new ReactorNettyWebSocketSession(
//                                        in, out, handshakeInfo, bufferFactory, spec.maxFramePayloadLength());
//                        return handler.handle(session).checkpoint(uri + " [ReactorNettyRequestUpgradeStrategy]");
//                    }, spec);
//                }));
//    }
//
//    private static HttpServerResponse getNativeResponse(ServerHttpResponse response) {
//        if (response instanceof AbstractServerHttpResponse) {
//            return ((AbstractServerHttpResponse) response).getNativeResponse();
//        }
//        else if (response instanceof ServerHttpResponseDecorator) {
//            return getNativeResponse(((ServerHttpResponseDecorator) response).getDelegate());
//        }
//        else {
//            throw new IllegalArgumentException(
//                    "Couldn't find native response in " + response.getClass().getName());
//        }
//    }
//
//    WebsocketServerSpec buildSpec(@Nullable String subProtocol) {
//        WebsocketServerSpec.Builder builder = this.specBuilderSupplier.get();
//        if (subProtocol != null) {
//            builder.protocols(subProtocol);
//        }
//        if (this.maxFramePayloadLength != null) {
//            builder.maxFramePayloadLength(this.maxFramePayloadLength);
//        }
//        if (this.handlePing != null) {
//            builder.handlePing(this.handlePing);
//        }
//        return builder.build();
//    }

}
