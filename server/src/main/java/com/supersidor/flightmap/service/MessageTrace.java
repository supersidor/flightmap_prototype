package com.supersidor.flightmap.service;

import com.supersidor.flightmap.avro.Position;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.concurrent.atomic.AtomicLong;

@Service
@Slf4j
public class MessageTrace {

    AtomicLong count = new AtomicLong();
    AtomicLong totalBytes = new AtomicLong();

    @KafkaListener(topics = "${kafka.topic.position}",groupId = "trace3")
    private void consume(ConsumerRecord<String, Position> rec){
        Position value = rec.value();
        //count.incrementAndGet();
        //totalBytes.addAndGet(rec.value().getBytes().length);
        //log.info("{}",value);
        //log.info("avg size: {}",(totalBytes.get()/count.get()));
    }
}
