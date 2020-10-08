package com.supersidor.flightmap.repository;

import com.supersidor.flightmap.model.Aircraft;
import com.supersidor.flightmap.model.User;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Repository
public interface AircraftRepository extends JpaRepository<Aircraft, Long> {

    Optional<Aircraft> findByName(String name);

    @Cacheable("aircrafts")
    @Transactional
    default Aircraft getAircraftByName(String name){
        Optional<Aircraft> aircraft = findByName(name);
        if (aircraft.isPresent())
            return aircraft.get();
        else{
            Aircraft newAircraft = new Aircraft();
            newAircraft.setName(name);
            return save(newAircraft);
        }
    }

}
