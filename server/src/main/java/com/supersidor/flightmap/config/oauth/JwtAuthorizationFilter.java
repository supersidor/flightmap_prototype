package com.supersidor.flightmap.config.oauth;

import com.supersidor.flightmap.exception.ResourceNotFoundException;
import com.supersidor.flightmap.repository.UserReactiveRepository;
import com.supersidor.flightmap.security.TokenProvider;
import com.supersidor.flightmap.security.UserPrincipal;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

@Component
public class JwtAuthorizationFilter implements WebFilter {

//    @Value("${jwt.TOKEN_PREFIX}")
//    private String TOKEN_PREFIX;

    //    @Autowired
//    private JwtTokenUtility jwtTokenUtility;
    private UserReactiveRepository userRepository;
    private TokenProvider tokenProvider;

    public JwtAuthorizationFilter(UserReactiveRepository userRepository, TokenProvider tokenProvider) {
        this.userRepository = userRepository;
        this.tokenProvider = tokenProvider;
    }


    private static final Logger log = LoggerFactory.getLogger(JwtAuthorizationFilter.class);
    private static final String TOKEN_PREFIX = "Bearer ";
    private static final String HEADER_UPGRADE = "Upgrade";
    private static final String HEADER_UPGRADE_WEBSOCKET = "websocket";
    private static final String QUERY_TOKEN = "token";

    @Override
    public Mono<Void> filter(ServerWebExchange serverWebExchange, WebFilterChain webFilterChain) {
        log.info("Request {} called", serverWebExchange.getRequest().getPath().value());
        String jwt = null;

        final HttpHeaders headers = serverWebExchange.getRequest().getHeaders();
        String authorizationHeader = headers.getFirst(HttpHeaders.AUTHORIZATION);
        if (!StringUtils.isEmpty(authorizationHeader) && (authorizationHeader.startsWith(TOKEN_PREFIX))) {
            jwt = authorizationHeader.substring(TOKEN_PREFIX.length());
        }
        //if (queryParams.containsKey(""))

        final String upgradeValue = headers.getFirst(HEADER_UPGRADE);
        if (!StringUtils.isEmpty(upgradeValue)) {
            if (upgradeValue.equals(HEADER_UPGRADE_WEBSOCKET)){
                final MultiValueMap<String, String> queryParams = serverWebExchange.getRequest().getQueryParams();
                jwt = queryParams.getFirst(QUERY_TOKEN);
            }

        }

//        String webSocketProtocolAsJwt = headers.getFirst(WEBSOCKET_PROTOCOL);
//        if (!StringUtils.isEmpty(webSocketProtocolAsJwt)) {
//            jwt = webSocketProtocolAsJwt;
//        }

        if (jwt==null)
            return webFilterChain.filter(serverWebExchange);


        if (!StringUtils.hasText(jwt) || !tokenProvider.validateToken(jwt)) {
            return webFilterChain.filter(serverWebExchange);

//            // start = System.currentTimeMillis();
//            UserDetails userDetails = customUserDetailsService.loadUserById(userId);
//            //long duration =  System.currentTimeMillis() - start;
//            //logger.info("duration: {} ",duration);
//            UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
//            authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
//
//            SecurityContextHolder.getContext().setAuthentication(authentication);
        }
        Long userId = tokenProvider.getUserIdFromToken(jwt);

        return userRepository.findById(userId)
                .switchIfEmpty(Mono.error(new ResourceNotFoundException("User", "id", userId)))
                .map(user -> UserPrincipal.create(user))
                .map(principal -> new UsernamePasswordAuthenticationToken(principal, null, principal.getAuthorities()))
                .flatMap(auth -> {
                    log.info("setting the reactive context");
                    return webFilterChain.filter(serverWebExchange).subscriberContext(c -> ReactiveSecurityContextHolder.withAuthentication(auth)).onErrorResume(AuthenticationException.class, e -> {
                        log.error("Authentication Exception", e);
                        return webFilterChain.filter(serverWebExchange);
                    });
                });


//        return jwtTokenUtility.getUsernamePasswordAuthenticationTokenFromJwt(authorizationHeader).flatMap(auth -> {
//            log.info("setting the reactive context");
//
//            return webFilterChain.filter(serverWebExchange).subscriberContext(c -> ReactiveSecurityContextHolder.withAuthentication(auth)).onErrorResume(AuthenticationException.class, e -> {
//                log.error("Authentication Exception", e);
//                return webFilterChain.filter(serverWebExchange);
//            });
//
//        });
    }
}
/*
    public Mono<UsernamePasswordAuthenticationToken> getUsernamePasswordAuthenticationTokenFromJwt(String jwt) {
        try {
            var jwtBody = Jwts.parser().setSigningKey(JWT_SECRET).parseClaimsJws(jwt.replace("Bearer ", "")).getBody();
            String username = jwtBody.getSubject();
            return userRepository.findByUsernameOrEmail(username, username).switchIfEmpty(Mono.error(new UsernameNotFoundException("Username or email dont exist! " + username)))
                    .flatMap(user -> {
                        Set<GrantedAuthority> roles = user.getRoles().stream().map(role -> new SimpleGrantedAuthority(role.getName().name())).collect(Collectors.toSet());
                        CustomUserDetails currentUser = new CustomUserDetails(user.getId(), user.getName(), user.getUsername(), user.getEmail(), user.getPassword(), user.getVerified(), roles, null);
                        return Mono.just(new UsernamePasswordAuthenticationToken(currentUser, null, roles));
                    });
        } catch (ExpiredJwtException exception) {
            log.warn("Request to parse expired JWT : {} failed : {}", jwt, exception.getMessage());
        } catch (UnsupportedJwtException exception) {
            log.warn("Request to parse unsupported JWT : {} failed : {}", jwt, exception.getMessage());
        } catch (MalformedJwtException exception) {
            log.warn("Request to parse invalid JWT : {} failed : {}", jwt, exception.getMessage());
        } catch (SignatureException exception) {
            log.warn("Request to parse JWT with invalid signature : {} failed : {}", jwt, exception.getMessage());
        } catch (IllegalArgumentException exception) {
            log.warn("Request to parse empty or null JWT : {} failed : {}", jwt, exception.getMessage());
        }
        return null;
    }
 */
