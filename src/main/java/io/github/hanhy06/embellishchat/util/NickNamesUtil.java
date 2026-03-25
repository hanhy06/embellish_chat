package io.github.hanhy06.embellishchat.util;

import eu.pb4.stylednicknames.NicknameHolder;
import io.github.hanhy06.embellishchat.EmbellishChat;
import net.minecraft.server.level.ServerPlayer;

public class NickNamesUtil {
    public static ServerPlayer getPlayerByNickName(String name){
        for (ServerPlayer player : EmbellishChat.SERVER.getPlayerList().getPlayers()){
            String nickName = NicknameHolder.of(player).styledNicknames$get();
            if (nickName != null && nickName.equals(name)) return player;
        }
        return null;
    }
}
