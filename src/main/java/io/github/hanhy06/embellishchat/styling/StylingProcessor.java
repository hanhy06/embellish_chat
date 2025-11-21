package io.github.hanhy06.embellishchat.styling;

import io.github.hanhy06.embellishchat.config.Config;
import io.github.hanhy06.embellishchat.config.ConfigListener;
import io.github.hanhy06.embellishchat.mention.data.MentionSegment;
import io.github.hanhy06.embellishchat.styling.rule.*;
import io.github.hanhy06.embellishchat.styling.util.Runs;
import io.github.hanhy06.embellishchat.util.OptionUtil;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;

import java.util.*;
import java.util.function.Function;
import java.util.regex.Matcher;

import static io.github.hanhy06.embellishchat.styling.util.TextSliceUtil.flatten;
import static io.github.hanhy06.embellishchat.styling.util.TextSliceUtil.slice;

public class StylingProcessor implements ConfigListener {
    public static StylingProcessor INSTANCE;

    private Config config;
    private Map<String, List<StylingRule>> stylingRules;
    private StyleRegistry registry;

    public StylingProcessor() {
        INSTANCE = this;
    }

    @Override
    public void onConfigReload(Config newConfig) {
        this.config = newConfig;
        this.stylingRules = newConfig.stylingRules();
        this.registry = new StyleRegistry(newConfig);
    }

    public MutableText handleStyling(MutableText text, ServerPlayerEntity player, List<String> keys) {
        List<StylingRule> rules = new ArrayList<>();
        keys.forEach(key -> rules.addAll(stylingRules.getOrDefault(key, List.of())));
        if (rules.isEmpty()) return text;

        String content = text.getString();
        List<StyleNode> rawNodes = new ArrayList<>();

        // 1. 정규식 매칭을 통해 모든 Raw Node 수집
        for (int level = 0; level < rules.size(); level++) {
            rawNodes.addAll(parseStyles(content, rules.get(level), player, level));
        }
        if (rawNodes.isEmpty()) return text;

        List<Integer> points = new ArrayList<>();
        points.add(0);
        points.add(content.length());
        for (StyleNode node : rawNodes) {
            points.add(node.begin());
            points.add(node.end());
        }
        points = points.stream()
                .distinct()
                .sorted()
                .toList();

        // 3. 각 원자적 구간(Atomic Interval)별로 스타일 병합
        List<StyleNode> nodes = new ArrayList<>();
        for (int i = 0; i < points.size() - 1; i++) {
            int start = points.get(i);
            int end = points.get(i + 1);

            // 현재 구간(start ~ end)을 완전히 포함하는 모든 Raw Node 찾기
            List<StyleNode> activeNodes = new ArrayList<>();
            for (StyleNode raw : rawNodes) {
                if (raw.begin() <= start && raw.end() >= end) {
                    activeNodes.add(raw);
                }
            }

            if (activeNodes.isEmpty()) continue;

            // Config 순서(level)대로 정렬하여 스타일 적용 순서 보장
            activeNodes.sort(Comparator.comparingInt(StyleNode::level));

            // 해당 구간에 적용될 옵션과 함수 병합
            List<String> mergedOptions = new ArrayList<>();
            List<Function<StyleParameter, MutableText>> mergedFunctions = new ArrayList<>();

            for (StyleNode active : activeNodes) {
                mergedOptions.addAll(active.options());
                mergedFunctions.addAll(active.functions());
            }

            nodes.add(new StyleNode(
                    start, end, // matchStart/End는 applyStyle에서 사용되지 않으므로 범위와 동일하게 설정
                    start, end,
                    mergedOptions,
                    mergedFunctions,
                    0
            ));
        }

        return applyStyle(text, nodes, player);
    }

    private List<StyleNode> parseStyles(String text, StylingRule rule, ServerPlayerEntity player, int level) {
        Matcher matcher = rule.pattern().matcher(text);
        List<StyleNode> result = new ArrayList<>();

        while (matcher.find()) {
            int begin = matcher.start(1);
            int end = matcher.end(1);

            List<String> presets = new ArrayList<>();
            List<Function<StyleParameter, MutableText>> functions = new ArrayList<>();
            rule.styles().forEach(action -> {
                presets.add(action.preset());
                functions.add(registry.get(action.styleType()));
            });

            List<String> options = OptionUtil.split(matcher.group(2), config.delimiter());
            options = OptionUtil.parseOption(options, presets, player);

            StyleNode node = new StyleNode(matcher.start(), matcher.end(), begin, end, options, functions, level);
            result.add(node);
        }

        return result;
    }

    // parseNodes 메서드는 삭제됨 (handleStyling 내의 Sweep Line 로직으로 대체)

    private MutableText applyStyle(MutableText text, List<StyleNode> nodes, ServerPlayerEntity player) {
        if (nodes.isEmpty()) return text;

        Runs runs = flatten(text);
        MutableText result = Text.empty();

        int lastEnd = 0;
        for (StyleNode node : nodes) {
            List<String> options = node.options();
            List<Function<StyleParameter, MutableText>> functions = node.functions();

            // 스타일 적용 전 일반 텍스트 추가
            result.append(slice(runs, lastEnd, node.begin()));

            // 스타일 적용 구간 처리
            MutableText segment = slice(runs, node.begin(), node.end());
            for (int i = 0; i < functions.size(); i++) {
                segment = functions.get(i).apply(StyleParameter.of(
                        segment, options.get(i), player
                ));
            }
            result.append(segment);
            lastEnd = node.end();
        }
        // 남은 뒷부분 텍스트 추가
        result.append(slice(runs, lastEnd, runs.full().length()));

        return result;
    }

    public MutableText applyMention(MutableText text, List<MentionSegment> mentionSegments, ServerPlayerEntity player) {
        if (mentionSegments.isEmpty()) return text;

        Runs runs = flatten(text);
        MutableText result = Text.empty();

        int lastEnd = 0;
        for (MentionSegment mentionSegment : mentionSegments) {
            result.append(slice(runs, lastEnd, mentionSegment.begin()));

            MutableText segment = slice(runs, mentionSegment.begin(), mentionSegment.end());
            segment.fillStyle(mentionSegment.style());
            for (StyleAction action : mentionSegment.styles()) {
                segment = registry
                        .get(action.styleType())
                        .apply(StyleParameter.of(segment, action.preset(), player));
            }

            result.append(segment);
            lastEnd = mentionSegment.end();
        }
        result.append(slice(runs, lastEnd, runs.full().length()));

        return result;
    }
}