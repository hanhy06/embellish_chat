package io.github.hanhy06.embellishchat.discord;

import java.net.URI;

public record DiscordSetting(
        URI webhook,
        String name,
        String avatar,
        String format
) {
    public static DiscordSetting createDefault(){
        return new DiscordSetting(
                URI.create(""),
                "",
                "",
                """
                        {
                            "name":"%player:name"
                            "avatar:"https://crafatar.com/avatars/%player:uuid%?size=512"
                            "content":"%embellish-chat:all%"
                        }
                        """
        );
    }
}
