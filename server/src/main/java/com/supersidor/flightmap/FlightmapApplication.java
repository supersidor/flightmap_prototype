package com.supersidor.flightmap;

import com.supersidor.avro.Position;
import lombok.extern.slf4j.Slf4j;
import org.apache.avro.Schema;
import org.apache.avro.generic.GenericData;
import org.apache.avro.generic.GenericRecord;
import org.apache.avro.specific.SpecificRecord;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.core.KafkaTemplate;

import java.time.Instant;

@SpringBootApplication
@Slf4j
public class FlightmapApplication {

	public static void main(String[] args) {
		SpringApplication.run(FlightmapApplication.class, args);
	}
//	@Bean
//	public CommandLineRunner run(KafkaTemplate<String, SpecificRecord> template) throws Exception {
//		return args -> {
////avro schema
//			String simpleMessageSchema =
//					"{" +
//							" \"type\": \"record\"," +
//							" \"name\": \"SimpleMessage\"," +
//							" \"namespace\": \"com.codingharbour.avro\"," +
//							" \"fields\": [" +
//							" {\"name\": \"content\", \"type\": \"string\", \"doc\": \"Message content\"}," +
//							" {\"name\": \"date_time\", \"type\": \"string\", \"doc\": \"Datetime when the message\"}" +
//							" ]" +
//							"}";
//
////parse the schema
//			Schema.Parser parser = new Schema.Parser();
//			Schema schema = parser.parse(simpleMessageSchema);
//
////prepare the avro record
//			Position position = new Position();
//			position.setTitle("Test");
//			position.setAltitude(10.0f);
//			position.setLongitude(30.0001);
//			position.setLatitude(50.01322);
//			position.setTimestamp(21312312312312L);
//			position.setHeading(123.323f);
//
//			ProducerRecord<String, SpecificRecord> record = new ProducerRecord<>("avro-topic-2", null, position);
//			template.send(record);
//			log.info("hello world");
//		};
//	}
}
