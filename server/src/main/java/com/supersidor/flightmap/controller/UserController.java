package com.supersidor.flightmap.controller;

import com.supersidor.flightmap.repository.UserRepository;
import com.supersidor.flightmap.security.CurrentUser;
import com.supersidor.flightmap.security.UserPrincipal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.Map;

@RestController
public class UserController {

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/user")
    @PreAuthorize("hasRole('USER')")
    public Map<String, Object> getCurrentUser(@CurrentUser UserPrincipal userPrincipal) {
        return Collections.singletonMap("name", userPrincipal.getUsername());
//        return userRepository.findById(userPrincipal.getId())
//                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userPrincipal.getId()));
    }
}

