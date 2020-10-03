package com.supersidor.flightmap;

import com.supersidor.avro.Position;
import lombok.extern.slf4j.Slf4j;
import org.apache.avro.specific.SpecificRecord;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.*;
import tech.allegro.schema.json2avro.converter.JsonAvroConverter;

import java.io.IOException;

@RestController
@Slf4j
@RequestMapping("/sim")
public class SimController {
    private static JsonAvroConverter converter = new JsonAvroConverter();
    private KafkaTemplate<String, SpecificRecord> kafkaTemplate;
    @Value("${kafka.topic.position}")
    private String positionTopicName;
    public SimController(KafkaTemplate<String, SpecificRecord> template){
        this.kafkaTemplate = template;
    }
    @PostMapping
    public void post(@RequestBody String posJson) throws IOException {
        log.info("{}",posJson);
        Position position = converter.convertToSpecificRecord(posJson.getBytes(), Position.class, Position.SCHEMA$);
        ProducerRecord<String, SpecificRecord> record = new ProducerRecord<>(positionTopicName, null, position);
        kafkaTemplate.send(record);
    }
}
