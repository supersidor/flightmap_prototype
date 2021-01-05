package com.supersidor.flightmap.controller;


import com.supersidor.flightmap.model.Aircraft;
import com.supersidor.flightmap.repository.AirflightReactiveRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.UUID;

@RestController
@Slf4j
public class HomeController {
    private AirflightReactiveRepository repository;

    //    @RequestMapping(value = "/ui/**")
//    public String redirectUi() {
//        return "forward:/index.html";
//    }
//    //@RequestMapping(value = "/")
//    //public String redirectRoot() {
//    //    return "redirect:/ui/";
//    //}
    HomeController(AirflightReactiveRepository repository) {
        //AirflightReactiveRepository repository
        this.repository = repository;
    }

    @RequestMapping(value = "/test")
    public Mono<Aircraft> test() {
        Aircraft aircraft = new Aircraft();
        aircraft.setName("aircraft:" + UUID.randomUUID().toString());

        //.map(aircraft1 -> aircraft.getName());
        return repository.insert(aircraft);
        //return Mono.just("hello world").delayElement(Duration.ofSeconds(1));
        // return Mono.just("hello world");
    }

    @GetMapping("/userInfo")
    public Mono<String> index(@AuthenticationPrincipal Mono<OAuth2User> oauth2User) {
        return oauth2User
                .map(OAuth2User::getName)
                .map(name -> String.format("Hi, %s", name));
    }

}
