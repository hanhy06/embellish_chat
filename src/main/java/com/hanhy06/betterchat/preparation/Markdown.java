package com.hanhy06.betterchat.preparation;

import net.minecraft.text.MutableText;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.List;

public class Markdown {

    public static MutableText markdown(MutableText context){
        MutableText result = Text.empty();



        return result;
    }

    private static MutableText substring(MutableText context, int beginIndex, int endIndex) {
        if (beginIndex < 0 || beginIndex >= endIndex) {
            return Text.empty();
        }

        MutableText result = Text.empty();

        List<Text> parts = new ArrayList<>();
        parts.add(context);
        parts.addAll(context.getSiblings());

        int currentPos = 0;

        for (Text part : parts) {
            String partString = part.getString();
            int partLen = partString.length();
            int partStart = currentPos;
            int partEnd = currentPos + partLen;

            int overlapStart = Math.max(partStart, beginIndex);
            int overlapEnd = Math.min(partEnd, endIndex);

            if (overlapStart < overlapEnd) {
                int subBeginInPart = overlapStart - partStart;
                int subEndInPart = overlapEnd - partStart;

                String subString = partString.substring(subBeginInPart, subEndInPart);

                MutableText styledSubstring = Text.literal(subString).setStyle(part.getStyle());

                result.append(styledSubstring);
            }

            currentPos = partEnd;

            if (currentPos >= endIndex) {
                break;
            }
        }

        return result;
    }
}
