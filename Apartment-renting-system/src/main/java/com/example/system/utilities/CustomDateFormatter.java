package com.example.system.utilities;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;
import java.sql.Date;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

public class CustomDateFormatter extends JsonSerializer<Date> {

    private final DateTimeFormatter formatter;

    public CustomDateFormatter() {
        formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd").withZone(ZoneOffset.UTC);
    }

    @Override
    public void serialize(Date value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        String str = formatter.format(value.toLocalDate());
        gen.writeString(str);
    }
}
