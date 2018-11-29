package com.example.asus.syoucloud.util;

import android.util.Log;

import com.example.asus.syoucloud.bean.Lyric;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class LrcHandle {

    private static final String TAG = "LrcHandle";
    private List<Lyric> lyricList = new ArrayList<>();

    private static LrcHandle lrcHandle;

    private LrcHandle(){

    }

    public static LrcHandle getInstance() {
        if (lrcHandle == null) lrcHandle = new LrcHandle();
        return lrcHandle;
    }

    public void readLRC(String path) {
        lyricList.clear();
        File file = new File(path);
        try {
            FileInputStream inputStream = new FileInputStream(file);
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "UTF-8");
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            String s;
            while ((s = bufferedReader.readLine()) != null) {
                if (s.contains("[ti:") || s.contains("[ar:") || s.contains("[al:") || s.contains("[by:"))
                    continue;
                String lyric = s, translate = null;
                while (lyric.contains("]")) {
                    String ss = lyric.substring(lyric.indexOf("["), lyric.indexOf("]") + 1);
                    lyric = lyric.replace(ss, "");
                }
                if (lyric.contains("/")) {
                    translate = lyric.substring(lyric.indexOf("/") + 1).replace(" ", "");
                    lyric = lyric.substring(0, lyric.indexOf("/")).replace(" ", "");
                }
                addTime(s, lyric, translate);
            }
            bufferedReader.close();
            inputStreamReader.close();
            inputStream.close();
        } catch (FileNotFoundException e) {
            Log.i(TAG, "readLRC: 没有歌词文件");
        } catch (IOException e) {
            Log.i(TAG, "readLRC: 歌词文件读取错误");
        }
        Collections.sort(lyricList);
    }

    public List<Lyric> getLyricList() {
        return lyricList;
    }

    private void timeRead(String s, String lyric, String translate) {
        s = s.replace(".", ":");
        String time[] = s.split(":");
        int minute = Integer.parseInt(time[0]);
        int second = Integer.parseInt(time[1]);
        int millSecond = Integer.parseInt(time[2]);
        int all = millSecond * 10 + second * 1000 + minute * 1000 * 60;
        lyricList.add(new Lyric(all, lyric, translate));
    }

    private void addTime(String s, String lyric, String translate) {
        while (s.contains("]")) {
            String ss = s.substring(1, 9);
            if (s.length() > 10) s = s.substring(10, s.length());
            else s = "";
            timeRead(ss, lyric, translate);
        }
    }
}