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
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import tech.allegro.schema.json2avro.converter.JsonAvroConverter;

import java.io.IOException;

@Slf4j
@Service
public class SimControllerTest {
    private static JsonAvroConverter converter = new JsonAvroConverter();
    private KafkaTemplate<String, SpecificRecord> kafkaTemplate;
    @Value("${kafka.topic.position}")
    private String positionTopicName;
    public SimControllerTest(KafkaTemplate<String, SpecificRecord> template){
        this.kafkaTemplate = template;
    }
    public void post(Position pos) throws IOException {
        ProducerRecord<String, SpecificRecord> record = new ProducerRecord<>(positionTopicName, null, pos);
        kafkaTemplate.send(record);
    }
}
