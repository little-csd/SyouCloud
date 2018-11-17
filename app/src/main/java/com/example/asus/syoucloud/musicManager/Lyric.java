package com.example.asus.syoucloud.musicManager;

import android.support.annotation.NonNull;

public class Lyric implements Comparable<Lyric> {
    private int time;
    private String text;
    private String translate;

    public Lyric(int time, String text, String translate) {
        this.time = time;
        this.text = text;
        this.translate = translate;
    }

    public int getTime() {
        return time;
    }

    public String getText() {
        return text;
    }

    public String getTranslate() {
        return translate;
    }

    @Override
    public int compareTo(@NonNull Lyric o) {
        return Integer.compare(time, o.time);
    }
}