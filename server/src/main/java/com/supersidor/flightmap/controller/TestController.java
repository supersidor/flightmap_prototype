package com.supersidor.flightmap.controller;

import com.supersidor.flightmap.model.TestUser;
import com.supersidor.flightmap.model.User;
import com.supersidor.flightmap.security.TokenProvider;
import com.supersidor.flightmap.service.UserService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/test")
public class TestController {
    private UserService userService;
    private TokenProvider tokenProvider;

    public TestController(UserService userService, TokenProvider tokenProvider) {
        this.userService = userService;
        this.tokenProvider = tokenProvider;
    }

    @GetMapping("user")
    Mono<TestUser> getUser(@RequestParam String userName) {
        return userService.findByEmail(userName)
                .switchIfEmpty(createUser(userName))
                .map( u-> new TestUser(u,tokenProvider.createToken(u.getId())) );
    }

    private Mono<? extends User> createUser(String userName) {
        User u = new User();
        u.setEmail(userName);
        u.setName(userName);
        return userService.create(u);
    }

}
