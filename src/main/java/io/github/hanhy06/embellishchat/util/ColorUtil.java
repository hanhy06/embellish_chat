package io.github.hanhy06.embellishchat.util;

import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.scoreboard.Team;
import net.minecraft.util.Formatting;

import java.awt.*;

public class ColorUtil {
    public static Integer getTeamColor(Scoreboard scoreboard, String name, Integer preset){
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

    public static Color lerpColor(Color start, Color end, double rate) {
        rate = Math.max(0, Math.min(1, rate));

        int r = (int) (start.getRed()   + (end.getRed()   - start.getRed())   * rate);
        int g = (int) (start.getGreen() + (end.getGreen() - start.getGreen()) * rate);
        int b = (int) (start.getBlue()  + (end.getBlue()  - start.getBlue())  * rate);

        return new Color(r, g, b);
    }
}
