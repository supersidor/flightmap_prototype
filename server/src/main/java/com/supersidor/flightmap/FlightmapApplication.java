package com.supersidor.flightmap;

import com.supersidor.flightmap.avro.Position;
import com.supersidor.flightmap.config.AppProperties;
import com.supersidor.flightmap.controller.SimControllerTest;
import lombok.extern.slf4j.Slf4j;
import org.apache.avro.Schema;
import org.apache.avro.generic.GenericData;
import org.apache.avro.generic.GenericRecord;
import org.apache.avro.specific.SpecificRecord;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.util.Collections;
import java.util.Map;

@SpringBootApplication
@EnableConfigurationProperties(AppProperties.class)
@Slf4j
public class FlightmapApplication{

	public static void main(String[] args) {
		SpringApplication.run(FlightmapApplication.class, args);
	}

	@Autowired
	SimControllerTest test;
	@Bean
	CommandLineRunner runner(){
		return (args)->{
			log.info("Application started");
//			for (int  i=0;i<1000000;i++){
//				Position pos = new Position();
//				pos.setTitle("test"+i);
//				test.post(pos);
//				log.info("Sent message {}",i);
//			}
		};
	}

}
