package io.github.hanhy06.embellishchat.styling.util;

import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.scoreboard.Team;
import net.minecraft.util.Formatting;

import java.awt.*;

public class ColorUtil {
    public static Integer getTeamColor(Scoreboard scoreboard, String name){
        Team team = scoreboard.getScoreHolderTeam(name);

        if (team != null) {
            Formatting formatting = team.getColor();
            if (formatting != null && formatting.isColor()) {
                return formatting.getColorValue();
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
