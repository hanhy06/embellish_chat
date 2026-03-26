package io.github.hanhy06.embellishchat.mixin;

import io.github.hanhy06.embellishchat.EmbellishChat;
import io.github.hanhy06.embellishchat.config.ConfigManager;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.ChatType;
import net.minecraft.network.chat.OutgoingChatMessage;
import net.minecraft.network.chat.PlayerChatMessage;
import net.minecraft.resources.Identifier;
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
    private static final ResourceKey<ChatType> CLEAR = ResourceKey.create(Registries.CHAT_TYPE, Identifier.fromNamespaceAndPath(EmbellishChat.MOD_ID,"clear"));

    @ModifyVariable(
            method = "sendToPlayer",
            at = @At("HEAD"),
            argsOnly = true,
            ordinal = 0
    )
    private ChatType.Bound modifyParams(ChatType.Bound params) {
        if (!params.chatType().is(ChatType.CHAT) || !ConfigManager.getConfig().disable_vanilla_chat_format()) return params;

        Registry<ChatType> registry = EmbellishChat.SERVER.registryAccess().lookupOrThrow(Registries.CHAT_TYPE);
        Optional<Holder.Reference<ChatType>> optional = registry.get(CLEAR.identifier());

        return optional.map(entry -> new ChatType.Bound(entry, this.message.decoratedContent(), params.targetName())).orElse(params);
    }
}
