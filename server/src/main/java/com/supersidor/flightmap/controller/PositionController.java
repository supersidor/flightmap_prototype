package com.supersidor.flightmap.controller;

import com.supersidor.flightmap.avro.schemas.Position;
//import com.supersidor.flightmap.security.UserPrincipal;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.beans.factory.annotation.Value;
//import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.kafka.core.reactive.ReactiveKafkaProducerTemplate;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;
import tech.allegro.schema.json2avro.converter.JsonAvroConverter;

import java.io.IOException;

@RestController
@Slf4j
@RequestMapping("/api/position")
public class PositionController {
    private static JsonAvroConverter converter = new JsonAvroConverter();
    private ReactiveKafkaProducerTemplate<Long, Position> kafkaTemplate;
    @Value("${kafka.topic.position}")
    private String positionTopicName;
    public PositionController(ReactiveKafkaProducerTemplate<Long, Position> template){
        this.kafkaTemplate = template;
    }
    @PostMapping
    public Mono<Void> post(/*@AuthenticationPrincipal UserPrincipal userPrincipal, */@RequestBody String posJson) throws IOException {
        log.info("{}",posJson);
        Position position = converter.convertToSpecificRecord(posJson.getBytes(), Position.class, Position.SCHEMA$);
        //ProducerRecord<Long, Position> record = new ProducerRecord<>(positionTopicName, userPrincipal.getId(), position);
        ProducerRecord<Long, Position> record = new ProducerRecord<Long, Position>(positionTopicName, 0L, position);

        return kafkaTemplate.send(record)
                .doOnError(e -> log.error("Send failed",e))
                .doOnNext(r -> System.out.printf("Message #%d send response: %s\n", r.correlationMetadata(), r.recordMetadata()))
                .then();

        //kafkaTemplate.send(record);
    }
}
