package com.supersidor.flightmap.kafka;

import com.supersidor.flightmap.avro.Schemas;
import com.supersidor.flightmap.avro.schemas.Position;
import org.apache.avro.Schema;
import org.apache.avro.io.BinaryDecoder;
import org.apache.avro.io.DecoderFactory;
import org.apache.avro.specific.SpecificDatumReader;
import org.apache.kafka.common.serialization.Deserializer;

import java.io.IOException;
import java.nio.ByteBuffer;

public class PositionDeserializer implements Deserializer<Position>{

    public static final int INT_SIZE = 4;

    @Override
    public Position deserialize(String topic, byte[] data) {
        ByteBuffer buffer = ByteBuffer.wrap(data);
        int version = buffer.getInt();
        Schema writerSchema = Schemas.getSchema(version);

        BinaryDecoder binaryDecoder = DecoderFactory.get().binaryDecoder(buffer.array(), INT_SIZE,data.length-INT_SIZE,(BinaryDecoder)null);
        SpecificDatumReader<Position> reader = new SpecificDatumReader(writerSchema,Position.SCHEMA$);
        try {
            return reader.read(null, binaryDecoder);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }
}
