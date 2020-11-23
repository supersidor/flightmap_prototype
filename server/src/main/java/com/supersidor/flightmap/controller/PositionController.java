package com.supersidor.flightmap.controller;

import com.supersidor.flightmap.avro.schemas.Position;
import com.supersidor.flightmap.security.UserPrincipal;
import lombok.extern.slf4j.Slf4j;
import org.apache.avro.specific.SpecificRecord;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import tech.allegro.schema.json2avro.converter.JsonAvroConverter;

import java.io.IOException;

@RestController
@Slf4j
@RequestMapping("/api/position")
public class PositionController {
    private static JsonAvroConverter converter = new JsonAvroConverter();
    private KafkaTemplate<Long, Position> kafkaTemplate;
    @Value("${kafka.topic.position}")
    private String positionTopicName;
    public PositionController(KafkaTemplate<Long, Position> template){
        this.kafkaTemplate = template;
    }
    @PostMapping
    public void post(@AuthenticationPrincipal UserPrincipal userPrincipal, @RequestBody String posJson) throws IOException {
        log.info("{}",posJson);
        Position position = converter.convertToSpecificRecord(posJson.getBytes(), Position.class, Position.SCHEMA$);
        ProducerRecord<Long, Position> record = new ProducerRecord<>(positionTopicName, userPrincipal.getId(), position);
        kafkaTemplate.send(record);
    }
}
