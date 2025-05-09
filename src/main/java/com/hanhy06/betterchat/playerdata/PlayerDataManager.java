package com.hanhy06.betterchat.playerdata;

import com.hanhy06.betterchat.mention.MentionData;
import net.minecraft.util.UserCache;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class PlayerDataManager {
    private final Map<UUID,PlayerData> playerDataCache;
    private final Map<UUID, List<MentionData>> mentionDataCache;
    private final List<Unit> mentionDataBuffer;

    private final PlayerDataIO playerDataIO;

    private final ScheduledExecutorService scheduler;

    public PlayerDataManager(UserCache cache, Path modDirPath) {
        this.playerDataCache = new ConcurrentHashMap<>();
        this.mentionDataCache = new ConcurrentHashMap<>();
        this.mentionDataBuffer = new ArrayList<>();

        playerDataIO = new PlayerDataIO(cache,modDirPath);

        scheduler = Executors.newScheduledThreadPool(1);
    }

    public void startScheduler(){
        Runnable task = new Runnable() {
            @Override
            public void run() {
                for (Unit unit : mentionDataBuffer){
                    List<MentionData> list =  mentionDataCache.get(unit.uuid);
                    list.add(unit.mention);

                    PlayerData playerData = playerDataCache.get(unit.uuid);

                    playerDataIO.saveMentionData(playerData,playerData.getLastPage(),list);

                    if (list.size()==21){
                        playerData.setLastPage(playerData.getLastPage()+1);
                        playerDataIO.savePlayerData(playerData);
                        mentionDataCache.put(unit.uuid,new ArrayList<>());
                    }
                }

                mentionDataBuffer.clear();
            }
        };

        scheduler.scheduleAtFixedRate(task,0 ,1,TimeUnit.MINUTES);
    }

    public void stopScheduler(){
        if(scheduler !=null){
            scheduler.shutdown();
            try {
                if (!scheduler.awaitTermination(1, TimeUnit.MINUTES)){
                    scheduler.shutdownNow();
                }
            } catch (InterruptedException e) {
                scheduler.shutdownNow();
                Thread.currentThread().interrupt();
            }
        }
    }

    public void bufferWrite(UUID uuid,MentionData data){
        mentionDataBuffer.add(new Unit(uuid,data));
    }

    public Map<UUID, PlayerData> getPlayerDataCache() {
        return playerDataCache;
    }

    public Map<UUID, List<MentionData>> getMentionDataCache() {
        return mentionDataCache;
    }

    private record Unit(
            UUID uuid,MentionData mention
    ){}
}
