package io.github.hanhy06.embellishchat.styling.util;

import java.awt.*;

public class ColorUtil {
    public static Color lerpColor(Color start, Color end, double rate) {
        rate = Math.clamp(rate, 0, 1);

        int r = (int) (start.getRed()   + (end.getRed()   - start.getRed())   * rate);
        int g = (int) (start.getGreen() + (end.getGreen() - start.getGreen()) * rate);
        int b = (int) (start.getBlue()  + (end.getBlue()  - start.getBlue())  * rate);

        return new Color(r, g, b);
    }
}
