package com.hanhy06.embellish_chat.util;

import com.hanhy06.embellish_chat.config.ConfigManager;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.scoreboard.Team;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Formatting;

import java.util.UUID;

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

        return -1;
    }

    public static int getPlayerColor(Scoreboard scoreboard,String name){
        for (String teamName : scoreboard.getTeamNames()){
            Team team = scoreboard.getTeam(teamName);
            if (team != null) {
                Formatting formatting = team.getColor();
                boolean belongTeam = team.getPlayerList().contains(name);
                if (formatting != null && formatting.isColor() && formatting != Formatting.RESET && belongTeam) {
                    return formatting.getColorValue().intValue();
                } else if (belongTeam) {
                    return ConfigManager.getConfig().defaultMentionColor();
                }
            }
        }

        return ConfigManager.getConfig().defaultMentionColor();
    }

    public static int decideTeamColor(
            PlayerManager manager,
            MinecraftServer server,
            UUID playerId,
            String playerName
    ) {
        int color = Teamcolor.getPlayerColor(manager.getPlayer(playerId));
        if (color != -1) return color;
        return Teamcolor.getPlayerColor(server.getScoreboard(), playerName);
    }
}
