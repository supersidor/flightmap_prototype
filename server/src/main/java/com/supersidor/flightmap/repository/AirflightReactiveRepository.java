package com.supersidor.flightmap.repository;

import com.supersidor.flightmap.model.Aircraft;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
public interface AirflightReactiveRepository extends ReactiveMongoRepository<Aircraft, String> {
    Mono<Aircraft> findByName(final String name);
}
