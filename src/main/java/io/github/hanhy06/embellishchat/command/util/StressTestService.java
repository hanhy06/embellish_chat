package io.github.hanhy06.embellishchat.command.util;

import io.github.hanhy06.embellishchat.EmbellishChat;
import io.github.hanhy06.embellishchat.message.MessageProcessor;
import io.github.hanhy06.embellishchat.util.PlaceHolderUtil;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.PlayerChatMessage;
import net.minecraft.server.MinecraftServer;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class StressTestService {
    private static final String STRESS_TEST_FORMAT = """
        <gray>── Test Configuration ──</gray>
        • <yellow>Total Ticks</yellow>: %d
        • <yellow>Count Per Tick</yellow>: %d
        • <yellow>Test Message</yellow>: %s
    
        <gray>── Performance Analysis ──</gray>
        • <aqua>Ticks</aqua>: %d
        • <dark_green>Minimum</dark_green>: %dms
        • <red>Maximum</red>: %dms
        • <green>Average</green>: %.2fms
        • <light_purple>Median</light_purple>: %.2fms
        • <gold>Total Processing Time</gold>: %dms
    
        <gray>── Tick Analysis ──</gray>
        • <blue>Used Ticks</blue>: %d / %d
        • <blue>Usage</blue>: %.2f%%
        """;

    private int remainingTicks;
    private final int countPerTick;
    private final String testText;
    private final UUID testUuid;
    private final CommandSourceStack testSource;
    private final List<Long> testResults;

    public StressTestService(int ticks, int count, String text, CommandSourceStack source){
        remainingTicks = ticks;
        countPerTick = count;
        testText = text;
        testUuid = source.getPlayer().getUUID();
        testSource = source;
        testResults = new ArrayList<>(ticks);
    }

    public boolean executeTest(){
        if (remainingTicks <= 0) {
            return true;
        }

        if (testSource.getPlayer() == null){
            testSource.sendFailure(Component.literal("Unable to retrieve the player for an unknown reason"));
            completeStressTest();
            return true;
        }

        long startTime = System.nanoTime();
        for (int i = 0; i < countPerTick; i++) {
            PlayerChatMessage message = PlayerChatMessage.unsigned(testUuid, testText);
            MessageProcessor.INSTANCE.handleMessage(message);
        }
        testResults.add((System.nanoTime() - startTime) / 1_000_000L);
        testSource.getPlayer().sendOverlayMessage(Component.literal("Time remaining: %d tick".formatted(remainingTicks)));
        remainingTicks--;

        if (remainingTicks <= 0) {
            completeStressTest();
            return true;
        }

        return false;
    }

    public void completeStressTest() {
        int totalTicks = testResults.size();
        long totalProcessing = testResults.stream().mapToLong(Long::longValue).sum();
        long min = testResults.stream().mapToLong(Long::longValue).min().orElse(0);
        long max = testResults.stream().mapToLong(Long::longValue).max().orElse(0);
        double average = totalProcessing / (double) totalTicks;

        if (totalTicks < 1) {
            testSource.sendFailure(Component.literal("Cannot display statistics. No tests have been executed yet."));
            return;
        }

        testResults.sort(null);
        double median;
        if (totalTicks % 2 == 0) {
            median = (testResults.get(totalTicks / 2 - 1) + testResults.get(totalTicks / 2)) / 2.0;
        } else {
            median = testResults.get(totalTicks / 2);
        }

        long occupiedTicks = totalProcessing / 50;
        double usagePercentTicks = (occupiedTicks / (double) totalTicks) * 100.0;

        Component message = PlaceHolderUtil.parseTag(String.format(
                STRESS_TEST_FORMAT,
                totalTicks+remainingTicks,countPerTick,testText,
                totalTicks, min, max, average, median, totalProcessing,
                occupiedTicks, totalTicks, usagePercentTicks
        ));

        testSource.sendSuccess(() -> Component.literal("Stress test completed!"), true);
        testSource.sendSuccess(() -> message, true);
        EmbellishChat.LOGGER.info(message.getString());
    }
}
