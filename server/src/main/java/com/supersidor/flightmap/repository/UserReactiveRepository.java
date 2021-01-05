package com.supersidor.flightmap.repository;

import com.supersidor.flightmap.model.Aircraft;
import com.supersidor.flightmap.model.User;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public interface UserReactiveRepository extends ReactiveMongoRepository<User, Long> {
    Mono<User> findByEmail(final String email);
}
