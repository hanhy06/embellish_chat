package io.github.hanhy06.embellishchat.config.adapter;

import com.google.gson.JsonSyntaxException;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;

import java.io.IOException;

public class SoundEventTypeAdapter extends TypeAdapter<SoundEvent> {
    @Override
    public void write(JsonWriter jsonWriter, SoundEvent event) throws IOException {
        if (event == null) {
            jsonWriter.nullValue();
            return;
        }
        
        jsonWriter.value(event.location().toString());
    }

    @Override
    public SoundEvent read(JsonReader jsonReader) throws IOException {
        JsonToken token = jsonReader.peek();

        if (token == JsonToken.NULL){
            jsonReader.nextNull();
            throw new JsonSyntaxException("Mention sound ID is null. Please write it in the format namespace:id.");
        }

        if (token != JsonToken.STRING){
            throw new JsonSyntaxException("Mention sound ID is not a string. Please write it in the format namespace:id.");
        }

        return SoundEvent.createVariableRangeEvent(ResourceLocation.tryParse(jsonReader.nextString()));
    }
}
