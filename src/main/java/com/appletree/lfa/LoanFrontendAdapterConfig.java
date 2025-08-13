package com.appletree.lfa;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;

import java.io.IOException;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;

import static java.time.format.DateTimeFormatter.ofPattern;

@Configuration
public class LoanFrontendAdapterConfig {

    @Bean
    public ObjectMapper objectMapper(Jackson2ObjectMapperBuilder builder) {
        ObjectMapper objectMapper = getObjectMapper(builder);
        objectMapper.registerModule(new SimpleModule().addSerializer(OffsetDateTime.class, new MillisOffsetDateTimeSerializer()));
        objectMapper.registerModule(new SimpleModule().addDeserializer(LocalDate.class, new LocalDateDeserializer(ofPattern("dd.MM.yyyy"))));
        return objectMapper;
    }

    private ObjectMapper getObjectMapper(Jackson2ObjectMapperBuilder builder) {
        return builder
                .featuresToEnable(MapperFeature.ACCEPT_CASE_INSENSITIVE_ENUMS)
                .build();
    }

    static class MillisOffsetDateTimeSerializer extends JsonSerializer<OffsetDateTime> {
        private static final DateTimeFormatter FORMATTER = ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSX");

        @Override
        public void serialize(OffsetDateTime value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
            gen.writeString(FORMATTER.format(value));
        }
    }

}
