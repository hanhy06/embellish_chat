package io.github.hanhy06.embellishchat.styling.util;

import io.github.hanhy06.embellishchat.styling.data.Run;
import io.github.hanhy06.embellishchat.styling.data.Runs;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class TextSliceUtil {
    public static Runs flatten(Component text) {
        List<Run> list = new ArrayList<>();
        StringBuilder all = new StringBuilder();

        text.visit((FormattedText.StyledContentConsumer<Void>) (style, content) -> {
            int start = all.length();
            all.append(content);
            int end = all.length();
            list.add(new Run(start, end, style, content));
            return Optional.empty();
        }, Style.EMPTY);

        return new Runs(all.toString(), list);
    }

    public static MutableComponent slice(Runs runs, int begin, int end) {
        MutableComponent out = Component.empty();
        for (Run run : runs.runs()) {
            if (run.end() <= begin) continue;
            if (run.start() >= end) break;

            int startIndex = Math.max(begin, run.start()) - run.start();
            int endIndex = Math.min(end, run.end()) - run.start();
            out.append(Component.literal(run.content().substring(startIndex, endIndex)).setStyle(run.style()));
        }
        return out;
    }
}
