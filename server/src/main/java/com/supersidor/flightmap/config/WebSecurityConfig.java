package com.supersidor.flightmap.config;

//import com.supersidor.flightmap.security.TokenAuthenticationFilter;
//import com.supersidor.flightmap.security.oauth2.CustomOAuth2UserService;
//import com.supersidor.flightmap.security.oauth2.HttpCookieOAuth2AuthorizationRequestRepository;
//import com.supersidor.flightmap.security.oauth2.OAuth2AuthenticationFailureHandler;
//import com.supersidor.flightmap.security.oauth2.OAuth2AuthenticationSuccessHandler;

import com.supersidor.flightmap.config.oauth.CustomServerAuthorizationRequestRepository;
import com.supersidor.flightmap.config.oauth.JwtAuthorizationFilter;
import com.supersidor.flightmap.security.TokenProvider;
import com.supersidor.flightmap.util.CookieUtility;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpCookie;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.client.registration.ReactiveClientRegistrationRepository;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.ReactiveOAuth2UserService;
import org.springframework.security.oauth2.client.web.server.DefaultServerOAuth2AuthorizationRequestResolver;
import org.springframework.security.oauth2.client.web.server.ServerOAuth2AuthorizationRequestResolver;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.WebFilterExchange;
import org.springframework.security.web.server.authentication.ServerAuthenticationSuccessHandler;
import org.springframework.security.web.server.context.NoOpServerSecurityContextRepository;
import org.springframework.security.web.server.util.matcher.PathPatternParserServerWebExchangeMatcher;
import org.springframework.util.MultiValueMap;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Mono;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URLEncoder;
import java.util.Map;

import static com.supersidor.flightmap.config.oauth.OAuthConst.REDIRECT_URI_PARAM_COOKIE_NAME;

@Configuration
//@EnableWebSecurity
//@EnableGlobalMethodSecurity(
//        securedEnabled = true,
//        jsr250Enabled = true,
//        prePostEnabled = true
//)
@EnableWebFluxSecurity
@Slf4j
public class WebSecurityConfig {

    private CustomServerAuthorizationRequestRepository authorizationRequestRepository;
    private CookieUtility cookieUtility;
    private JwtAuthorizationFilter authFilter;
    private ReactiveOAuth2UserService<OAuth2UserRequest, OAuth2User> oauthUserService;
    private TokenProvider tokenProvider;
    private ReactiveClientRegistrationRepository reactiveClientRegistrationRepository;

    public WebSecurityConfig(CustomServerAuthorizationRequestRepository authorizationRequestRepository,
                             CookieUtility cookieUtility,
                             JwtAuthorizationFilter authFilter,
                             ReactiveOAuth2UserService<OAuth2UserRequest, OAuth2User> oauthUserService,
                             TokenProvider tokenProvider,
                             ReactiveClientRegistrationRepository reactiveClientRegistrationRepository) {
        this.authorizationRequestRepository = authorizationRequestRepository;
        this.cookieUtility = cookieUtility;
        this.authFilter = authFilter;
        this.oauthUserService = oauthUserService;
        this.tokenProvider = tokenProvider;
        this.reactiveClientRegistrationRepository = reactiveClientRegistrationRepository;
    }


    @Bean
    public SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http) {
        PathPatternParserServerWebExchangeMatcher exchangeMatcher = new PathPatternParserServerWebExchangeMatcher("/api/oauth2/authorize/{registrationId}");

        //TODO merge
        http.securityContextRepository(NoOpServerSecurityContextRepository.getInstance()); // stateless sessions, clients must send Authorization header with every request

        http
                .cors()
                    .and()
                .csrf()
                    .disable()
                .formLogin()
                    .disable()
                .httpBasic()
                    .disable()
                //.exceptionHandling()
                //    .authenticationEntryPoint(new HttpStatusServerEntryPoint(HttpStatus.UNAUTHORIZED))
                //.and()
                .authorizeExchange()
                    .pathMatchers("/",
                        "/leafletjs/**",
                        "/error",
                        "/ws",
                        "/ui/**",
                        "/*.js",
                        "/favicon.ico",
                        "/manifest.json",
                        "/**/*.png",
                        "/**/*.gif",
                        "/**/*.svg",
                        "/**/*.jpg",
                        "/**/*.html",
                        "/**/*.css",
                        "/oauth/**",
                        "/oauth2/**",
                        "/login/**",
                        "/test/**",
                        "/api/oauth2/**",
                        "/static/**")
                        .permitAll()
                    .anyExchange()
                        .authenticated()
                .and()
                //oauth2 -> oauth2.authenticationMatcher(new PathPatternParserServerWebExchangeMatcher("/login/oauth2/code/{registrationId}"))
                .oauth2Login()
//                   .authenticationMatcher(new ServerWebExchangeMatcher(){
//
//                       @Override
//                       public Mono<MatchResult> matches(ServerWebExchange exchange) {
//                           return exchangeMatcher.matches(exchange).doOnNext( m-> {
//                               if (m.isMatch()){
//                                   int  i=0;
//                                   i++;
//                               }
//                           });
//                       }
//                   })
//                .authenticationFailureHandler(new ServerAuthenticationFailureHandler(){
//                    @Override
//                    public Mono<Void> onAuthenticationFailure(WebFilterExchange webFilterExchange, AuthenticationException exception) {
//                        return null;
//                    }
//                })
                .authenticationSuccessHandler(new ServerAuthenticationSuccessHandler() {
                    @Override
                    public Mono<Void> onAuthenticationSuccess(WebFilterExchange webFilterExchange, Authentication authentication) {
                        if (authentication instanceof OAuth2AuthenticationToken) {
                            ServerHttpRequest request = webFilterExchange.getExchange().getRequest();
                            MultiValueMap<String, HttpCookie> cookieMap = request.getCookies();


                            String redirectUri = cookieUtility.getCookieValue(cookieMap, REDIRECT_URI_PARAM_COOKIE_NAME);

                            log.info("redirectURi {}", redirectUri);


                            ServerHttpResponse response = webFilterExchange.getExchange().getResponse();

                            if (response.isCommitted()) {
                                return Mono.empty();
                            }

                            String token = tokenProvider.createToken(authentication);

                            String location = null;
                            try {
                                location = UriComponentsBuilder.fromUriString(redirectUri).queryParam("token", URLEncoder.encode(token, "UTF-8")).build().toUriString();
                            } catch (UnsupportedEncodingException e) {
                                e.printStackTrace();
                            }

                            response.setStatusCode(HttpStatus.PERMANENT_REDIRECT);
                            response.getHeaders().setLocation(URI.create(location));

                            return Mono.empty();

//                            return userRepository.existsByEmail((String) userAttributes.get("email")).flatMap(exists -> {
//                                OAuth2UserAttributes oAuth2UserAttributes = OAuth2ProviderFactory.getOAuth2UserAttributesByProvider((OAuth2AuthenticationToken) authentication, userAttributes);
//
//                                String jwt = TOKEN_PREFIX + " " + jwtTokenUtility.generateToken(oAuth2UserAttributes.getEmail());
//
//                                String location = null;
//                                try {
//                                    location = UriComponentsBuilder.fromUriString(redirectUri).queryParam("token", URLEncoder.encode(jwt, "UTF-8")).build().toUriString();
//                                } catch (UnsupportedEncodingException e) {
//                                    e.printStackTrace();
//                                }
//
//                                // clear authentication attributes and cookies are no longer needed
//                                cookieUtility.deleteCookie(response, cookieMap, REDIRECT_COOKIE);
//                                cookieUtility.deleteCookie(response, cookieMap, OAUTH2_AUTHORIZATION_COOKIE);
//
//                                response.setStatusCode(HttpStatus.PERMANENT_REDIRECT);
//                                response.getHeaders().setLocation(URI.create(location));
//
//                                log.info("location {}", location);
//
//                                if (!exists) {
//                                    log.info("user doesnt exist, creating him!");
//                                    // Register the new user
//                                    UserEntity userEntity = new UserEntity();
//                                    userEntity.setName(oAuth2UserAttributes.getName());
//                                    // use email but make the user input the username
//                                    userEntity.setUsername(oAuth2UserAttributes.getEmail());
//                                    userEntity.setEmail(oAuth2UserAttributes.getEmail());
//                                    userEntity.setImageUrl(oAuth2UserAttributes.getPictureUrl());
//                                    userEntity.setProviderType(((OAuth2AuthenticationToken) authentication).getAuthorizedClientRegistrationId());
//
//                                    userEntity.setToken(null);
//                                    userEntity.setVerified(true);
//
//                                    return roleRepository.findByName(RoleType.ROLE_USER).flatMap(role -> {
//                                        userEntity.setRoles(Collections.singleton(role));
//                                        System.out.println(userEntity);
//                                        return userRepository.save(userEntity).flatMap(user -> Mono.empty());
//                                    });
//                                } else { // User already exists
//                                    log.info("user exists!");
//                                    return Mono.empty();
//                                }
//                            });

                        } else return Mono.empty();
                    }
                })
                .authorizationRequestRepository(authorizationRequestRepository)
                .authorizationRequestResolver(getAuthorizationRequestResolver())
                .authenticationMatcher(new PathPatternParserServerWebExchangeMatcher("/api/oauth2/callback/{registrationId}"));
        //.authenticationMatcher(new PathPatternParserServerWebExchangeMatcher("/api/oauth2/callback/{registrationId}")).and();

//                .and()
//                .build();

        http.addFilterBefore(authFilter, SecurityWebFiltersOrder.HTTP_BASIC);
        return http.build();

//                .oauth2Login()
//                .authorizationEndpoint()
//                    .baseUri("/api/oauth2/authorize")
//                    .authorizationRequestRepository(cookieAuthorizationRequestRepository())
//                    .and()
//                .redirectionEndpoint()
//                    .baseUri("/api/oauth2/callback/*")
//                    .and()
//                .userInfoEndpoint()
//                    .userService(customOAuth2UserService)
//                    .and()
//                .successHandler(oAuth2AuthenticationSuccessHandler)
//                .failureHandler(oAuth2AuthenticationFailureHandler);
    }

    //    private OAuth2AuthenticationFailureHandler oAuth2AuthenticationFailureHandler;
//
//    private OAuth2AuthenticationSuccessHandler oAuth2AuthenticationSuccessHandler;
//
//    private CustomOAuth2UserService customOAuth2UserService;
//
//    public WebSecurityConfig(OAuth2AuthenticationFailureHandler oAuth2AuthenticationFailureHandler, OAuth2AuthenticationSuccessHandler oAuth2AuthenticationSuccessHandler, CustomOAuth2UserService customOAuth2UserService) {
//        this.oAuth2AuthenticationFailureHandler = oAuth2AuthenticationFailureHandler;
//        this.oAuth2AuthenticationSuccessHandler = oAuth2AuthenticationSuccessHandler;
//        this.customOAuth2UserService = customOAuth2UserService;
//    }
    private ServerOAuth2AuthorizationRequestResolver getAuthorizationRequestResolver() {
        return new DefaultServerOAuth2AuthorizationRequestResolver(
                this.reactiveClientRegistrationRepository,
                new PathPatternParserServerWebExchangeMatcher(
                        "/api/oauth2/authorize/{registrationId}"));
//getClientRegistrationRepository
    }

//    @Override
//    protected void configure(HttpSecurity http) throws Exception {
//        //TODO ws authentification
//        http
//                .cors()
//                    .and()
//                .sessionManagement()
//                    .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
//                    .and()
//                .csrf()
//                    .disable()
//                .formLogin()
//                    .disable()
//                .httpBasic()
//                    .disable()
//                .exceptionHandling()
//                    .authenticationEntryPoint(new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED))
//                    .and()
//                .authorizeRequests()
//                    .antMatchers("/",
//                        "/leafletjs/**",
//                        "/error",
//                        "/ws",
//                        "/ui/**",
//                        "/*.js",
//                        "/favicon.ico",
//                        "/manifest.json",
//                        "/**/*.png",
//                        "/**/*.gif",
//                        "/**/*.svg",
//                        "/**/*.jpg",
//                        "/**/*.html",
//                        "/**/*.css",
//                        "/oauth2/**",
//                        "/static/**")
//                        .permitAll()
//                    .anyRequest()
//                        .authenticated()
//                    .and()
//                .oauth2Login()
//                .authorizationEndpoint()
//                    .baseUri("/api/oauth2/authorize")
//                    .authorizationRequestRepository(cookieAuthorizationRequestRepository())
//                    .and()
//                .redirectionEndpoint()
//                    .baseUri("/api/oauth2/callback/*")
//                    .and()
//                .userInfoEndpoint()
//                    .userService(customOAuth2UserService)
//                    .and()
//                .successHandler(oAuth2AuthenticationSuccessHandler)
//                .failureHandler(oAuth2AuthenticationFailureHandler);
//
//        http.addFilterBefore(tokenAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);
//    }

//    @Bean
//    public TokenAuthenticationFilter tokenAuthenticationFilter() {
//        return new TokenAuthenticationFilter();
//    }

    /*
      By default, Spring OAuth2 uses HttpSessionOAuth2AuthorizationRequestRepository to save
      the authorization request. But, since our service is stateless, we can't save it in
      the session. We'll save the request in a Base64 encoded cookie instead.
    */
//    @Bean
//    public HttpCookieOAuth2AuthorizationRequestRepository cookieAuthorizationRequestRepository() {
//        return new HttpCookieOAuth2AuthorizationRequestRepository();
//    }

    //NOT USED
//    @Override
//    public void configure(AuthenticationManagerBuilder authenticationManagerBuilder) throws Exception {
//        authenticationManagerBuilder
//                .userDetailsService(customUserDetailsService)
//                .passwordEncoder(passwordEncoder());
//    }

//    @Bean
//    public PasswordEncoder passwordEncoder() {
//        return new BCryptPasswordEncoder();
//    }

//    @Bean(BeanIds.AUTHENTICATION_MANAGER)
//    @Override
//    public AuthenticationManager authenticationManagerBean() throws Exception {
//        return super.authenticationManagerBean();
//    }

}
