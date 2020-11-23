package com.supersidor.flightmap.kafka;

import com.supersidor.flightmap.avro.schemas.Position;
import org.apache.avro.io.BinaryEncoder;
import org.apache.avro.io.EncoderFactory;
import org.apache.avro.specific.SpecificDatumWriter;
import org.apache.kafka.common.serialization.Serializer;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

import static com.supersidor.flightmap.avro.Schemas.LAST_SCHEMA_ID;

public class PositionSerializer implements Serializer<Position>{

    @Override
    public byte[] serialize(String topic, Position position) {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        try {
            out.write(ByteBuffer.allocate(4).putInt(LAST_SCHEMA_ID).array());
            BinaryEncoder encoder = EncoderFactory.get().directBinaryEncoder(out, (BinaryEncoder)null);
            (new SpecificDatumWriter(Position.SCHEMA$)).write(position,encoder);
            encoder.flush();
            return out.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}

