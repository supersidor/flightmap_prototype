package com.supersidor.flightmap.controller;

import com.supersidor.flightmap.avro.Position;
import com.supersidor.flightmap.model.Aircraft;
import com.supersidor.flightmap.model.User;
import com.supersidor.flightmap.repository.AircraftRepository;
import com.supersidor.flightmap.security.UserPrincipal;
import lombok.extern.slf4j.Slf4j;
import org.apache.avro.specific.SpecificRecord;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import tech.allegro.schema.json2avro.converter.JsonAvroConverter;

import java.io.IOException;
import java.util.Optional;

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
