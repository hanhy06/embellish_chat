package io.github.hanhy06.embellish_chat.styling.util;

import net.minecraft.text.MutableText;
import net.minecraft.text.Style;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class TextSliceUtil {
    public static Runs flatten(Text text) {
        List<Run> list = new ArrayList<>();
        StringBuilder all = new StringBuilder();

        text.visit(new Text.StyledVisitor<Void>() {
            @Override
            public Optional<Void> accept(Style style, String content) {
                int start = all.length();
                all.append(content);
                int end = all.length();
                list.add(new Run(start, end, style, content));
                return Optional.empty();
            }
        }, Style.EMPTY);

        return new Runs(all.toString(), list);
    }

    public static MutableText slice(Runs runs, int begin, int end) {
        MutableText out = Text.empty();
        for (Run run : runs.runs()) {
            if (run.end() <= begin) continue;
            if (run.start() >= end) break;

            int startIndex = Math.max(begin, run.start()) - run.start();
            int endIndex = Math.min(end, run.end()) - run.start();
            out.append(Text.literal(run.content().substring(startIndex, endIndex)).setStyle(run.style()));
        }
        return out;
    }
}
