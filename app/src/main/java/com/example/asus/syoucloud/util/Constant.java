package com.example.asus.syoucloud.util;

import android.os.Environment;

public class Constant {
    public static final int SINGLE_LOOP = 1;
    public static final int LIST_LOOP = 2;
    public static final int SHUFFLE = 3;
    public static final int DY = 120;
    public static final int DT = 60;
    public static final int DECORATION_PADDING = 160;
    public static final int ITEM_DECORATION = 0xffe3e3e3;

    public static final int MAX_TYPE = 6;
    public static final int OVERLAY_TYPE = 5;
    public static final int MUSIC_PLAY_TYPE = 4;
    public static final int BOTTOM_SHOW = 3;
    public static final int BOTTOM_MAIN = 2;
    public static final int LYRIC_TYPE = 1;
    public static final int DISK_TYPE = 0;

    public static final int DOWNLOAD_BEGIN = -1;
    public static final int DOWNLOADING = 0;
    public static final int DOWNLOAD_NOT_FOUND = 1;
    public static final int DOWNLOAD_FAIL = 2;
    public static final int DOWNLOAD_SUCCESS = 3;

    public static final String PLAY = "com.example.asus.syoucloud.NotificationReceiver.Play";
    public static final String NEXT = "com.example.asus.syoucloud.NotificationReceiver.Next";
    public static final String LAST = "com.example.asus.syoucloud.NotificationReceiver.Last";
    public static final String CANCEL = "com.example.asus.syoucloud.NotificationReceiver.Cancel";
    public static final String LYRIC = "com.example.asus.syoucloud.NotificationReceiver.LyricItem";
    public static final String UNLOCK = "com.example.asus.syoucloud.NotificationReceiver.Unlock";
    public static final String BACKGROUND = "com.example.asus.syoucloud.NotificationReceiver.Background";
    public static final String FOREGROUND = "com.example.asus.syoucloud.NotificationReceiver.Foreground";
    public static final String TIME_OUT = "com.example.asus.syoucloud.NotificationReceiver.Timeout";
    public static final String HEADSET = "android.intent.action.HEADSET_PLUG";

    public static final String DATABASE_NAME = "greendaodemo.db";

    public static final String SEARCH_MUSIC = " https://api.bzqll.com/music/netease/search?key=579621905&s=";
    private static final String target = Environment.getExternalStoragePublicDirectory(
            Environment.DIRECTORY_DOWNLOADS).toString();
    public static final String musicTarget = target + "/music/";
    public static final String lyricTarget = target + "/lyric/";
    public static final String bmpTarget = target + "/bitmap/";
}
