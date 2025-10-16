package com.hanhy06.embellish_chat.styling;

import com.hanhy06.embellish_chat.config.ConfigListener;
import com.hanhy06.embellish_chat.data.Config;
import com.hanhy06.embellish_chat.data.RegexAction;
import com.hanhy06.embellish_chat.data.RegexActionCache;

import java.util.List;

public class TextStylingManager implements ConfigListener {
    private List<RegexActionCache> inChatStyling;
    private List<RegexActionCache> inCommandStyling;
    private List<RegexActionCache> inAnvilStyling;

    @Override
    public void onConfigReload(Config newConfig) {
        this.inChatStyling = actionCaching(newConfig.inChatStyling());
        this.inCommandStyling = actionCaching(newConfig.inCommandStyling());
        this.inAnvilStyling = actionCaching(newConfig.inAnvilStyling());
    }

    private List<RegexActionCache> actionCaching(List<RegexAction> actions){
        return actions.stream().map(RegexActionCache::of).toList();
    }
}
