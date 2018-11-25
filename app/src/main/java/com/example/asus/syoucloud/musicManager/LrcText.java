package com.example.asus.syoucloud.musicManager;

import org.litepal.crud.LitePalSupport;

import java.io.Serializable;
import java.util.List;

public class LrcText extends LitePalSupport implements Serializable{
    Long songId;
    List<Lyric> lyricList;

    public Long getSongId() {
        return songId;
    }

    public void setSongId(String title) {
        this.songId = songId;
    }

    public List<Lyric> getLyricList() {
        return lyricList;
    }

    public void setLyricList(List<Lyric> lyricList) {
        this.lyricList = lyricList;
    }
}
