package com.example.greendaodemo.db;

import java.util.List;
import android.database.Cursor;
import android.database.sqlite.SQLiteStatement;

import org.greenrobot.greendao.AbstractDao;
import org.greenrobot.greendao.Property;
import org.greenrobot.greendao.internal.DaoConfig;
import org.greenrobot.greendao.database.Database;
import org.greenrobot.greendao.database.DatabaseStatement;
import org.greenrobot.greendao.query.Query;
import org.greenrobot.greendao.query.QueryBuilder;

import com.example.asus.syoucloud.bean.MusicAndMixJoin;

import com.example.asus.syoucloud.bean.MusicInfo;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.
/** 
 * DAO for table "MUSIC_INFO".
*/
public class MusicInfoDao extends AbstractDao<MusicInfo, Long> {

    public static final String TABLENAME = "MUSIC_INFO";

    /**
     * Properties of entity MusicInfo.<br/>
     * Can be used for QueryBuilder and for referencing column names.
     */
    public static class Properties {
        public final static Property Id = new Property(0, long.class, "id", true, "_id");
        public final static Property Size = new Property(1, long.class, "size", false, "SIZE");
        public final static Property Duration = new Property(2, int.class, "duration", false, "DURATION");
        public final static Property AlbumId = new Property(3, int.class, "albumId", false, "ALBUM_ID");
        public final static Property Title = new Property(4, String.class, "title", false, "TITLE");
        public final static Property Album = new Property(5, String.class, "album", false, "ALBUM");
        public final static Property Artist = new Property(6, String.class, "artist", false, "ARTIST");
        public final static Property Url = new Property(7, String.class, "url", false, "URL");
    }

    private Query<MusicInfo> mixItem_MusicListQuery;

    public MusicInfoDao(DaoConfig config) {
        super(config);
    }
    
    public MusicInfoDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
    }

    /** Creates the underlying database table. */
    public static void createTable(Database db, boolean ifNotExists) {
        String constraint = ifNotExists? "IF NOT EXISTS ": "";
        db.execSQL("CREATE TABLE " + constraint + "\"MUSIC_INFO\" (" + //
                "\"_id\" INTEGER PRIMARY KEY NOT NULL ," + // 0: id
                "\"SIZE\" INTEGER NOT NULL ," + // 1: size
                "\"DURATION\" INTEGER NOT NULL ," + // 2: duration
                "\"ALBUM_ID\" INTEGER NOT NULL ," + // 3: albumId
                "\"TITLE\" TEXT," + // 4: title
                "\"ALBUM\" TEXT," + // 5: album
                "\"ARTIST\" TEXT," + // 6: artist
                "\"URL\" TEXT);"); // 7: url
    }

    /** Drops the underlying database table. */
    public static void dropTable(Database db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "\"MUSIC_INFO\"";
        db.execSQL(sql);
    }

    @Override
    protected final void bindValues(DatabaseStatement stmt, MusicInfo entity) {
        stmt.clearBindings();
        stmt.bindLong(1, entity.getId());
        stmt.bindLong(2, entity.getSize());
        stmt.bindLong(3, entity.getDuration());
        stmt.bindLong(4, entity.getAlbumId());
 
        String title = entity.getTitle();
        if (title != null) {
            stmt.bindString(5, title);
        }
 
        String album = entity.getAlbum();
        if (album != null) {
            stmt.bindString(6, album);
        }
 
        String artist = entity.getArtist();
        if (artist != null) {
            stmt.bindString(7, artist);
        }
 
        String url = entity.getUrl();
        if (url != null) {
            stmt.bindString(8, url);
        }
    }

    @Override
    protected final void bindValues(SQLiteStatement stmt, MusicInfo entity) {
        stmt.clearBindings();
        stmt.bindLong(1, entity.getId());
        stmt.bindLong(2, entity.getSize());
        stmt.bindLong(3, entity.getDuration());
        stmt.bindLong(4, entity.getAlbumId());
 
        String title = entity.getTitle();
        if (title != null) {
            stmt.bindString(5, title);
        }
 
        String album = entity.getAlbum();
        if (album != null) {
            stmt.bindString(6, album);
        }
 
        String artist = entity.getArtist();
        if (artist != null) {
            stmt.bindString(7, artist);
        }
 
        String url = entity.getUrl();
        if (url != null) {
            stmt.bindString(8, url);
        }
    }

    @Override
    public Long readKey(Cursor cursor, int offset) {
        return cursor.getLong(offset + 0);
    }    

    @Override
    public MusicInfo readEntity(Cursor cursor, int offset) {
        MusicInfo entity = new MusicInfo( //
            cursor.getLong(offset + 0), // id
            cursor.getLong(offset + 1), // size
            cursor.getInt(offset + 2), // duration
            cursor.getInt(offset + 3), // albumId
            cursor.isNull(offset + 4) ? null : cursor.getString(offset + 4), // title
            cursor.isNull(offset + 5) ? null : cursor.getString(offset + 5), // album
            cursor.isNull(offset + 6) ? null : cursor.getString(offset + 6), // artist
            cursor.isNull(offset + 7) ? null : cursor.getString(offset + 7) // url
        );
        return entity;
    }
     
    @Override
    public void readEntity(Cursor cursor, MusicInfo entity, int offset) {
        entity.setId(cursor.getLong(offset + 0));
        entity.setSize(cursor.getLong(offset + 1));
        entity.setDuration(cursor.getInt(offset + 2));
        entity.setAlbumId(cursor.getInt(offset + 3));
        entity.setTitle(cursor.isNull(offset + 4) ? null : cursor.getString(offset + 4));
        entity.setAlbum(cursor.isNull(offset + 5) ? null : cursor.getString(offset + 5));
        entity.setArtist(cursor.isNull(offset + 6) ? null : cursor.getString(offset + 6));
        entity.setUrl(cursor.isNull(offset + 7) ? null : cursor.getString(offset + 7));
     }
    
    @Override
    protected final Long updateKeyAfterInsert(MusicInfo entity, long rowId) {
        entity.setId(rowId);
        return rowId;
    }
    
    @Override
    public Long getKey(MusicInfo entity) {
        if(entity != null) {
            return entity.getId();
        } else {
            return null;
        }
    }

    @Override
    public boolean hasKey(MusicInfo entity) {
        throw new UnsupportedOperationException("Unsupported for entities with a non-null key");
    }

    @Override
    protected final boolean isEntityUpdateable() {
        return true;
    }
    
    /** Internal query to resolve the "musicList" to-many relationship of MixItem. */
    public List<MusicInfo> _queryMixItem_MusicList(long mixItemId) {
        synchronized (this) {
            if (mixItem_MusicListQuery == null) {
                QueryBuilder<MusicInfo> queryBuilder = queryBuilder();
                queryBuilder.join(MusicAndMixJoin.class, MusicAndMixJoinDao.Properties.MusicInfoId)
                    .where(MusicAndMixJoinDao.Properties.MixItemId.eq(mixItemId));
                mixItem_MusicListQuery = queryBuilder.build();
            }
        }
        Query<MusicInfo> query = mixItem_MusicListQuery.forCurrentThread();
        query.setParameter(0, mixItemId);
        return query.list();
    }

}
