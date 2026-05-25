package io.github.hanhy06.embellishchat.config.adapter;

import com.google.gson.JsonSyntaxException;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import net.minecraft.resources.ResourceLocation;

import java.io.IOException;

public class ResourceLocationTypeAdapter extends TypeAdapter<ResourceLocation> {
    @Override
    public void write(JsonWriter jsonWriter, ResourceLocation identifier) throws IOException {
        if (identifier == null){
            jsonWriter.nullValue();
            return;
        }
        jsonWriter.value(identifier.toString());
    }

    @Override
    public ResourceLocation read(JsonReader jsonReader) throws IOException {
        JsonToken token = jsonReader.peek();

        if (token == JsonToken.NULL) {
            jsonReader.nextNull();
            throw new JsonSyntaxException("Cannot parse JSON null as a ResourceLocation.");
        }

        if (token != JsonToken.STRING) {
            throw new JsonSyntaxException("Expected a string for a ResourceLocation, but found " + token + ".");
        }

        String identifier = jsonReader.nextString();
        return ResourceLocation.parse(identifier);
    }
}
