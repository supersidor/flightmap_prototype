package com.supersidor.flightmap.service;

import com.supersidor.flightmap.avro.schemas.Position;
import org.springframework.kafka.core.reactive.ReactiveKafkaConsumerTemplate;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.kafka.receiver.ReceiverRecord;

@Service
public class PositionReceiveService {
    private ReactiveKafkaConsumerTemplate<Long, Position> consumerTemplate;

    PositionReceiveService(ReactiveKafkaConsumerTemplate<Long, Position> consumerTemplate){
        this.consumerTemplate = consumerTemplate;
    }

    public Flux<ReceiverRecord<Long, Position>> receive(){
        return  consumerTemplate.receive();
    }

}
