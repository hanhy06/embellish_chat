package io.github.hanhy06.embellishchat.suggestion;

import io.github.hanhy06.embellishchat.EmbellishChat;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.Identifier;
import org.jspecify.annotations.NonNull;

import java.util.List;

public record SuggestionCandidatePayload(
        List<String> candidates,
        List<String> nicknames,
        boolean hasPlayer
) implements CustomPacketPayload {
    public static final Identifier SUGGESTION_CANDIDATES_ID = Identifier.fromNamespaceAndPath(EmbellishChat.MOD_ID, "mention_pattern");
    public static final CustomPacketPayload.Type<SuggestionCandidatePayload> TYPE = new CustomPacketPayload.Type<>(SUGGESTION_CANDIDATES_ID);

    public static final StreamCodec<RegistryFriendlyByteBuf, SuggestionCandidatePayload> CODEC = StreamCodec.composite(
            ByteBufCodecs.stringUtf8(512).apply(ByteBufCodecs.list(128)),
            SuggestionCandidatePayload::candidates,
            ByteBufCodecs.stringUtf8(512).apply(ByteBufCodecs.list(128)),
            SuggestionCandidatePayload::nicknames,
            ByteBufCodecs.BOOL,
            SuggestionCandidatePayload::hasPlayer,
            SuggestionCandidatePayload::new
    );

    @Override
    public @NonNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}

