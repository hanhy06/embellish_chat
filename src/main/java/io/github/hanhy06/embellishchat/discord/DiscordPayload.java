package io.github.hanhy06.embellishchat.discord;

public record DiscordPayload(
        int type,
        String name,
        String avatar,
        String content
) {
    public static DiscordPayload of(DiscordProfile discordProfile, String content){
        return new DiscordPayload(3, discordProfile.name(), discordProfile.avatar(), content);
    }
}
