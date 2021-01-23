package com.supersidor.flightmap.config;

import com.supersidor.flightmap.avro.schemas.Position;
import com.supersidor.flightmap.kafka.PositionDeserializer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.LongDeserializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.reactive.ReactiveKafkaConsumerTemplate;
import reactor.kafka.receiver.ReceiverOptions;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@Configuration
public class ReactiveKafkaConfiguration {

    @Value("${spring.kafka.bootstrap-servers}") String kafkaUrl;

    @Bean
    Map<String, Object> kafkaConsumerConfiguration() {
        Map<String, Object> configuration = new HashMap<>();
        configuration.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "kafkaUrl");
        configuration.put(ConsumerConfig.GROUP_ID_CONFIG, "groupId");
        configuration.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "latest");
        return configuration;
    }

    @Bean
    ReceiverOptions<Long, Position> kafkaReceiverOptions(@Value("${kafka.topic.position}") String inTopicName) {
        ReceiverOptions<Long, Position> options = ReceiverOptions.create(kafkaConsumerConfiguration());
        return options.subscription(Arrays.asList(inTopicName))
                .withKeyDeserializer(new LongDeserializer())
                .withValueDeserializer(new PositionDeserializer());
    }

    @Bean
    ReactiveKafkaConsumerTemplate<Long, Position> kafkaConsumerTemplate(ReceiverOptions<Long, Position> kafkaReceiverOptions){
        return new ReactiveKafkaConsumerTemplate(kafkaReceiverOptions);
    }

}
