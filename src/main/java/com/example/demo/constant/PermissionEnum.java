package com.example.demo.constant;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import java.io.IOException;

@JsonDeserialize(using = PermissionEnum.PermissionDeserializer.class)
public enum PermissionEnum {
    CREATE_USER, GET_USER, UPDATE_USER, DELETE_USER;

    public static class PermissionDeserializer extends JsonDeserializer<PermissionEnum> {
        @Override
        public PermissionEnum deserialize(JsonParser jsonParser,
                                          DeserializationContext deserializationContext)
                throws IOException, JacksonException {
            String value = jsonParser.getText().toUpperCase();
            return PermissionEnum.valueOf(value);
        }
    }
}
