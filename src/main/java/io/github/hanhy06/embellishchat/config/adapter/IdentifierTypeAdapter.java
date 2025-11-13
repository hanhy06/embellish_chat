package io.github.hanhy06.embellishchat.config.adapter;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;
import net.minecraft.util.Identifier;

import java.io.IOException;

public class IdentifierTypeAdapter extends TypeAdapter<Identifier> {
    @Override
    public void write(JsonWriter jsonWriter, Identifier identifier) throws IOException {
        jsonWriter.value(identifier.getPath());
    }

    @Override
    public Identifier read(JsonReader jsonReader) throws IOException {
        return Identifier.tryParse(jsonReader.nextString());
    }
}
