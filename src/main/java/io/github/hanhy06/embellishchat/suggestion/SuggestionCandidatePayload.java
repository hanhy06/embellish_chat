package io.github.hanhy06.embellishchat.suggestion;

import io.github.hanhy06.embellishchat.EmbellishChat;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

import java.util.List;

public record SuggestionCandidatePayload(
        List<String> candidates,
        boolean playerSuggestion
) implements CustomPacketPayload {
    public static final ResourceLocation SUGGESTION_CANDIDATES_ID = ResourceLocation.fromNamespaceAndPath(EmbellishChat.MOD_ID, "suggestion_candidates");
    public static final CustomPacketPayload.Type<SuggestionCandidatePayload> TYPE = new CustomPacketPayload.Type<>(SUGGESTION_CANDIDATES_ID);

    public static final StreamCodec<RegistryFriendlyByteBuf, SuggestionCandidatePayload> CODEC = StreamCodec.composite(
            ByteBufCodecs.stringUtf8(512).apply(ByteBufCodecs.list(128)),
            SuggestionCandidatePayload::candidates,
            ByteBufCodecs.BOOL,
            SuggestionCandidatePayload::playerSuggestion,
            SuggestionCandidatePayload::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}

