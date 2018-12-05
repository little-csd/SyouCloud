package com.example.Gson;

import com.example.Gson.Lyric.LyricInfo;
import com.example.Gson.Lyric.LyricResult;
import com.example.Gson.Music.MusicResult;
import com.example.Gson.Music.MusicResultItem;
import com.example.asus.syoucloud.bean.LyricItem;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ParseHelper {

    private static List<LyricItem> lyric;

    public static String parseLyricInfo(String data) {
        List<LyricResult> list = new Gson().fromJson(data, LyricInfo.class).getResult();
        if (list == null || list.isEmpty()) return "";
        else return list.get(0).getLrc();
    }

    public static List<MusicResultItem> parseMusicList(String data) {
        List<MusicResultItem> list = new Gson().fromJson(data, MusicResult.class).getData();
        if (list == null) list = new ArrayList<>();
        return list;
    }

    public static List<LyricItem> parseLyric(String data) {
        lyric = new ArrayList<>();
        String[] list = data.split("\r?\n");
        for (String mList : list) {
            if (mList.contains("ti:") || mList.contains("ar:") || mList.contains("al:") ||
                    mList.contains("by:") || mList.contains("Offset") || !mList.contains("]"))
                continue;
            String text = mList, translate = null;
            text = text.replace("〖", "[").replace("〗", "]");
            while (text.contains("]")) {
                String ss = text.substring(text.indexOf("["), text.indexOf("]") + 1);
                text = text.replace(ss, "");
            }
            if (text.contains("/")) {
                translate = text.substring(text.indexOf("/") + 1);
                text = text.substring(0, text.indexOf("/"));
            } else if (text.contains("\\")) {
                translate = text.substring(text.indexOf("\\") + 1);
                text = text.substring(0, text.indexOf("\\"));
            }
            if (text.equals("")) continue;
            addTime(mList, text, translate);
        }
        Collections.sort(lyric);
        return lyric;
    }

    private static void timeRead(String s, String text, String translate) {
        s = s.replace(".", ":");
        String time[] = s.split(":");
        int minute, second, millSecond;
        if (time.length > 0) minute = Integer.parseInt(time[0]);
        else minute = 0;
        if (time.length > 1) second = Integer.parseInt(time[1]);
        else second = 0;
        if (time.length > 2) millSecond = Integer.parseInt(time[2]);
        else millSecond = 0;
        if (millSecond < 100) millSecond *= 10;
        int all = millSecond + second * 1000 + minute * 1000 * 60;
        if (all < 0) return;
        lyric.add(new LyricItem(all, text, translate));
    }

    private static void addTime(String s, String text, String translate) {
        while (s.contains("]")) {
            String ss = s.substring(s.indexOf("[") + 1, s.indexOf("]"));
            if (s.indexOf("]") + 1 < s.length())
                s = s.substring(s.indexOf("]") + 1, s.length());
            else s = "";
            timeRead(ss, text, translate);
        }
    }
}