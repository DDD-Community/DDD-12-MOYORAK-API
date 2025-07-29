package com.moyorak.config.web;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.module.SimpleModule;
import java.io.IOException;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;

@Configuration
class MapperConfig {

    @Bean
    public ObjectMapper objectMapper(Jackson2ObjectMapperBuilder builder) {
        ObjectMapper objectMapper = builder.build();

        SimpleModule module = new SimpleModule();
        module.addDeserializer(
                String.class,
                new StdDeserializer<>(String.class) {
                    @Override
                    public String deserialize(JsonParser p, DeserializationContext ctxt)
                            throws IOException, JacksonException {
                        final String value = p.getValueAsString();
                        return value != null ? value.trim() : null;
                    }
                });

        objectMapper.registerModule(module);

        return objectMapper;
    }
}
