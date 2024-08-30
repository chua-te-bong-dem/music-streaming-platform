package com.example.demo.constant;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import java.io.IOException;

@JsonDeserialize(using = GenreEnum.GenreDeserializer.class)
public enum GenreEnum {
    POP, ROCK;

    @JsonDeserialize(using = GenreDeserializer.class)
    public static class GenreDeserializer extends JsonDeserializer<GenreEnum> {
        @Override
        public GenreEnum deserialize(JsonParser jsonParser,
                                      DeserializationContext deserializationContext)
                throws IOException, JacksonException {
            String value = jsonParser.getText().toUpperCase();
            return GenreEnum.valueOf(value);
        }
    }
}
