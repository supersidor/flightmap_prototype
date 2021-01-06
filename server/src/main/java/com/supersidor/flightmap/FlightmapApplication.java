package com.supersidor.flightmap;

import com.supersidor.flightmap.config.AppProperties;
import com.supersidor.flightmap.controller.AircraftController;
import com.supersidor.flightmap.controller.SimControllerTest;
import com.supersidor.flightmap.repository.AirflightReactiveRepository;
import com.supersidor.flightmap.service.SequenceGeneratorService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.data.mongodb.repository.config.EnableReactiveMongoRepositories;
import reactor.core.publisher.Mono;

import java.util.UUID;
import java.util.function.Consumer;

import static org.springframework.data.mongodb.core.FindAndModifyOptions.options;
import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.data.mongodb.core.query.Query.query;

@SpringBootApplication
@EnableConfigurationProperties(AppProperties.class)
@Slf4j
@EnableReactiveMongoRepositories
public class FlightmapApplication{

	private AirflightReactiveRepository repository;
	private SequenceGeneratorService sequenceGeneratorService;
	private AircraftController controller;

	public FlightmapApplication(AirflightReactiveRepository repository, SequenceGeneratorService sequenceGeneratorService) {
		this.repository = repository;
		this.sequenceGeneratorService = sequenceGeneratorService;
		this.controller = new AircraftController(this.repository,this.sequenceGeneratorService);
	}

	public static void main(String[] args) {
		SpringApplication.run(FlightmapApplication.class, args);
	}

	@Autowired
	SimControllerTest test;
	@Bean
	CommandLineRunner runner(){
		return (args)->{
			log.info("Application started");
//
//			int threadCount = 10;
//			String aircraftName = "hello_world:"+UUID.randomUUID().toString();
//			Thread threads[] = new Thread[threadCount];
//			for (int i=0;i<threadCount;i++){
//				threads[i] = new Thread( () -> {
//					log.info("Thread started");
//					final Mono<Long> mono = this.controller.registerAircraft(aircraftName);
//					final Long airCraftId = mono.block();
//					log.info("aircraft-id {}",airCraftId);
////					.subscribe( id ->{
////						log.info("aircraft-id {}",id);
////					}, error -> {
////						log.info("error {}",error);
////					});
//				});
//			}
//			for (int i=0;i<threadCount;i++){
//				threads[i].start();
//			}
//			for (int i=0;i<threadCount;i++){
//				threads[i].join();
//			}
//			log.info("FINISHED");
		};
	}

}
