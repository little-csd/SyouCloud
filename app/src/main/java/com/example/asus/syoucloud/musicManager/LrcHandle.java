package com.example.asus.syoucloud.musicManager;

import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LrcHandle {
    private static final String TAG = "LrcHandle";
    List<String> wordList = new ArrayList<>();
    List<Integer> timeList = new ArrayList<>();

    public void readLRC(String path) {
        File file = new File(path);
        try {
            FileInputStream inputStream = new FileInputStream(file);
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "UTF-8");
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            String s = "";
            while ((s = bufferedReader.readLine()) != null) {
                addTime(s);
                if (s.contains("[ar:") || s.contains("[ti:") || s.contains("[by:"))
                    s = s.substring(s.indexOf(":") + 1, s.indexOf("]"));
                else {
                    String ss = s.substring(s.indexOf("["), s.indexOf("]") + 1);
                    s = s.replace(ss, "");
                }
                wordList.add(s);
            }
            bufferedReader.close();
            inputStreamReader.close();
            inputStream.close();
        } catch (FileNotFoundException e) {
            wordList.add("没有歌词文件");
            Log.i(TAG, "readLRC: 没有歌词文件");
        } catch (IOException e) {
            wordList.add("歌词文件读取错误");
            Log.i(TAG, "readLRC: 歌词文件读取错误");
        }
    }

    public List<String> getWordList() {
        return wordList;
    }

    public List<Integer> getTimeList() {
        return timeList;
    }

    private void timeRead(String s) {
        s = s.replace(".", ":");
        String time[] = s.split(":");
        int minute = Integer.parseInt(time[0]);
        int second = Integer.parseInt(time[1]);
        int millSecond = Integer.parseInt(time[2]);
        timeList.add(millSecond * 10 + second * 1000 + minute * 1000 * 60);
    }

    private void addTime(String s) {
        Matcher matcher = Pattern.compile(
                "\\[\\d{1,2}:\\d{1,2}([.:]\\d{1,2})?]").matcher(s);
        if (matcher.find()) {
            String str = matcher.group();
            timeRead(str.substring(1, str.length() - 1));
        }
    }
}
