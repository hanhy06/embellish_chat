package io.github.hanhy06.embellishchat.mention.data;

import com.google.gson.annotations.SerializedName;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.protocol.game.ClientboundSoundPacket;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;

public record Sound(
        @SerializedName("id")
        SoundEvent event,
        SoundSource category,
        float volume,
        float pitch
) {
    public static Sound of(String id, SoundSource category, float volume, float pitch){
        return new Sound(SoundEvent.createVariableRangeEvent(Identifier.parse(id)),category,volume,pitch);
    }

    public void playSoundToPlayer(ServerPlayer player) {
        player.connection.send(new ClientboundSoundPacket(
                BuiltInRegistries.SOUND_EVENT.wrapAsHolder(this.event),
                this.category,
                player.getX(),
                player.getY(),
                player.getZ(),
                this.volume, this.pitch,
                player.getRandom().nextLong()
        ));
    }
}
