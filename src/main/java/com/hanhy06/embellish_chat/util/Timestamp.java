package com.hanhy06.embellish_chat.util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Timestamp {
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public static String timeStamp() {
        return LocalDateTime.now().format(FORMATTER);
    }
}
