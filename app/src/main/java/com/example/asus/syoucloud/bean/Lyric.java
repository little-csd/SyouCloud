package com.example.asus.syoucloud.bean;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.OrderBy;
import org.greenrobot.greendao.annotation.ToMany;

import java.util.List;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.DaoException;
import com.example.greendaodemo.db.DaoSession;
import com.example.greendaodemo.db.LyricItemDao;
import com.example.greendaodemo.db.LyricDao;

@Entity
public class Lyric {
    @Id
    private long lyricId;

    @ToMany(referencedJoinProperty = "fromLyric")
    private List<LyricItem> lyricItems;

    /** Used to resolve relations */
    @Generated(hash = 2040040024)
    private transient DaoSession daoSession;

    /** Used for active entity operations. */
    @Generated(hash = 1685673968)
    private transient LyricDao myDao;

    @Generated(hash = 1398310837)
    public Lyric(long lyricId) {
        this.lyricId = lyricId;
    }

    @Generated(hash = 2083827090)
    public Lyric() {
    }

    public long getLyricId() {
        return this.lyricId;
    }

    public void setLyricId(long lyricId) {
        this.lyricId = lyricId;
    }

    /**
     * To-many relationship, resolved on first access (and after reset).
     * Changes to to-many relations are not persisted, make changes to the target entity.
     */
    @Generated(hash = 440176380)
    public List<LyricItem> getLyricItems() {
        if (lyricItems == null) {
            final DaoSession daoSession = this.daoSession;
            if (daoSession == null) {
                throw new DaoException("Entity is detached from DAO context");
            }
            LyricItemDao targetDao = daoSession.getLyricItemDao();
            List<LyricItem> lyricItemsNew = targetDao
                    ._queryLyric_LyricItems(lyricId);
            synchronized (this) {
                if (lyricItems == null) {
                    lyricItems = lyricItemsNew;
                }
            }
        }
        return lyricItems;
    }

    /** Resets a to-many relationship, making the next get call to query for a fresh result. */
    @Generated(hash = 828901999)
    public synchronized void resetLyricItems() {
        lyricItems = null;
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
    @Generated(hash = 1578765116)
    public void __setDaoSession(DaoSession daoSession) {
        this.daoSession = daoSession;
        myDao = daoSession != null ? daoSession.getLyricDao() : null;
    }

}
