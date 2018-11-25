package com.example.asus.syoucloud.util;

import android.util.Log;

import com.example.asus.syoucloud.musicManager.Lyric;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class LrcHandle {

    private static final String TAG = "LrcHandle";
    private List<Lyric> lyricList = new ArrayList<>();

    public void readLrcFromFile(String path) {
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
            Log.i(TAG, "readLrcFromFile: 没有歌词文件");
        } catch (IOException e) {
            Log.i(TAG, "readLrcFromFile: 歌词文件读取错误");
        }
        Collections.sort(lyricList);
    }

    public void readLrcFromInternet(String path, DownloadCallback listener) {
        new Thread(() -> {
            HttpURLConnection connection = null;
            try {
                URL url = new URL(path);
                connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");
                connection.setConnectTimeout(4000);
                connection.setReadTimeout(4000);
                connection.setDoInput(true);
                InputStream in = connection.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                String line;
                while ((line = reader.readLine()) != null) {
                    if (line.contains("[ti:") || line.contains("[ar:") ||
                            line.contains("[al:") || line.contains("[by:"))
                        continue;
                    String lyric = line, translate = null;
                    while (lyric.contains("]")) {
                        String ss = lyric.substring(lyric.indexOf("["), lyric.indexOf("]") + 1);
                        lyric = lyric.replace(ss, "");
                    }
                    if (lyric.contains("/")) {
                        translate = lyric.substring(lyric.indexOf("/") + 1).replace(" ", "");
                        lyric = lyric.substring(0, lyric.indexOf("/")).replace(" ", "");
                    }
                    addTime(line, lyric, translate);
                }
                if (listener != null) listener.onFinish();
            } catch (Exception e) {
                e.printStackTrace();
                if (listener != null) listener.onError();
            } finally {
                if (connection != null) connection.disconnect();
            }
        }).start();
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

    public interface DownloadCallback {
        void onFinish();

        void onError();
    }
}