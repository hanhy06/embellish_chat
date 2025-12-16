package io.github.hanhy06.embellishchat.discord;

public record DiscordPayload(
        int type,
        String name,
        String avatar,
        String content
) {
    public static DiscordPayload of(Discord discord,String content){
        return new DiscordPayload(3,discord.name(), discord.avatar_url(), content);
    }
}
