package com.example.asus.syoucloud.musicManager;

import android.support.annotation.NonNull;

import com.example.asus.syoucloud.util.Constant;

public class Lyric implements Comparable<Lyric> {
    private int time;
    private String text;
    private String translate;
    private float offset = Float.MIN_VALUE;

    Lyric(int time, String text, String translate) {
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

    public float getOffset() {
        return offset;
    }

    public void setOffset(float offset) {
        this.offset = offset;
    }

    public float getHeight() {
        if (translate != null) return Constant.DY;
        else return Constant.DT;
    }

    @Override
    public int compareTo(@NonNull Lyric o) {
        return Integer.compare(time, o.time);
    }
}