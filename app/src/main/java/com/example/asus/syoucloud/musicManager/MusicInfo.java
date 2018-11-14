package com.example.asus.syoucloud.musicManager;

import android.graphics.Bitmap;

public class MusicInfo {
    private long id;
    private long size;
    private int duration;
    private String title;
    private String album;
    private String artist;
    private String url;
    private Bitmap bitmap;

    MusicInfo(long id, long size, int duration, String title, String album, String artist,
              String url, Bitmap bitmap) {
        this.id = id;
        this.size = size;
        this.duration = duration;
        this.title = title;
        this.album = album;
        this.artist = artist;
        this.url = url;
        this.bitmap = bitmap;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public int getDuration() {
        return duration;
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAlbum() {
        return album;
    }

    public String getArtist() {
        return artist;
    }

    public String getUrl() {
        return url;
    }
}
