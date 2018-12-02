package com.example.asus.syoucloud.bean;

import android.support.annotation.NonNull;

import com.example.asus.syoucloud.util.Constant;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;

@Entity
public class LyricItem implements Comparable<LyricItem> {

    @Id
    private long id;
    private long fromLyric;
    private int time;
    private String text;
    private String translate;
    private float offset = Float.MIN_VALUE;

    public LyricItem(int time, String text, String translate) {
        this.time = time;
        this.text = text;
        this.translate = translate;
    }
    @Generated(hash = 1884852843)
    public LyricItem(long id, long fromLyric, int time, String text,
            String translate, float offset) {
        this.id = id;
        this.fromLyric = fromLyric;
        this.time = time;
        this.text = text;
        this.translate = translate;
        this.offset = offset;
    }

    @Generated(hash = 285902555)
    public LyricItem() {
    }

    public int getHeight() {
        if (translate != null) return Constant.DY;
        else return Constant.DT;
    }

    @Override
    public int compareTo(@NonNull LyricItem o) {
        return time - o.time;
    }

    public long getId() {
        return this.id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getFromLyric() {
        return this.fromLyric;
    }

    public void setFromLyric(long fromLyric) {
        this.fromLyric = fromLyric;
    }

    public int getTime() {
        return this.time;
    }

    public void setTime(int time) {
        this.time = time;
    }

    public String getText() {
        return this.text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getTranslate() {
        return this.translate;
    }

    public void setTranslate(String translate) {
        this.translate = translate;
    }

    public float getOffset() {
        return this.offset;
    }

    public void setOffset(float offset) {
        this.offset = offset;
    }

}