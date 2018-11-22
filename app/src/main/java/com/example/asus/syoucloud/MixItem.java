package com.example.asus.syoucloud;

import com.example.asus.syoucloud.musicManager.MusicInfo;

import org.litepal.crud.LitePalSupport;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class MixItem extends LitePalSupport implements Serializable{
    private int albumId;
    private String title;
    private String password;
    private List<MusicInfo> musicList;

    MixItem() {
        musicList = new ArrayList<>();
    }

    public int getAlbumId() {
        return albumId;
    }

    public void setAlbumId(int albumId) {
        this.albumId = albumId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void addMusic(MusicInfo music) {
        musicList.add(music);
    }

    public void deleteMusic(int id) {
        for (int i = 0; i < musicList.size(); i++)
            if (musicList.get(i).getId() == id) musicList.remove(i);
    }

    public List<MusicInfo> getMusicList() {
        return musicList;
    }
}