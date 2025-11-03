package io.github.hanhy06.embellish_chat.util;

import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.scoreboard.Team;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Formatting;

public class TeamColor {
    public static Integer getPlayerColor(ServerPlayerEntity player,Integer preset){
        if (player != null) {
            Team team = player.getScoreboardTeam();
            if (team != null) {
                Formatting formatting = team.getColor();
                if (formatting != null && formatting.isColor() && formatting != Formatting.RESET) {
                    return formatting.getColorValue();
                }
            }
        }

        return preset;
    }

    public static Integer getPlayerColor(Scoreboard scoreboard,String name, Integer preset){
        for (String teamName : scoreboard.getTeamNames()){
            Team team = scoreboard.getTeam(teamName);
            if (team != null) {
                Formatting formatting = team.getColor();
                boolean belongTeam = team.getPlayerList().contains(name);
                if (formatting != null && formatting.isColor() && formatting != Formatting.RESET && belongTeam) {
                    return formatting.getColorValue();
                } else if (belongTeam) {
                    break;
                }
            }
        }

        return preset;
    }
}
