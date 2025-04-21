package com.hanhy06.betterchat.util;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

public class Timestamp {
    public static String timeStamp(){
       ZonedDateTime now = ZonedDateTime.now();
       DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss z");
        return now.format(formatter);
    }
}
