package com.hanhy06.embellish_chat.chat.processor;

import com.hanhy06.embellish_chat.data.Config;
import com.hanhy06.embellish_chat.data.Receiver;
import com.hanhy06.embellish_chat.util.Teamcolor;
import com.mojang.authlib.GameProfile;
import net.minecraft.network.packet.s2c.play.PlaySoundS2CPacket;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.text.MutableText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.UserCache;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Mention {
    private static final Pattern MENTION_PATTERN = Pattern.compile("@([A-Za-z0-9_]{1,16})(?=\\b|$)");

    public static void broadcastMention(Config config, RegistryEntry<SoundEvent> mentionSound, ServerPlayerEntity sender, List<Receiver> receivers){
        PlayerManager manager = sender.getServer().getPlayerManager();

        for (Receiver receiver : new HashSet<>(receivers)){
            UUID uuid = receiver.profile().getId();
            ServerPlayerEntity player = manager.getPlayer(uuid);

            int teamColor = Teamcolor.getPlayerColor(sender);

            MutableText titleText = sender.getName().copy()
                    .styled(style -> style.withColor((teamColor == -1) ? config.defaultMentionColor() : teamColor).withBold(true))
                    .append(Text.literal(" mentioned you").fillStyle(Style.EMPTY.withBold(false).withColor(Formatting.WHITE)));

            if(player != null){
                player.networkHandler.sendPacket(
                        new PlaySoundS2CPacket(
                                mentionSound,
                                SoundCategory.MASTER
                                ,player.getX(),player.getY(),player.getZ()
                                ,1f,1.75f,1
                        )
                );
                player.sendMessage(titleText ,true);
            }
        }
    }

    public static List<Receiver> mentionParser(MinecraftServer server, String originalMessage){
        List<Receiver> receivers = new ArrayList<>();
        if(originalMessage == null || !originalMessage.contains("@")) return receivers;

        UserCache userCache = server.getUserCache();
        PlayerManager playerManager = server.getPlayerManager();

        for (Unit unit : nameParser(originalMessage)){
            Optional<GameProfile> profile = userCache.findByName(unit.name);

            if(profile.isEmpty()) continue;

            int teamColor = Teamcolor.decideTeamColor(playerManager, server, profile.get().getId(), profile.get().getName());
            receivers.add(new Receiver(profile.get(), unit.begin, unit.end, teamColor));
        }

        return receivers;
    }

    private static List<Unit> nameParser(String raw){
        List<Unit> unit = new ArrayList<>();

        Matcher matcher = MENTION_PATTERN.matcher(raw);

        while (matcher.find()){
            unit.add(new Unit(
                    matcher.group(1),
                    matcher.start(),
                    matcher.end(1)
            ));
        }

        return  unit;
    }

    private record Unit(
            String name,int begin,int end
    ){}
}