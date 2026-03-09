package io.github.hanhy06.embellishchat.config;

import io.github.hanhy06.embellishchat.config.configs.Config;

public interface ConfigListener {
    void onConfigReload(Config newConfig);
}
