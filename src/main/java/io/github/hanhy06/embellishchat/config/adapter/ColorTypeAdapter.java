package io.github.hanhy06.embellishchat.config.adapter;

import com.google.gson.JsonSyntaxException;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;

import java.awt.*;
import java.io.IOException;

public class ColorTypeAdapter extends TypeAdapter<Color> {
    @Override
    public void write(JsonWriter jsonWriter, Color color) throws IOException {
        if (color == null){
            jsonWriter.nullValue();
            return;
        }

        String hexColor = String.format("#%02X%02X%02X", color.getRed(),color.getGreen(),color.getBlue());
        jsonWriter.value(hexColor);
    }

    @Override
    public Color read(JsonReader jsonReader) throws IOException {
        JsonToken token = jsonReader.peek();

        if (token == JsonToken.NULL){
            jsonReader.nextNull();
            return null;
        }

        if (token != JsonToken.STRING){
            throw new JsonSyntaxException("Color value is not a string. Please provide it as a string.");
        }

        return Color.decode(jsonReader.nextString());
    }
}
