package io.github.hanhy06.embellishchat.config.adapter;

import com.google.gson.JsonSyntaxException;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import net.minecraft.util.Identifier;

import java.io.IOException;

public class IdentifierTypeAdapter extends TypeAdapter<Identifier> {
    @Override
    public void write(JsonWriter jsonWriter, Identifier identifier) throws IOException {
        if (identifier == null){
            jsonWriter.nullValue();
            return;
        }
        jsonWriter.value(identifier.toString());
    }

    @Override
    public Identifier read(JsonReader jsonReader) throws IOException {
        JsonToken token = jsonReader.peek();

        if (token == JsonToken.NULL){
            jsonReader.nextNull();
            throw new JsonSyntaxException("Mention sound ID is null. Please write it in the format namespace:id.");
        }

        if (token != JsonToken.STRING){
            throw new JsonSyntaxException("Mention sound ID is not a string. Please write it in the format namespace:id.");
        }

        return Identifier.tryParse(jsonReader.nextString());
    }
}
