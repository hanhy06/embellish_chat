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
    private Map<String,List<StylingRule>> stylingRules;
    private StyleRegistry registry;

    public StylingProcessor(){
        INSTANCE = this;
    }

    @Override
    public void onConfigReload(Config newConfig) {
        this.config = newConfig;

        this.stylingRules = newConfig.stylingRules();
        this.registry = new StyleRegistry(newConfig);
    }

    public MutableText handleStyling(MutableText text,ServerPlayerEntity player,List<String> keys){
        List<StylingRule> rules = new ArrayList<>();
        keys.forEach(key -> rules.addAll(stylingRules.getOrDefault(key,List.of())));
        if (rules.isEmpty()) return text;

        String content = text.getString();
        List<StyleNode> nodes = new ArrayList<>();
        for (int i=0;i<rules.size();i++){
            nodes.addAll(parseStyles(content,rules.get(i),player,i));
        }
        nodes = parseNodes(nodes.stream().toList(),content);

        return applyStyle(text,nodes,player);
    }

    private List<StyleNode> parseStyles(String text,StylingRule rule,ServerPlayerEntity player,int level){
        Matcher matcher = rule.pattern().matcher(text);
        List<StyleNode> result = new ArrayList<>();
        if (!matcher.find()) return result;

        List<String> presets = new ArrayList<>();
        List<Function<StyleParameter, MutableText>> functions = new ArrayList<>();
        rule.styles().forEach(action ->{
            presets.add(action.preset());
            functions.add(registry.get(action.styleType()));
        });

        do {
            int begin = matcher.start(1);
            int end = matcher.end(1);
            List<String> options = OptionUtil.split(matcher.group(2),config.delimiter());
            options = OptionUtil.parseOption(options,presets,player);

            StyleNode node = new StyleNode(matcher.start(),matcher.end(),begin,end,options,functions,level);
            result.add(node);
        } while (matcher.find());

        return result;
    }

    private List<StyleNode> parseNodes(List<StyleNode> nodes,String text) {
        if (nodes.isEmpty()) return nodes;
        List<StyleNode> result = new ArrayList<>();

        List<Integer> points = new ArrayList<>();
        points.add(0); points.add(text.length());
        for (StyleNode node : nodes) {
            points.add(node.matchStart());
            points.add(node.begin());
            points.add(node.end());
            points.add(node.matchEnd());
        }
        points = points.stream().distinct().sorted().toList();

        for (int i=0;i<points.size()-1;i++){
            int start = points.get(i);
            int end = points.get(i+1);
            int mid = (start + end) / 2;

            boolean isDelimiter = false;
            for (StyleNode node : nodes){
                if (
                        (mid >= node.matchStart() && mid < node.begin()) ||
                        (mid >= node.end() && mid < node.matchEnd())
                )
                {
                    isDelimiter = true;
                    break;
                }
            }
            if (isDelimiter) continue;

            TreeSet<StyleNode> styles = new TreeSet<>(Comparator.comparing(StyleNode::level));
            for (StyleNode node : nodes) {
                if (mid >= node.begin() && mid < node.end()) {
                    styles.add(node);
                }
            }

            StyleNode active = new StyleNode(
                    start,end,
                    start,end,
                    new ArrayList<>(),
                    new ArrayList<>(),
                    0
            );

            for (StyleNode style : styles) {
                active.options().addAll(style.options());
                active.functions().addAll(style.functions());
            }

            result.add(active);
        }

        return result;
    }

    private MutableText applyStyle(MutableText text,List<StyleNode> nodes,ServerPlayerEntity player){
        if (nodes.isEmpty()) return text;

        Runs runs = flatten(text);
        MutableText result = Text.empty();

        int lastEnd = 0;
        for (StyleNode node:nodes){
            List<String> options = node.options();
            List<Function<StyleParameter, MutableText>> functions = node.functions();

            result.append(slice(runs,lastEnd,node.begin()));

            MutableText segment = slice(runs,node.begin(),node.end());
            for (int i=0;i<functions.size();i++){
                segment = functions.get(i).apply(StyleParameter.of(
                        segment,options.get(i),player
                ));
            }
            result.append(segment);
            lastEnd = node.end();
        }
        result.append(slice(runs,lastEnd,runs.full().length()));

        return result;
    }

    public MutableText applyMention(MutableText text, List<MentionSegment> mentionSegments,ServerPlayerEntity player){
        if (mentionSegments.isEmpty()) return text;

        Runs runs = flatten(text);
        MutableText result = Text.empty();

        int lastEnd = 0;
        for (MentionSegment mentionSegment : mentionSegments){
            result.append(slice(runs,lastEnd, mentionSegment.begin()));

            MutableText segment = slice(runs, mentionSegment.begin(), mentionSegment.end());
            segment.fillStyle(mentionSegment.style());
            for (StyleAction action : mentionSegment.styles()){
                segment = registry
                        .get(action.styleType())
                        .apply(StyleParameter.of(segment,action.preset(),player));
            }

            result.append(segment);
            lastEnd = mentionSegment.end();
        }
        result.append(slice(runs, lastEnd, runs.full().length()));

        return result;
    }
}