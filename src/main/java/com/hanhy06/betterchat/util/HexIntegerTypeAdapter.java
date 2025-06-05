package com.hanhy06.betterchat.util;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import com.google.gson.JsonSyntaxException;

import java.io.IOException;

public class HexIntegerTypeAdapter extends TypeAdapter<Integer> {
    @Override
    public void write(JsonWriter out, Integer value) throws IOException {
        if (value == null) {
            out.nullValue();
        } else {
            out.value("0x" + Integer.toHexString(value).toUpperCase());
        }
    }

    @Override
    public Integer read(JsonReader in) throws IOException {
        JsonToken token = in.peek();

        if (token == JsonToken.NULL) {
            in.nextNull();
            throw new JsonSyntaxException("Cannot parse JSON null as a primitive int for hex color.");
        }

        if (token != JsonToken.STRING) {
            throw new JsonSyntaxException("Expected a string for hex color, but found " + token);
        }

        String hexString = in.nextString();
        try {
            return Integer.decode(hexString);
        } catch (NumberFormatException e) {
            throw new JsonSyntaxException("Invalid hex string for integer: '" + hexString + "'", e);
        }
    }
}