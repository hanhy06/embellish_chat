package io.github.hanhy06.embellishchat.mixin;

import io.github.hanhy06.embellishchat.EmbellishChat;
import io.github.hanhy06.embellishchat.config.ConfigManager;
import net.minecraft.network.message.MessageType;
import net.minecraft.network.message.SentMessage;
import net.minecraft.network.message.SignedMessage;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import java.util.Optional;

@Mixin(SentMessage.Chat.class)
public class SentMessageMixin {
    @Shadow
    @Final
    private SignedMessage message;
    @Unique
    private static final RegistryKey<MessageType> CLEAR = RegistryKey.of(RegistryKeys.MESSAGE_TYPE, Identifier.of(EmbellishChat.MOD_ID,"clear"));

    @ModifyVariable(
            method = "send",
            at = @At("HEAD"),
            argsOnly = true,
            ordinal = 0
    )
    private MessageType.Parameters modifyParams(MessageType.Parameters params) {
        if (!ConfigManager.getConfig().DISABLE_VANILLA_CHAT_FORMAT()) return params;

        Registry<MessageType> registry = EmbellishChat.SERVER.getRegistryManager().getOrThrow(RegistryKeys.MESSAGE_TYPE);
        Optional<RegistryEntry.Reference<MessageType>> optional = registry.getEntry(CLEAR.getValue());

        return optional.map(entry -> new MessageType.Parameters(entry, this.message.getContent(), params.targetName())).orElse(params);
    }
}
