package com.supersidor.flightmap.service;

import com.supersidor.flightmap.avro.schemas.Position;
import com.supersidor.flightmap.security.UserPrincipal;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.reactive.ReactiveKafkaProducerTemplate;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;
import tech.allegro.schema.json2avro.converter.JsonAvroConverter;

import java.io.IOException;

@Slf4j
@Service
public class PositionService {
    private static JsonAvroConverter converter = new JsonAvroConverter();
    private ReactiveKafkaProducerTemplate<Long, Position> kafkaTemplate;
    @Value("${kafka.topic.position}")
    private String positionTopicName;
    public PositionService(ReactiveKafkaProducerTemplate<Long, Position> template){
        this.kafkaTemplate = template;
    }
    public Mono<Void> send(UserPrincipal userPrincipal,String posJson){
        log.info("{}",posJson);
        Position position = converter.convertToSpecificRecord(posJson.getBytes(), Position.class, Position.SCHEMA$);
        ProducerRecord<Long, Position> record = new ProducerRecord<>(positionTopicName, userPrincipal.getId(), position);

        return kafkaTemplate.send(record)
                .doOnError(e -> log.error("Send failed",e))
                .doOnNext(r -> System.out.printf("Message #%d send response: %s\n", r.correlationMetadata(), r.recordMetadata()))
                .then();
    }
}
