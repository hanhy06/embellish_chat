package io.github.hanhy06.embellishchat.config.adapter;

import com.google.gson.JsonSyntaxException;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import io.github.hanhy06.embellishchat.util.PlaceHolderUtil;
import net.minecraft.text.MutableText;

import java.io.IOException;

public class MutableTextAdapter extends TypeAdapter<MutableText> {
    @Override
    public void write(JsonWriter jsonWriter, MutableText mutableText) throws IOException {
        if (mutableText == null) {
            jsonWriter.nullValue();
            return;
        }

        jsonWriter.value(mutableText.getString());
    }

    @Override
    public MutableText read(JsonReader jsonReader) throws IOException {
        JsonToken token = jsonReader.peek();

        if (token == JsonToken.NULL){
            jsonReader.nextNull();
            return null;
        }

        if (token != JsonToken.STRING){
            throw new JsonSyntaxException("MutableText value is not a string. Please provide it as a string.");
        }

        return PlaceHolderUtil.parseTag(jsonReader.nextString()).copy();
    }
}
