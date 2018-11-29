package com.example.asus.syoucloud.util;

public class TimeUtil {
    public static String parseToString(int time) {
        StringBuilder mTime = new StringBuilder();
        int minute = time / 60, second = time % 60;
        if (minute >= 10) mTime.append(minute).append(":");
        else mTime.append("0").append(minute).append(":");
        if (second >= 10) mTime.append(second);
        else mTime.append("0").append(second);
        return mTime.toString();
    }
}
