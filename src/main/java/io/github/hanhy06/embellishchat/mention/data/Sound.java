package io.github.hanhy06.embellishchat.mention.data;

import com.google.gson.annotations.SerializedName;
import net.minecraft.network.packet.s2c.play.PlaySoundS2CPacket;
import net.minecraft.registry.Registries;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;

public record Sound(
        @SerializedName("id")
        SoundEvent event,
        SoundCategory category,
        float volume,
        float pitch
) {
    public static Sound of(String id, SoundCategory category, float volume, float pitch){
        return new Sound(SoundEvent.of(Identifier.of(id)),category,volume,pitch);
    }

    public static void playSoundToPlayer(ServerPlayerEntity player,Sound sound) {
        player.networkHandler.sendPacket(new PlaySoundS2CPacket(
                Registries.SOUND_EVENT.getEntry(sound.event),
                sound.category,
                player.getX(),
                player.getY(),
                player.getZ(),
                sound.volume, sound.pitch,
                player.getRandom().nextLong()
        ));
    }
}
