package io.github.hanhy06.embellishchat.styling.util;

import com.mojang.math.Transformation;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FontDescription;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Display;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.scores.DisplaySlot;
import net.minecraft.world.scores.Objective;
import org.joml.Vector3f;

import java.util.*;

public class BubbleUtil {
    private static final List<BubbleContext> activeBubbles = new ArrayList<>();

    private static final Vector3f SMALL_SCALE = new Vector3f(0,0,0);
    private static final Vector3f BIG_SCALE = new Vector3f(1.1f,1.1f,1.1f);
    private static final Vector3f Y_OFFSET = new Vector3f(0,0.4f,0);

    private record BubbleContext(Display.TextDisplay entity, ServerPlayer owner) {
    }

    public static void registerTickEvent() {
        ServerTickEvents.END_SERVER_TICK.register(BubbleUtil::updateBubbles);
    }

    private static void updateBubbles(MinecraftServer server){
        if (activeBubbles.isEmpty()) return;

        Map<UUID, Integer> stackCounts = new HashMap<>();
        Objective blowObject = server.getScoreboard().getDisplayObjective(DisplaySlot.BELOW_NAME);

        for (int i = activeBubbles.size() - 1; i >= 0; i--) {
            BubbleContext context = activeBubbles.get(i);

            Display.TextDisplay entity = context.entity();
            ServerPlayer owner = context.owner();
            UUID ownerUuid = owner.getUUID();

            if (entity.tickCount >= 75 || owner.isRemoved()) {
                entity.discard();
                activeBubbles.remove(i);
                continue;
            }
            else if (entity.tickCount == 70) {
                entity.setTransformation(
                        new Transformation(null, null, SMALL_SCALE, null)
                );
                entity.setTransformationInterpolationDuration(5);
                entity.setTransformationInterpolationDelay(0);
            }

            int stackIndex = stackCounts.getOrDefault(ownerUuid, 0);
            double yPos = owner.getY() + owner.getBbHeight() + 0.2 + (stackIndex * 0.32) + (blowObject != null ? 0.2:0);
            entity.setPos(owner.getX(), yPos, owner.getZ());

            stackCounts.put(ownerUuid, stackIndex + 1);
        }
    }

    public static void spawnDisplayEntity(ServerPlayer owner, Component text) {
        ServerLevel level = owner.level();
        Display.TextDisplay entity = new Display.TextDisplay(EntityType.TEXT_DISPLAY, level);

        level.addFreshEntity(entity);

        entity.setText(preprocessing(text));
        entity.setBillboardConstraints(Display.BillboardConstraints.CENTER);
        entity.setPos(owner.getX(), owner.getY() + owner.getBbHeight() - 0.2f, owner.getZ());
        entity.setPosRotInterpolationDuration(1);
        entity.setTransformation(
                new Transformation(null, null, SMALL_SCALE, null)
        );

        entity.setTransformation(
                new Transformation(Y_OFFSET, null, BIG_SCALE, null)
        );
        entity.setTransformationInterpolationDuration(5);
        entity.setTransformationInterpolationDelay(0);

        activeBubbles.add(new BubbleContext(entity, owner));
    }

    private static Component preprocessing(Component text) {
        MutableComponent result = Component.empty();

        text.visit((style, content) -> {
            Style newStyle = style.withBold(true);
            if (style.getFont() instanceof FontDescription.AtlasSprite || style.getFont() instanceof FontDescription.PlayerSprite) {
                newStyle = newStyle.withFont(null);
            }
            result.append(Component.literal(content).setStyle(newStyle));
            return Optional.empty();
        }, Style.EMPTY);

        return result;
    }
}
