package com.hanhy06.embellish_chat.util;

import com.google.gson.JsonSyntaxException;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import com.hanhy06.embellish_chat.EmbellishChat;

import java.io.IOException;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

public class PatternTypeAdapter extends TypeAdapter<Pattern> {
    @Override
    public void write(JsonWriter out, Pattern value) throws IOException {
        if (value == null) {
            out.nullValue();
            return;
        }
        out.value(value.pattern());
    }

    @Override
    public Pattern read(JsonReader in) throws IOException {
        JsonToken token = in.peek();

        if (token == JsonToken.NULL) {
            in.nextNull();
            throw new JsonSyntaxException("Cannot parse JSON null as a regex pattern.");
        }

        if (token != JsonToken.STRING) {
            throw new JsonSyntaxException("Expected a string for a regex pattern, but found " + token + ".");
        }

        String regex = in.nextString();
        try {
            return Pattern.compile(regex);
        } catch (PatternSyntaxException e) {
            EmbellishChat.LOGGER.error("Failed to compile regex pattern: \"{}\" ({})", regex, e.getMessage());
            throw new JsonSyntaxException("Invalid regex pattern: \"" + regex + "\"", e);
        }
    }
}
