package com.supersidor.flightmap.controller;

//import com.supersidor.flightmap.repository.AircraftRepository;
import com.supersidor.flightmap.model.Aircraft;
import com.supersidor.flightmap.repository.AirflightReactiveRepository;
import com.supersidor.flightmap.service.SequenceGeneratorService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;
import reactor.retry.Retry;

import java.io.IOException;

import static reactor.util.retry.Retry.withThrowable;

@RestController
@Slf4j
@RequestMapping("/api/aircraft")
public class AircraftController {
    private AirflightReactiveRepository repository;
    private SequenceGeneratorService sequenceGeneratorService;
    public AircraftController(AirflightReactiveRepository repository,SequenceGeneratorService sequenceGeneratorService){
        this.repository = repository;
        this.sequenceGeneratorService = sequenceGeneratorService;
    }

    @GetMapping("/register")
    public Mono<Long> registerAircraft(@RequestParam String name){
        return repository.findByName(name)
                .switchIfEmpty(createAircraft(name))
                .retryWhen(withThrowable(Retry.anyOf(DuplicateKeyException.class)))
//                .doOnError( err -> {
//                    log.error("registerAircraft error",err);
//                })
                //.retry( err ->  err instanceof DuplicateKeyException)
                .map(aircraft->aircraft.getId());
    }

    private Mono<Aircraft> createAircraft(String name) {
        return  sequenceGeneratorService.generateSequence(Aircraft.SEQUENCE_NAME).flatMap(newAircraftID -> {
            Aircraft aircraft = new Aircraft();
            aircraft.setId(newAircraftID);
            aircraft.setName(name);
            return repository.save(aircraft);
        });

    }
/*
        return sequenceGeneratorService.generateSequence(User.SEQUENCE_NAME).flatMap(newUserId -> {
            User user = new User();
            user.setId(newUserId);
            user.setProvider(AuthProvider.valueOf(oAuth2UserRequest.getClientRegistration().getRegistrationId()));
            user.setProviderId(oAuth2UserInfo.getId());
            user.setName(oAuth2UserInfo.getName());
            user.setEmail(oAuth2UserInfo.getEmail());
            user.setImageUrl(oAuth2UserInfo.getImageUrl());
            return userRepository.save(user);
        });
 */

}
