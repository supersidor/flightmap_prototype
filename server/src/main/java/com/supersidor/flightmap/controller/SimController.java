package com.supersidor.flightmap.controller;

import com.supersidor.flightmap.avro.Position;
import com.supersidor.flightmap.security.UserPrincipal;
import lombok.extern.slf4j.Slf4j;
import org.apache.avro.specific.SpecificRecord;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import tech.allegro.schema.json2avro.converter.JsonAvroConverter;

import java.io.IOException;

@RestController
@Slf4j
@RequestMapping("/api/sim")
public class SimController {
    private static JsonAvroConverter converter = new JsonAvroConverter();
    private KafkaTemplate<String, SpecificRecord> kafkaTemplate;
    @Value("${kafka.topic.position}")
    private String positionTopicName;
    public SimController(KafkaTemplate<String, SpecificRecord> template){
        this.kafkaTemplate = template;
    }
    @PostMapping
    @PreAuthorize("hasRole('USER')")
    public void post(@AuthenticationPrincipal UserPrincipal userPrincipal, @RequestBody String posJson) throws IOException {
        log.info("{}",posJson);
        Position position = converter.convertToSpecificRecord(posJson.getBytes(), Position.class, Position.SCHEMA$);
        position.setUserId(userPrincipal.getId());
        ProducerRecord<String, SpecificRecord> record = new ProducerRecord<>(positionTopicName, null, position);
        kafkaTemplate.send(record);
    }
}
