package com.supersidor.flightmap.service;

import com.supersidor.flightmap.model.AuthProvider;
import com.supersidor.flightmap.model.User;
import com.supersidor.flightmap.repository.UserReactiveRepository;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class UserService {
    private UserReactiveRepository userRepository;
    private SequenceGeneratorService sequenceGeneratorService;

    public UserService(UserReactiveRepository userRepository, SequenceGeneratorService sequenceGeneratorService) {
        this.userRepository = userRepository;
        this.sequenceGeneratorService = sequenceGeneratorService;
    }

    public Mono<User> findById(Long id) {
        return userRepository.findById(id);
    }

    public Mono<User> findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public Mono<User> update(User user) {
        //TODO check that id is not null
        return userRepository.save(user);
    }

    public Mono<User> create(User user) {
        return sequenceGeneratorService.generateSequence(User.SEQUENCE_NAME).flatMap(newUserId -> {
            user.setId(newUserId);
            return userRepository.save(user);
        });

    }
}
