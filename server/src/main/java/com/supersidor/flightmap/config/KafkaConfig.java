package com.supersidor.flightmap.config;

import com.supersidor.flightmap.avro.schemas.Position;
import com.supersidor.flightmap.kafka.PositionSerializer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.LongSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.reactive.ReactiveKafkaProducerTemplate;
import reactor.kafka.sender.SenderOptions;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class KafkaConfig {
    @Value(value = "${spring.kafka.bootstrap-servers}")
    private String bootstrapAddress;

    @Bean
    ReactiveKafkaProducerTemplate<Long, Position> sendTemplate(){
        Map<String, Object> props = new HashMap<>();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapAddress);
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, LongSerializer.class);
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, PositionSerializer.class);
        SenderOptions<Integer, String> senderOptions = SenderOptions.create(props);

        return new ReactiveKafkaProducerTemplate(senderOptions);
    }

}
/*
#    bootstrap-servers: localhost:9092
        #    producer:
        #      key-serializer: org.apache.kafka.common.serialization.LongSerializer
        #      value-serializer: com.supersidor.flightmap.kafka.PositionSerializer
        #      compression-type: gzip

 */
