package com.supersidor.flightmap.controller;

import com.supersidor.flightmap.exception.ResourceNotFoundException;
import com.supersidor.flightmap.model.User;
import com.supersidor.flightmap.repository.UserRepository;
import com.supersidor.flightmap.security.CurrentUser;
import com.supersidor.flightmap.security.UserPrincipal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.Map;

@RestController
@RequestMapping("/api/user")
public class UserController {

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/me")
    @PreAuthorize("hasRole('USER')")
    public User getCurrentUser(@CurrentUser UserPrincipal userPrincipal) {
        return userRepository.findById(userPrincipal.getId())
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userPrincipal.getId()));
    }
    @GetMapping("/test")
    public String getCurrentUser() {
        return "hello world";
    }
}


