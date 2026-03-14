package io.github.hanhy06.embellishchat.styling.util;

import net.minecraft.ChatFormatting;
import net.minecraft.world.scores.PlayerTeam;
import net.minecraft.world.scores.Scoreboard;

import java.awt.*;

public class ColorUtil {
    public static Integer getTeamColor(Scoreboard scoreboard, String name){
        PlayerTeam team = scoreboard.getPlayersTeam(name);

        if (team != null) {
            ChatFormatting formatting = team.getColor();
            if (formatting != null && formatting.isColor()) {
                return formatting.getColor();
            }
        }

        return null;
    }

    public static Color lerpColor(Color start, Color end, double rate) {
        rate = Math.max(0, Math.min(1, rate));

        int r = (int) (start.getRed()   + (end.getRed()   - start.getRed())   * rate);
        int g = (int) (start.getGreen() + (end.getGreen() - start.getGreen()) * rate);
        int b = (int) (start.getBlue()  + (end.getBlue()  - start.getBlue())  * rate);

        return new Color(r, g, b);
    }
}
