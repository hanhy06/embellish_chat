package io.github.hanhy06.embellishchat.styling.util;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.decoration.DisplayEntity;
import net.minecraft.scoreboard.ScoreboardDisplaySlot;
import net.minecraft.scoreboard.ScoreboardObjective;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.math.AffineTransformation;
import org.joml.Vector3f;

import java.util.*;

public class BubbleUtil {
    private static final List<BubbleContext> activeBubbles = new ArrayList<>();

    private static final Vector3f SMALL_SCALE = new Vector3f(0,0,0);
    private static final Vector3f BIG_SCALE = new Vector3f(1,1,1);
    private static final Vector3f Y_OFFSET = new Vector3f(0,0.4f,0);

    private record BubbleContext(DisplayEntity.TextDisplayEntity entity, ServerPlayerEntity owner) {
    }

    public static void registerTickEvent() {
        ServerTickEvents.END_SERVER_TICK.register(BubbleUtil::updateBubbles);
    }

    private static void updateBubbles(MinecraftServer server){
        if (activeBubbles.isEmpty()) return;

        Map<UUID, Integer> stackCounts = new HashMap<>();
        ScoreboardObjective blowObject = server.getScoreboard().getObjectiveForSlot(ScoreboardDisplaySlot.BELOW_NAME);

        for (int i = activeBubbles.size() - 1; i >= 0; i--) {
            BubbleContext context = activeBubbles.get(i);

            DisplayEntity.TextDisplayEntity entity = context.entity();
            ServerPlayerEntity owner = context.owner();
            UUID ownerUuid = owner.getUuid();

            if (entity.age >= 60 || owner.isRemoved()) {
                entity.discard();
                activeBubbles.remove(i);
                continue;
            }
            else if (entity.age == 50) {
                entity.setTransformation(
                        new AffineTransformation(null, null, SMALL_SCALE, null)
                );
                entity.setInterpolationDuration(5);
                entity.setStartInterpolation(0);
            }

            int stackIndex = stackCounts.getOrDefault(ownerUuid, 0);
            double yPos = owner.getY() + owner.getHeight() + 0.2 + (stackIndex * 0.3) + (blowObject != null ? 0.2:0);
            entity.setPosition(owner.getX(), yPos, owner.getZ());

            stackCounts.put(ownerUuid, stackIndex + 1);
        }
    }

    public static void spawnDisplayEntity(ServerPlayerEntity owner, Text text) {
        ServerWorld world = owner.getEntityWorld();
        DisplayEntity.TextDisplayEntity entity = new DisplayEntity.TextDisplayEntity(EntityType.TEXT_DISPLAY, world);

        world.spawnEntity(entity);

        entity.setText(text);
        entity.setBillboardMode(DisplayEntity.BillboardMode.CENTER);
        entity.setPosition(owner.getX(), owner.getY() + owner.getHeight() - 0.2f, owner.getZ());
        entity.setTeleportDuration(1);
        entity.setTransformation(
                new AffineTransformation(null, null, SMALL_SCALE, null)
        );

        entity.setTransformation(
                new AffineTransformation(Y_OFFSET, null, BIG_SCALE, null)
        );
        entity.setInterpolationDuration(5);
        entity.setStartInterpolation(0);

        activeBubbles.add(new BubbleContext(entity, owner));
    }
}