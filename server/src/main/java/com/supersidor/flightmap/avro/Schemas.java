package com.supersidor.flightmap.avro;

import com.supersidor.flightmap.avro.schemas.Position;
import org.apache.avro.Schema;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;

public class Schemas {
    public static final int LAST_SCHEMA_ID = 1;
    private static final Map<Integer, Schema> schemas = new HashMap<>();

    public static Schema getSchema(int version) {
        Schema schema = schemas.get(version);
        if (schema == null)
            throw new IllegalArgumentException("Unknown schema version " + version);
        return schema;
    }


    static {
        schemas.put(LAST_SCHEMA_ID, Position.SCHEMA$);
    }

    static private Schema load(String resourceName) throws IOException {
        String schemaStr = Files.readString(new ClassPathResource(resourceName).getFile().toPath());
        Schema.Parser p = new Schema.Parser();
        return p.parse(schemaStr);
    }


}
