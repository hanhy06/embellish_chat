package com.hanhy06.betterchat.command;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.JsonOps;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.minecraft.dialog.type.Dialog;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;

public class InboxCommand {
    public static void registerInboxCommand(){
        CommandRegistrationCallback.EVENT.register(
                (commandDispatcher, commandRegistryAccess, registrationEnvironment) -> {
                    commandDispatcher.register(
                            CommandManager.literal("inbox")
                                    .executes(InboxCommand::executeInbox)
                    );
                }
        );
    }

    private static int executeInbox(CommandContext<ServerCommandSource> context){
        ServerPlayerEntity player = context.getSource().getPlayer();

        String josn = """
                {"type":"minecraft:multi_action","title":{"text":"제목"},"body":[{"type":"minecraft:item","item":{"id":"minecraft:diamond","count":1},"description":{"text":"이것은 다이아몬드입니다."}}],"actions":[{"label":{"text":"버튼"},"action":{"type":"run_command","command":"/say Hello"}}]}
                """;
        try {
            JsonElement jsonElement = JsonParser.parseString(josn);

            DataResult<Dialog> result = Dialog.CODEC.parse(JsonOps.INSTANCE, jsonElement);

            result.result().ifPresent(dialog -> {
               player.openDialog(RegistryEntry.of(dialog));
            });
        } catch (Exception e) {
            e.printStackTrace();
        }

        return 1;
    }
}
