package io.github.hanhy06.embellishchat.mention.data;

import io.github.hanhy06.embellishchat.styling.rule.StyleAction;
import io.github.hanhy06.embellishchat.util.LuckPermsUtil;
import io.github.hanhy06.embellishchat.util.PlaceHolderUtil;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.sound.SoundEvent;
import net.minecraft.text.Style;
import net.minecraft.text.Text;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public record ParsedTarget(ParsedMention mention, List<ServerPlayerEntity> targets, Style style) {
    public static ParsedTarget of(ParsedMention mention,List<ServerPlayerEntity> targets, Style style){
        return new ParsedTarget(mention,new ArrayList<>(targets),style);
    }

    public MentionSegment createMention(){
        int begin = this.mention.begin(); int end = this.mention.end();
        List<StyleAction> styles = this.mention.rule().styles();
        Style style = this.style;

        return new MentionSegment(
                begin,
                end,
                styles,
                style
        );
    }

    public Set<MentionTarget> createTarget(ServerPlayerEntity sender,boolean notificationEnable,boolean permissionCheckEnabled){
        Set<MentionTarget> result = new HashSet<>();
        SoundEvent sound = SoundEvent.of(this.mention.rule().sound(),this.mention.rule().pitch());
        Text title = PlaceHolderUtil.getParedOption(this.mention.rule().title(),sender);

        for (ServerPlayerEntity target : this.targets){
            if (notificationEnable && permissionCheckEnabled && LuckPermsUtil.getNotification(target)) {
                result.add(new MentionTarget(target,sound,title));
            }else {
                result.add(new MentionTarget(target,sound,title));
            }
        }

        return result;
    }
}
