package com.hanhy06.betterchat.util;

import net.minecraft.text.HoverEvent;
import net.minecraft.text.Text;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

public class Timestamp {
    public static String timeStamp(){
        Instant now = Instant.now();
        ZoneId id = ZoneId.systemDefault();
        ZonedDateTime time = now.atZone(id);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy년 MM월 dd일 HH시 mm분 ss초에 전송됨.\n클릭시 클립보드에 복사.");
        return time.format(formatter);
    }
}
