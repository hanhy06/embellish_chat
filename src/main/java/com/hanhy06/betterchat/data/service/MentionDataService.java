package com.hanhy06.betterchat.data.service;

import com.hanhy06.betterchat.BetterChat;
import com.hanhy06.betterchat.config.ConfigData;
import com.hanhy06.betterchat.data.model.MentionData;
import com.hanhy06.betterchat.data.repository.MentionDataRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class MentionDataService {
    private final MentionDataRepository mentionDataRepository;

    private final ConcurrentLinkedQueue<MentionData> mentionDataBuffer;

    private final ScheduledExecutorService scheduler;

    private static final int ITEMS_PER_PAGE = 20;

    public MentionDataService(ConfigData configData, MentionDataRepository mentionDataRepository) {
        this.mentionDataRepository = mentionDataRepository;

        this.mentionDataBuffer = new ConcurrentLinkedQueue<>();

        this.scheduler = Executors.newScheduledThreadPool(1);
    }

    public void handleServerStart(ConfigData configData){
        scheduler.scheduleAtFixedRate(this::bufferClearProcess, 0, configData.mentionBufferClearIntervalMinutes(), TimeUnit.MINUTES);
    }

    public int getPendingMentionCount(){
        return mentionDataBuffer.size();
    }

    public void bufferClearProcess() {
        if (mentionDataBuffer.isEmpty()) return;

        BetterChat.LOGGER.info("Executing buffer clear process");

        List<MentionData> mentionDatas = new ArrayList<>();
        MentionData mentionData;
        while ((mentionData = mentionDataBuffer.poll()) != null) {
            mentionDatas.add(mentionData);
        }

        for (MentionData data : mentionDatas) {
            mentionDataRepository.writeMentionData(data);
        }

        BetterChat.LOGGER.info("Successfully cleared buffer and recorded {} mention data entries to the database.", mentionDatas.size());
    }

    public void handleServerStop() {
        bufferClearProcess();

        if (scheduler != null && !scheduler.isShutdown()) {
            scheduler.shutdown();
            try {
                if (!scheduler.awaitTermination(1, TimeUnit.MINUTES)) {
                    scheduler.shutdownNow();
                }
            } catch (InterruptedException e) {
                scheduler.shutdownNow();
                Thread.currentThread().interrupt();
            }
        }
    }

    public void bufferWrite(MentionData data) {
        mentionDataBuffer.add(data);
    }

    public void writeMentionData(MentionData mentionData){
        mentionDataRepository.writeMentionData(mentionData);
    }

    public List<MentionData> getMentionData(UUID uuid, int pageNumber){
        int mentionCount = mentionDataRepository.countMentionData(uuid);
        if (mentionCount < 1) return new ArrayList<>();
        return mentionDataRepository.readMentionData(uuid,ITEMS_PER_PAGE,ITEMS_PER_PAGE*pageNumber);
    }
}
