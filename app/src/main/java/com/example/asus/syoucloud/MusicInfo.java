package com.example.asus.syoucloud;

public class MusicInfo {
    private long id;
    private long size;
    private int duration;
    private String title;
    private String album;
    private String artist;
    private String url;

    public MusicInfo(long id, long size, int duration, String title, String album, String artist, String url) {
        this.id = id;
        this.size = size;
        this.duration = duration;
        this.title = title;
        this.album = album;
        this.artist = artist;
        this.url = url;
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

    public void setDuration(int duration) {
        this.duration = duration;
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

    public void setAlbum(String album) {
        this.album = album;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
