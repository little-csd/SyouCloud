package com.example.asus.syoucloud.bean;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;

@Entity
public class MusicAndMixJoin {
    @Id
    private long id;

    private long mixItemId;
    private long musicInfoId;

    @Generated(hash = 335188906)
    public MusicAndMixJoin(long id, long mixItemId, long musicInfoId) {
        this.id = id;
        this.mixItemId = mixItemId;
        this.musicInfoId = musicInfoId;
    }

    @Generated(hash = 837299077)
    public MusicAndMixJoin() {
    }

    public long getId() {
        return this.id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getMixItemId() {
        return this.mixItemId;
    }

    public void setMixItemId(long mixItemId) {
        this.mixItemId = mixItemId;
    }

    public long getMusicInfoId() {
        return this.musicInfoId;
    }

    public void setMusicInfoId(long musicInfoId) {
        this.musicInfoId = musicInfoId;
    }

}
