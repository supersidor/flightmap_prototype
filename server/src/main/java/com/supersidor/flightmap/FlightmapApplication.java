package com.supersidor.flightmap;

import com.supersidor.flightmap.config.AppProperties;
import com.supersidor.flightmap.controller.SimControllerTest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;

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
		};
	}

}
