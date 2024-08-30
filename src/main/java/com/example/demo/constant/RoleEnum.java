package com.example.demo.constant;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import java.io.IOException;

@JsonDeserialize(using = RoleEnum.RoleDeserializer.class)
public enum RoleEnum {
    ADMIN, USER, ARTIST, MOD;

    public static class RoleDeserializer extends JsonDeserializer<RoleEnum> {
        @Override
        public RoleEnum deserialize(JsonParser jsonParser,
                                    DeserializationContext deserializationContext)
                throws IOException, JacksonException {
            String value = jsonParser.getText().toUpperCase();
            return RoleEnum.valueOf(value);
        }
    }
}
