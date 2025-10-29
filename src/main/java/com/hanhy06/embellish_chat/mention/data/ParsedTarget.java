package com.hanhy06.embellish_chat.mention.data;

import com.hanhy06.embellish_chat.styling.rule.StyleAction;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Style;

import java.util.List;

public record ParsedTarget(ParsedMention mention,List<ServerPlayerEntity> players, Style style) {
    public static ParsedTarget of(ParsedMention mention,List<ServerPlayerEntity> players, Style style){
        return new ParsedTarget(mention,players,style);
    }

    public Mention createMention(){
        int begin = this.mention.begin(); int end = this.mention.end();
        List<StyleAction> styles = this.mention.rule().styles();
        Style style = this.style;

        return new Mention(
                begin,
                end,
                styles,
                style
        );
    }
}
