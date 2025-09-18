package com.hanhy06.embellish_chat.config;

import com.hanhy06.embellish_chat.data.Config;

public interface ConfigListener {
    void onConfigReload(Config newConfig);
}
