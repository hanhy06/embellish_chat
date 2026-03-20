package io.github.hanhy06.embellishchat.config.adapter;

import com.google.gson.*;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import com.mojang.serialization.JsonOps;
import io.github.hanhy06.embellishchat.util.PlaceHolderUtil;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.network.chat.MutableComponent;

import java.io.IOException;

public class MutableTextAdapter extends TypeAdapter<MutableComponent> {
    @Override
    public void write(JsonWriter jsonWriter, MutableComponent mutableText) throws IOException {
        if (mutableText == null) {
            jsonWriter.nullValue();
            return;
        }

         String value = ComponentSerialization.CODEC
                .encodeStart(JsonOps.INSTANCE, mutableText)
                .getOrThrow()
                .toString();

        jsonWriter.value(value);
    }

    @Override
    public MutableComponent read(JsonReader jsonReader) throws IOException {
        JsonToken token = jsonReader.peek();

        if (token == JsonToken.NULL){
            jsonReader.nextNull();
            return null;
        }

        if (token != JsonToken.STRING){
            throw new JsonSyntaxException("MutableText value is not a string. Please provide it as a string.");
        }

        String value = jsonReader.nextString();
        try {
            JsonElement element = JsonParser.parseString(value);
            return ComponentSerialization.CODEC.parse(JsonOps.INSTANCE,element).getOrThrow().copy();
        }catch (JsonParseException e){
            return PlaceHolderUtil.parseTag(jsonReader.nextString()).copy();
        }
    }
}