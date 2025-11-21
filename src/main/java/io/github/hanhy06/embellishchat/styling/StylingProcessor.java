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

import java.lang.reflect.Array;
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

        String string = text.getString();
        Set<StyleNode> nodeSet = new TreeSet<>(Comparator
                .comparing(StyleNode::begin)
                .thenComparing(StyleNode::level)
                .thenComparing(StyleNode::end)
        );
        for (int i=0;i<rules.size();i++){
            nodeSet.addAll(parseStyles(string,rules.get(i),player,i));
        }

        List<StyleNode> nodeList = parseNodes(nodeSet.stream().toList());

        return text;
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

            StyleNode node = new StyleNode(begin,end,options,functions,level);
            result.add(node);
        } while (matcher.find());

        return result;
    }

    private List<StyleNode> parseNodes(List<StyleNode> nodes){
        List<StyleNode> result = new ArrayList<>();

        for (int i=0;i<nodes.size()-1;i++){
            StyleNode current = nodes.get(i);
            StyleNode next = nodes.get(i++);

            if (current.begin() == next.begin() && current.end() == next.end()){
                current.options().addAll(next.options());
                current.functions().addAll(next.functions());
                result.add(current);
                i+=2;
            } else if (next.begin() < current.end()) {
                StyleNode node1 = new StyleNode(
                        current.begin(),next.begin(),
                        current.options(),current.functions(),
                        current.level()
                );
                result.add(node1);

                current.options().addAll(next.options());
                current.functions().addAll(next.functions());
                StyleNode node2 = new StyleNode(
                        next.begin(),current.end(),
                        current.options(),current.functions(),
                        current.level()
                );
                result.add(node2);

                StyleNode node3 = new StyleNode(
                        current.end(),next.end(),
                        next.options(),next.functions(),
                        next.level()
                );
                result.add(node3);
                i+=2;
            }else {
                result.add(current);
            }
        }

        return result;
    }

    public MutableText applyMention(MutableText text, List<MentionSegment> mentionSegments){
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
                        .apply(StyleParameter.of(segment,action.preset()));
            }

            result.append(segment);
            lastEnd = mentionSegment.end();
        }
        result.append(slice(runs, lastEnd, runs.full().length()));

        return result;
    }
}