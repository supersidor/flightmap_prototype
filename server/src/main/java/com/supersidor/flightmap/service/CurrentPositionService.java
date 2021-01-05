package com.supersidor.flightmap.service;

import com.supersidor.flightmap.avro.schemas.Position;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.Optional;

//@Service
@Slf4j
public class CurrentPositionService {
    volatile Position lastPosition = null;
    @KafkaListener(
            topics = "${kafka.topic.position}",
            groupId = "current_position",
            properties={"auto.offset.reset:latest"})
    private void consume(ConsumerRecord<String, Position> rec){
        lastPosition = rec.value();
    }
    public Optional<Position> getPosition(){
        return Optional.ofNullable(lastPosition);
    }
}
