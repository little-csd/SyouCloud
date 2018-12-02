package com.example.asus.syoucloud.bean;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.JoinEntity;
import org.greenrobot.greendao.annotation.ToMany;
import org.greenrobot.greendao.annotation.Unique;

import java.util.List;
import org.greenrobot.greendao.DaoException;
import com.example.greendaodemo.db.DaoSession;
import com.example.greendaodemo.db.MixItemDao;
import com.example.greendaodemo.db.MusicInfoDao;

@Entity
public class MusicInfo {
    @Id
    private long id;
    private long size;
    private int duration;
    private int albumId;
    private String title;
    private String album;
    private String artist;
    private String url;
    @Generated(hash = 961085388)
    public MusicInfo(long id, long size, int duration, int albumId, String title,
            String album, String artist, String url) {
        this.id = id;
        this.size = size;
        this.duration = duration;
        this.albumId = albumId;
        this.title = title;
        this.album = album;
        this.artist = artist;
        this.url = url;
    }
    @Generated(hash = 1735505054)
    public MusicInfo() {
    }
    public long getId() {
        return this.id;
    }
    public void setId(long id) {
        this.id = id;
    }
    public long getSize() {
        return this.size;
    }
    public void setSize(long size) {
        this.size = size;
    }
    public int getDuration() {
        return this.duration;
    }
    public void setDuration(int duration) {
        this.duration = duration;
    }
    public int getAlbumId() {
        return this.albumId;
    }
    public void setAlbumId(int albumId) {
        this.albumId = albumId;
    }
    public String getTitle() {
        return this.title;
    }
    public void setTitle(String title) {
        this.title = title;
    }
    public String getAlbum() {
        return this.album;
    }
    public void setAlbum(String album) {
        this.album = album;
    }
    public String getArtist() {
        return this.artist;
    }
    public void setArtist(String artist) {
        this.artist = artist;
    }
    public String getUrl() {
        return this.url;
    }
    public void setUrl(String url) {
        this.url = url;
    }

}
