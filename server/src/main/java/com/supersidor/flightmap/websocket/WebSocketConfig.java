package com.supersidor.flightmap.websocket;

import com.supersidor.flightmap.security.CustomUserDetailsService;
import com.supersidor.flightmap.security.TokenProvider;
import com.supersidor.flightmap.service.CurrentPositionService;
import com.supersidor.flightmap.service.PeriodicSendPositionTask;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.web.messaging.MessageSecurityMetadataSourceRegistry;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.util.StringUtils;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;
import org.springframework.web.socket.server.standard.ServletServerContainerFactoryBean;

@Configuration
@EnableWebSocketMessageBroker
@EnableScheduling
@Slf4j
@Order(Ordered.HIGHEST_PRECEDENCE + 99)
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Autowired
    private TokenProvider tokenProvider;

    @Autowired
    private CustomUserDetailsService customUserDetailsService;

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        config.enableSimpleBroker("/topic","/queue");
        config.setApplicationDestinationPrefixes("/app");
    }
//    @Bean
//    public ServletServerContainerFactoryBean createWebSocketContainer() {
//        ServletServerContainerFactoryBean container = new ServletServerContainerFactoryBean();
//        container.setMaxSessionIdleTimeout(600000L);
//        container.setMaxTextMessageBufferSize(8192);
//        container.setMaxBinaryMessageBufferSize(8192);
//       // container.setMaxBinaryMessageBufferSize(8192);\container
//        return container;
//    }
    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry
                .addEndpoint("/ws")
                .setAllowedOrigins("*");
        //.setHandshakeHandler(new CustomHandshakeHandler());

//		registry
//				.addEndpoint("/gs-guide-websocket")
//				.setAllowedOrigins("*")
//				.setHandshakeHandler(new CustomHandshakeHandler())
//				.withSockJS();

        //registry.addEndpoint("/gs-guide-websocket").setHandshakeHandler(new CustomHandshakeHandler()).withSockJS();
    }

    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        registration.interceptors(new ChannelInterceptor() {
            @Override
            public Message<?> preSend(Message<?> message, MessageChannel channel) {
                StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
                if (StompCommand.CONNECT.equals(accessor.getCommand())) {
                    //TODO check for arguments
                    String jwt = accessor.getNativeHeader("Authentication").get(0).replace("Bearer ", "");
                    //accessor.setUser(new StompPrincipal(bearerToken));
                    if (StringUtils.hasText(jwt) && tokenProvider.validateToken(jwt)) {
                        Long userId = tokenProvider.getUserIdFromToken(jwt);
                        long start = System.currentTimeMillis();
                        UserDetails userDetails = customUserDetailsService.loadUserById(userId);
                        long duration = System.currentTimeMillis() - start;
                        log.info("duration: {} ", duration);
                        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                        accessor.setUser(authentication);
                        SecurityContextHolder.getContext().setAuthentication(authentication);
                    }
//
//					Optional.ofNullable(accessor.getNativeHeader("Authorization")).ifPresent(ah -> {
//						log.debug("Received bearer token {}", bearerToken);
//						JWSAuthenticationToken token = (JWSAuthenticationToken) authenticationManager
//								.authenticate(new JWSAuthenticationToken(bearerToken));
//						accessor.setUser(token);
//					});
                }
                return message;
            }
        });
    }
    @Bean
    public PeriodicSendPositionTask task(CurrentPositionService currentPositionService, SimpMessagingTemplate simpMessagingTemplate) {
        return new PeriodicSendPositionTask(currentPositionService,simpMessagingTemplate);
    }

}
