package com.supersidor.flightmap.service;

import com.supersidor.flightmap.avro.schemas.Position;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.reactive.ReactiveKafkaConsumerTemplate;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.kafka.receiver.ReceiverRecord;

@Service
@Slf4j
public class PositionReceiveService {
    private ReactiveKafkaConsumerTemplate<Long, Position> consumerTemplate;
    private Flux<ReceiverRecord<Long, Position>> sharedFlux;

    PositionReceiveService(ReactiveKafkaConsumerTemplate<Long, Position> consumerTemplate){
        this.consumerTemplate = consumerTemplate;
        //TODO should do seek to end
        sharedFlux = consumerTemplate.receive().publish().autoConnect().doOnNext(
                next -> {
                    log.info("PositionReceiveService:onNext {}",next);
                }
        );
    }

    public Flux<ReceiverRecord<Long, Position>> receive(){
        return  sharedFlux;
    }

}
