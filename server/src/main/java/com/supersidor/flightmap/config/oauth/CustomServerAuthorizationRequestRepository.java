package com.supersidor.flightmap.config.oauth;

import com.supersidor.flightmap.util.CookieUtility;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpCookie;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.security.oauth2.client.web.server.ServerAuthorizationRequestRepository;
import org.springframework.security.oauth2.core.endpoint.OAuth2AuthorizationRequest;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;
import org.springframework.util.SerializationUtils;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.Base64;

import static com.supersidor.flightmap.config.oauth.OAuthConst.*;

@Service
public class CustomServerAuthorizationRequestRepository implements ServerAuthorizationRequestRepository<OAuth2AuthorizationRequest> {

    @Autowired
    private CookieUtility cookieUtility;

    @Override
    public Mono<OAuth2AuthorizationRequest> loadAuthorizationRequest(ServerWebExchange serverWebExchange) {
        MultiValueMap<String, HttpCookie> cookieMap = serverWebExchange.getRequest().getCookies();

        if (cookieMap != null) {
            String oauth2CookieValue = cookieUtility.getCookieValue(cookieMap, OAUTH2_AUTHORIZATION_REQUEST_COOKIE_NAME);
            if (oauth2CookieValue!=null){
                return Mono.just(OAuth2AuthorizationRequest.class
                        .cast(SerializationUtils.deserialize(Base64.getUrlDecoder().decode(oauth2CookieValue))));
            }else{
                return Mono.empty();
            }
        } else {
            return Mono.empty();
        }
    }

    @Override
    public Mono<Void> saveAuthorizationRequest(OAuth2AuthorizationRequest oAuth2AuthorizationRequest, ServerWebExchange serverWebExchange) {
        if (oAuth2AuthorizationRequest == null) {
            MultiValueMap<String, HttpCookie> cookieMap = serverWebExchange.getRequest().getCookies();

            if (cookieMap != null) {
                ServerHttpResponse response = serverWebExchange.getResponse();
                cookieUtility.deleteCookie(response, cookieMap, OAUTH2_AUTHORIZATION_REQUEST_COOKIE_NAME);
                cookieUtility.deleteCookie(response, cookieMap, REDIRECT_URI_PARAM_COOKIE_NAME);
            }
            return Mono.empty();
        } else {
            ServerHttpResponse response = serverWebExchange.getResponse();
            cookieUtility.addCookie(response, OAUTH2_AUTHORIZATION_REQUEST_COOKIE_NAME, Base64.getUrlEncoder().encodeToString(SerializationUtils.serialize(oAuth2AuthorizationRequest)), "", COOKIE_EXPIRE_SECONDS);
            cookieUtility.addCookie(response, REDIRECT_URI_PARAM_COOKIE_NAME, serverWebExchange.getRequest().getQueryParams().getFirst(REDIRECT_URI_PARAM_COOKIE_NAME), "", COOKIE_EXPIRE_SECONDS);

            return Mono.empty();
        }
    }

    @Override
    public Mono<OAuth2AuthorizationRequest> removeAuthorizationRequest(ServerWebExchange serverWebExchange) {
        return this.loadAuthorizationRequest(serverWebExchange);
    }
}
