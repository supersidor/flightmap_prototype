package com.supersidor.flightmap.controller;

import com.supersidor.flightmap.repository.AircraftRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
@Slf4j
@RequestMapping("/api/aircraft")
public class AircraftController {
    private AircraftRepository repository;
    public AircraftController(AircraftRepository repository){
        this.repository = repository;
    }

    @GetMapping("/register")
    public Long registerAircraft(@RequestParam String name) throws IOException {
        return  repository.getAircraftByName(name).getId();
    }




}
