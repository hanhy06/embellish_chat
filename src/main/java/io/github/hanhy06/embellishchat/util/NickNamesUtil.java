package io.github.hanhy06.embellishchat.util;

import eu.pb4.stylednicknames.NicknameHolder;
import io.github.hanhy06.embellishchat.EmbellishChat;
import net.minecraft.server.network.ServerPlayerEntity;

public class NickNamesUtil {
    public static ServerPlayerEntity getPlayerByNickName(String name){
        for (ServerPlayerEntity player : EmbellishChat.SERVER.getPlayerManager().getPlayerList()){
            String nickName = NicknameHolder.of(player).styledNicknames$get();

            if (nickName != null && nickName.equals(name)) return player;
        }
        return null;
    }
}
