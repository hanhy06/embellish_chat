package io.github.hanhy06.embellishchat.mixin;

import io.github.hanhy06.embellishchat.EmbellishChat;
import io.github.hanhy06.embellishchat.config.ConfigManager;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.ChatType;
import net.minecraft.network.chat.OutgoingChatMessage;
import net.minecraft.network.chat.PlayerChatMessage;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.resources.ResourceKey;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import java.util.Optional;

@Mixin(OutgoingChatMessage.Player.class)
public class SentMessageMixin {
    @Shadow
    @Final
    private PlayerChatMessage message;
    @Unique
    private static final ResourceKey<ChatType> CLEAR = ResourceKey.create(Registries.CHAT_TYPE, ResourceLocation.fromNamespaceAndPath(EmbellishChat.MOD_ID,"clear"));

    @ModifyVariable(
            method = "sendToPlayer",
            at = @At("HEAD"),
            argsOnly = true,
            ordinal = 0
    )
    private ChatType.Bound modifyChatType(ChatType.Bound chatType) {
        if (!ConfigManager.getConfig().disable_vanilla_chat_format() || !chatType.chatType().is(ChatType.CHAT)) {
            return chatType;
        }

        Optional<Holder.Reference<ChatType>> clearChatType = EmbellishChat.SERVER
                .registryAccess()
                .lookupOrThrow(Registries.CHAT_TYPE)
                .get(CLEAR);

        return clearChatType
                .map(holder -> new ChatType.Bound(holder, this.message.decoratedContent(), chatType.targetName()))
                .orElse(chatType);
    }
}
