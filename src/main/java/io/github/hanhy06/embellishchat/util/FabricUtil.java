package io.github.hanhy06.embellishchat.util;

import net.fabricmc.loader.api.FabricLoader;

public class FabricUtil {
    public static boolean isLuckPerms;
    public static boolean isNickName;
    public static boolean isAdvancedChat;

    public static void registerStatus(){
        isLuckPerms = FabricLoader.getInstance().isModLoaded("luckperms");
        isNickName = FabricLoader.getInstance().isModLoaded("styled-nicknames");
        isAdvancedChat = FabricLoader.getInstance().isModLoaded("advanced-chat");
    }
}
