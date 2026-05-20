package io.github.hanhy06.embellishchat.suggestion;

import com.mojang.authlib.GameProfile;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.ChatScreen;

import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.util.Mth;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.List;

public class SuggestionManager {
    private static final int LINE_HEIGHT = 12;
    private static final int MAX_VISIBLE = 6;
    private static final int FILL_COLOR = 0xD0101010;
    private static final int SELECTED_FILL_COLOR = 0xE0333333;
    private static final int TEXT_COLOR = 0xFFE0E0E0;

    private static List<String> candidates = new ArrayList<>();
    private static boolean playerSuggestion = false;

    private final EditBox editBox;
    private final ChatScreen chatScreen;
    private final List<String> activeCandidate;

    private boolean open;
    private boolean applyingSuggestion;
    private int tokenStart;
    private int tokenEnd;
    private int firstIndex;
    private int selectedIndex;

    public static void registerReceive(){
        ClientPlayNetworking.registerGlobalReceiver(SuggestionCandidatePayload.TYPE,(payload, context) -> {
            candidates = payload.candidates().stream()
                    .filter(str -> !(str.contains("[") || str.contains("?=") || str.isBlank()))
                    .map(str -> str.replace("\\",""))
                    .map(str -> str.replace("()",""))
                    .map(str -> str.replace("(.+?)",""))
                    .toList();

            playerSuggestion = payload.playerSuggestion();
        });

        ClientPlayConnectionEvents.DISCONNECT.register((listener, client) -> {
            candidates.clear();
            playerSuggestion = false;
        });
    }

    public SuggestionManager(EditBox editBox, ChatScreen chatScreen) {
        this.editBox = editBox;
        this.chatScreen = chatScreen;
        this.activeCandidate = new ArrayList<>();
    }

    public void update(){
        if (applyingSuggestion) {
            applyingSuggestion = false;
            return;
        }

        activeCandidate.clear();
        open = false;
        firstIndex = 0;
        selectedIndex = -1;

        int cursor = editBox.getCursorPosition();
        String value = editBox.getValue();
        if (value.startsWith("/")) return;

        int start = cursor;
        while (start > 0 && value.charAt(start - 1) != ' ') {
            start--;
        }

        if (start == cursor) return;

        tokenStart = start;
        tokenEnd = cursor;
        String token = value.substring(start, cursor);
        for (String candidate:candidates){
            if (token.length() <= candidate.length() && candidate.regionMatches(true, 0, token,0,token.length()))
                activeCandidate.add(candidate);
        }

        if (playerSuggestion) {
            Minecraft minecraft = Minecraft.getInstance();

            if (minecraft.getConnection() != null) {
                activeCandidate.addAll(
                        minecraft.getConnection().getOnlinePlayers().stream()
                                .map(PlayerInfo::getProfile)
                                .map(GameProfile::name)
                                .map(name -> "@"+name)
                                .filter(name -> token.length() <= name.length() && name.regionMatches(true, 0, token, 0, token.length()))
                                .toList()
                );
            }
        }

        open = !activeCandidate.isEmpty();
    }

    public void render(GuiGraphicsExtractor graphics) {
        if (!open) return;

        Font font = chatScreen.getFont();
        int size = Math.min(activeCandidate.size() - firstIndex, MAX_VISIBLE);
        int width = 0;
        for (int i = 0;i < size;i++) {
            width = Math.max(width,font.width(activeCandidate.get(firstIndex + i)));
        }

        int left = Mth.clamp(
                editBox.getScreenX(tokenStart),
                editBox.getScreenX(0),
                editBox.getScreenX(0) + editBox.getInnerWidth() - width - 8
        );
        int top = Math.max(4, editBox.getY() - (size * LINE_HEIGHT) - 6);
        int right = left + width + 8;
        int bottom = top + (size * LINE_HEIGHT) + 2;

        graphics.fill(left,top,right,bottom,FILL_COLOR);
        for (int i = 0;i < size;i++) {
            int index = firstIndex + i;
            int y = top + 2 + (i * LINE_HEIGHT);
            if (index == selectedIndex) {
                graphics.fill(left + 1,y - 1,right - 1,y + LINE_HEIGHT - 1,SELECTED_FILL_COLOR);
            }

            graphics.text(font,activeCandidate.get(index),left + 4,y,TEXT_COLOR);
        }
    }

    public boolean keyPressed(KeyEvent event){
        if (!open) return false;

        if (event.key() == GLFW.GLFW_KEY_TAB){
            select(selectedIndex + 1);
            String candidate = activeCandidate.get(selectedIndex);

            String value = editBox.getValue();
            value = new StringBuilder(value)
                    .replace(tokenStart, tokenEnd, candidate)
                    .toString();

            tokenEnd = tokenStart + candidate.length();
            int cursor = tokenEnd;
            if (candidate.endsWith(")")) cursor--;

            applyingSuggestion = true;
            editBox.setValue(value);
            editBox.setCursorPosition(cursor);
            editBox.setHighlightPos(cursor);
            return true;
        }


        if (event.key() == GLFW.GLFW_KEY_DOWN) {
            select(selectedIndex + 1);
            return true;
        }

        if (event.key() == GLFW.GLFW_KEY_UP) {
            select(selectedIndex - 1);
            return true;
        }

        return false;
    }

    public boolean mouseScrolled(double scrollY) {
        if (!open) return false;

        if (scrollY < 0) {
            select(selectedIndex + 1);
            return true;
        }

        if (scrollY > 0) {
            select(selectedIndex - 1);
            return true;
        }

        return false;
    }

    private void select(int index) {
        if (activeCandidate.isEmpty()) return;

        selectedIndex = index;
        if (selectedIndex >= activeCandidate.size()) {
            selectedIndex = 0;
        }

        if (selectedIndex < 0) {
            selectedIndex = activeCandidate.size() - 1;
        }

        if (selectedIndex < firstIndex) {
            firstIndex = selectedIndex;
        }

        if (selectedIndex >= firstIndex + MAX_VISIBLE) {
            firstIndex = selectedIndex - MAX_VISIBLE + 1;
        }
    }

}
