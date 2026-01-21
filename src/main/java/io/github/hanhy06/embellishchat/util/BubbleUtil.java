package io.github.hanhy06.embellishchat.util;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.decoration.DisplayEntity;
import net.minecraft.scoreboard.ScoreboardDisplaySlot;
import net.minecraft.scoreboard.ScoreboardObjective;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.math.AffineTransformation;
import org.joml.Vector3f;

import java.util.*;

public class BubbleUtil {

    private static final List<BubbleContext> activeBubbles = new ArrayList<>();

    private record BubbleContext(DisplayEntity.TextDisplayEntity entity, ServerPlayerEntity owner) {
    }

    public static void registerTickEvent() {
        ServerTickEvents.START_SERVER_TICK.register(server -> {
            if (activeBubbles.isEmpty()) return;

            Iterator<BubbleContext> iterator = activeBubbles.iterator();
            while (iterator.hasNext()) {
                BubbleContext context = iterator.next();
                DisplayEntity.TextDisplayEntity entity = context.entity();
                ServerPlayerEntity owner = context.owner();

                if (entity.age >= 50 || owner.isRemoved()) {
                    entity.discard();
                    iterator.remove();
                }
            }

            Map<UUID, Integer> stackCounts = new HashMap<>();
            ScoreboardObjective objectBlow = server.getScoreboard().getObjectiveForSlot(ScoreboardDisplaySlot.BELOW_NAME);

            for (int i = activeBubbles.size() - 1; i >= 0; i--) {
                BubbleContext context = activeBubbles.get(i);

                DisplayEntity.TextDisplayEntity entity = context.entity();
                ServerPlayerEntity owner = context.owner();
                UUID ownerUuid = owner.getUuid();

                int stackIndex = stackCounts.getOrDefault(ownerUuid, 0);
                double yOffset = 0.2 + (stackIndex * 0.3) + (objectBlow != null ? 0.2:0);

                entity.setPosition(
                        owner.getX(),
                        owner.getY() + owner.getHeight() + yOffset,
                        owner.getZ()
                );

                stackCounts.put(ownerUuid, stackIndex + 1);

                if (entity.age == 45) {
                    entity.setTransformation(new AffineTransformation(
                            null,
                            null,
                            new Vector3f(0f, 0f, 0f),
                            null
                    ));
                    entity.setInterpolationDuration(5);
                    entity.setStartInterpolation(0);
                }
            }
        });
    }

    public static void spawnDisplayEntity(ServerPlayerEntity player, Text text) {
        ServerWorld world = player.getEntityWorld();
        DisplayEntity.TextDisplayEntity entity = new DisplayEntity.TextDisplayEntity(EntityType.TEXT_DISPLAY, world);

        entity.setText(text);
        entity.setBillboardMode(DisplayEntity.BillboardMode.CENTER);
        entity.setPosition(player.getX(), player.getY() + player.getHeight() - 0.2f, player.getZ());
        entity.setTeleportDuration(1);

        entity.setTransformation(new AffineTransformation(
                new Vector3f(0f, 0f, 0f),
                null,
                new Vector3f(0.1f, 0.1f, 0.1f),
                null
        ));

        world.spawnEntity(entity);

        entity.setTransformation(new AffineTransformation(
                new Vector3f(0f, 0.4f, 0f),
                null,
                new Vector3f(1f, 1f, 1f),
                null
        ));
        entity.setInterpolationDuration(5);
        entity.setStartInterpolation(0);

        activeBubbles.add(new BubbleContext(entity, player));
    }
}