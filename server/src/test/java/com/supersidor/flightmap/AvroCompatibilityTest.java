package com.supersidor.flightmap;

import com.supersidor.flightmap.avro.schemas.Position;
import com.supersidor.flightmap.avro.schemas.PositionTestV1;
import com.supersidor.flightmap.avro.schemas.PositionTestV2;
import com.supersidor.flightmap.kafka.PositionDeserializer;
import com.supersidor.flightmap.kafka.PositionSerializer;
import org.apache.avro.Schema;
import org.apache.avro.io.BinaryDecoder;
import org.apache.avro.io.DecoderFactory;
import org.apache.avro.specific.SpecificDatumReader;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.ClassPathResource;
import tech.allegro.schema.json2avro.converter.JsonAvroConverter;

import java.io.IOException;
import java.nio.file.Files;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class AvroCompatibilityTest {

    String jsonPositionV1 = """
    {
    "aircraftId":1,
    "altitude":100,
    "longitude":200,
    "latitude":200,
    "heading":10,
    "timestamp":20
    }
    """;


    static Schema load(String resourceName) throws IOException {
        String schemaStr = Files.readString(new ClassPathResource(resourceName).getFile().toPath());
        Schema.Parser  p = new Schema.Parser();
        return p.parse(schemaStr);

    }
    @Test
    void saveV1LoadV1() throws IOException {
        JsonAvroConverter converter = new JsonAvroConverter();
        byte[] bytesAvro = converter.convertToAvro(jsonPositionV1.getBytes(), PositionTestV1.SCHEMA$);

        BinaryDecoder binaryDecoder = DecoderFactory.get().binaryDecoder(bytesAvro, null);
        SpecificDatumReader<PositionTestV1> reader = new SpecificDatumReader( PositionTestV1.SCHEMA$, PositionTestV1.SCHEMA$);
        PositionTestV1 positionV1 = reader.read(null, binaryDecoder);
        assertEquals(1,positionV1.getAircraftId());
    }
    @Test
    void saveV1LoadV1Test() throws IOException {
        JsonAvroConverter converter = new JsonAvroConverter();
        byte[] bytesAvro = converter.convertToAvro(jsonPositionV1.getBytes(),  PositionTestV1.SCHEMA$);

        BinaryDecoder binaryDecoder = DecoderFactory.get().binaryDecoder(bytesAvro, null);
        SpecificDatumReader<PositionTestV2> reader = new SpecificDatumReader( PositionTestV1.SCHEMA$, PositionTestV2.SCHEMA$);
        PositionTestV2 position = reader.read(null, binaryDecoder);
        assertEquals(-1,position.getUserId());
        assertEquals(-1,position.getTest());
    }
    @Test
    void serializeDeserialize(){
        Position pos = new Position();
        pos.setAltitude(10);
        pos.setAircraftId(20);
        byte[] bytes = new PositionSerializer().serialize(null, pos);
        Position deserialized = new PositionDeserializer().deserialize(null, bytes);
        assertEquals(10,deserialized.getAltitude());
        assertEquals(20,deserialized.getAircraftId());

    }

}
