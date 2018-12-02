package com.example.asus.syoucloud.bean;

import com.example.greendaodemo.db.DaoSession;
import com.example.greendaodemo.db.MixItemDao;
import com.example.greendaodemo.db.MusicInfoDao;

import org.greenrobot.greendao.DaoException;
import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.JoinEntity;
import org.greenrobot.greendao.annotation.ToMany;

import java.util.List;

@Entity
public class MixItem {
    @Id
    private long id;
    private String title;
    private String password;

    @ToMany
    @JoinEntity(
            entity = MusicAndMixJoin.class,
            sourceProperty = "mixItemId",
            targetProperty = "musicInfoId")
    private List<MusicInfo> musicList;
    /** Used to resolve relations */
    @Generated(hash = 2040040024)
    private transient DaoSession daoSession;
    /** Used for active entity operations. */
    @Generated(hash = 243521215)
    private transient MixItemDao myDao;

    @Generated(hash = 1824543508)
    public MixItem(long id, String title, String password) {
        this.id = id;
        this.title = title;
        this.password = password;
    }

    @Generated(hash = 1951954627)
    public MixItem() {
    }

    public long getId() {
        return this.id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getTitle() {
        return this.title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPassword() {
        return this.password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * To-many relationship, resolved on first access (and after reset).
     * Changes to to-many relations are not persisted, make changes to the target entity.
     */
    @Generated(hash = 1748314921)
    public List<MusicInfo> getMusicList() {
        if (musicList == null) {
            final DaoSession daoSession = this.daoSession;
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            MusicInfoDao targetDao = daoSession.getMusicInfoDao();
            List<MusicInfo> musicListNew = targetDao._queryMixItem_MusicList(id);
            synchronized (this) {
                if (musicList == null) {
                    musicList = musicListNew;
                }
            }
        }
        return musicList;
    }

    /** Resets a to-many relationship, making the next get call to query for a fresh result. */
    @Generated(hash = 1964759424)
    public synchronized void resetMusicList() {
        musicList = null;
    }

    /**
     * Convenient call for {@link org.greenrobot.greendao.AbstractDao#delete(Object)}.
     * Entity must attached to an entity context.
     */
    @Generated(hash = 128553479)
    public void delete() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }
        myDao.delete(this);
    }

    /**
     * Convenient call for {@link org.greenrobot.greendao.AbstractDao#refresh(Object)}.
     * Entity must attached to an entity context.
     */
    @Generated(hash = 1942392019)
    public void refresh() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }
        myDao.refresh(this);
    }

    /**
     * Convenient call for {@link org.greenrobot.greendao.AbstractDao#update(Object)}.
     * Entity must attached to an entity context.
     */
    @Generated(hash = 713229351)
    public void update() {
        if (myDao == null) {
            throw new DaoException("Entity is detached from DAO context");
        }
        myDao.update(this);
    }

    /** called by internal mechanisms, do not call yourself. */
    @Generated(hash = 2141764085)
    public void __setDaoSession(DaoSession daoSession) {
        this.daoSession = daoSession;
        myDao = daoSession != null ? daoSession.getMixItemDao() : null;
    }

}