package com.hanhy06.embellish_chat.util;

import com.hanhy06.embellish_chat.EmbellishChat;
import com.hanhy06.embellish_chat.config.ConfigManager;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.scoreboard.Team;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Formatting;

public class Teamcolor {
    public static int getPlayerColor(ServerPlayerEntity player){
        if (player != null) {
            Team team = player.getScoreboardTeam();
            if (team != null) {
                Formatting formatting = team.getColor();
                if (formatting != null && formatting.isColor() && formatting != Formatting.RESET) {
                    return formatting.getColorValue().intValue();
                }
            }
        }

        return ConfigManager.getConfig().defaultMentionColor();
    }

    public static int getPlayerColor(String name){
        MinecraftServer server = EmbellishChat.getServer();
        Scoreboard scoreboard = server.getScoreboard();

        for (String teamName : scoreboard.getTeamNames()){
            Team team = scoreboard.getTeam(teamName);
            if (team != null) {
                Formatting formatting = team.getColor();
                if (formatting != null && formatting.isColor() && formatting != Formatting.RESET) {
                    return formatting.getColorValue().intValue();
                }
            }
        }

        return ConfigManager.getConfig().defaultMentionColor();
    }
}
