package io.github.hanhy06.embellishchat.util;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.network.ServerPlayerEntity;
import xaero.pac.common.server.api.OpenPACServerAPI;
import xaero.pac.common.server.parties.party.api.IPartyManagerAPI;
import xaero.pac.common.server.parties.party.api.IServerPartyAPI;

import java.util.HashSet;

public class OpenPartyUtil {
    public static HashSet<ServerPlayerEntity> getPartyPlayers(MinecraftServer server, ServerPlayerEntity player){
        IPartyManagerAPI partyManager = OpenPACServerAPI.get(server).getPartyManager();
        PlayerManager manager = server.getPlayerManager();
        HashSet<ServerPlayerEntity> players = new HashSet<>();

        IServerPartyAPI party = partyManager.getPartyByMember(player.getUuid());
        if (party != null) {
            party.getMemberInfoStream().forEach(member -> {
                players.add(manager.getPlayer(member.getUUID()));
            });
        }

        return players;
    }
}
