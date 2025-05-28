package com.hanhy06.betterchat.util;

import net.minecraft.scoreboard.Team;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.TextColor;
import net.minecraft.util.Formatting;

public class Teamcolor {
    public static int getPlayerColor(ServerPlayerEntity player){
        if (player != null) {
            Team team = player.getScoreboardTeam();
            if (team != null) {
                Formatting formatting = team.getColor();
                if (formatting != null && formatting.isColor() && formatting != Formatting.RESET) {
                    return formatting.getColorIndex();
                }
            }
        }

        return 16777045;
    }
}
