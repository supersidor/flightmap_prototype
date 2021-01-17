package com.supersidor.flightmap.controller;

import com.supersidor.flightmap.exception.ResourceNotFoundException;
import com.supersidor.flightmap.model.User;
//import com.supersidor.flightmap.repository.UserRepository;
//import com.supersidor.flightmap.security.UserPrincipal;
import com.supersidor.flightmap.security.UserPrincipal;
import com.supersidor.flightmap.service.UserService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/user")
public class UserController {

    private UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }


//    @Autowired
//    private UserRepository userRepository;

//    @GetMapping("/me")
//    @PreAuthorize("hasRole('USER')")
//    public User getCurrentUser(@AuthenticationPrincipal UserPrincipal userPrincipal) {
//        return userRepository.findById(userPrincipal.getId())
//                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userPrincipal.getId()));
//    }
//    @GetMapping("/me")
//    public Mono<User> getCurrentUser(@AuthenticationPrincipal Mono<OAuth2User> oauth2User) {
//        User user = new User();
//        user.setEmail("sidorka@gmail.com");
//        user.setImageUrl("https://kor.ill.in.ua/m/190x120/2576722.jpg");
//        user.setEmailVerified(true);
//        user.setName("andriy");
//        user.setProvider(AuthProvider.github);
//        return Mono.just(user);
//        //return userRepository.findById(userPrincipal.getId())
//        //        .orElseThrow(() -> new ResourceNotFoundException("User", "id", userPrincipal.getId()));
//
//    }
    @GetMapping("/me")
    public Mono<User> getCurrentUser(@AuthenticationPrincipal UserPrincipal userPrincipal) {

        return userService
                .findById(userPrincipal.getId())
                .switchIfEmpty(Mono.error( new ResourceNotFoundException("User", "id", userPrincipal.getId())));

//        User user = new User();
//        user.setEmail("sidorka@gmail.com");
//        user.setImageUrl("https://kor.ill.in.ua/m/190x120/2576722.jpg");
//        user.setEmailVerified(true);
//        user.setName("andriy");
//        user.setProvider(AuthProvider.github);
//        return Mono.just(user);
//        return userRepository.findById(userPrincipal.getId())
        //        .orElseThrow(() -> new ResourceNotFoundException("User", "id", userPrincipal.getId()));

    }
}


