package com.example.greendaodemo.db;

import android.database.Cursor;
import android.database.sqlite.SQLiteStatement;

import org.greenrobot.greendao.AbstractDao;
import org.greenrobot.greendao.Property;
import org.greenrobot.greendao.internal.DaoConfig;
import org.greenrobot.greendao.database.Database;
import org.greenrobot.greendao.database.DatabaseStatement;

import com.example.asus.syoucloud.bean.MusicAndMixJoin;

// THIS CODE IS GENERATED BY greenDAO, DO NOT EDIT.
/** 
 * DAO for table "MUSIC_AND_MIX_JOIN".
*/
public class MusicAndMixJoinDao extends AbstractDao<MusicAndMixJoin, Long> {

    public static final String TABLENAME = "MUSIC_AND_MIX_JOIN";

    /**
     * Properties of entity MusicAndMixJoin.<br/>
     * Can be used for QueryBuilder and for referencing column names.
     */
    public static class Properties {
        public final static Property Id = new Property(0, long.class, "id", true, "_id");
        public final static Property MixItemId = new Property(1, long.class, "mixItemId", false, "MIX_ITEM_ID");
        public final static Property MusicInfoId = new Property(2, long.class, "musicInfoId", false, "MUSIC_INFO_ID");
    }


    public MusicAndMixJoinDao(DaoConfig config) {
        super(config);
    }
    
    public MusicAndMixJoinDao(DaoConfig config, DaoSession daoSession) {
        super(config, daoSession);
    }

    /** Creates the underlying database table. */
    public static void createTable(Database db, boolean ifNotExists) {
        String constraint = ifNotExists? "IF NOT EXISTS ": "";
        db.execSQL("CREATE TABLE " + constraint + "\"MUSIC_AND_MIX_JOIN\" (" + //
                "\"_id\" INTEGER PRIMARY KEY NOT NULL ," + // 0: id
                "\"MIX_ITEM_ID\" INTEGER NOT NULL ," + // 1: mixItemId
                "\"MUSIC_INFO_ID\" INTEGER NOT NULL );"); // 2: musicInfoId
    }

    /** Drops the underlying database table. */
    public static void dropTable(Database db, boolean ifExists) {
        String sql = "DROP TABLE " + (ifExists ? "IF EXISTS " : "") + "\"MUSIC_AND_MIX_JOIN\"";
        db.execSQL(sql);
    }

    @Override
    protected final void bindValues(DatabaseStatement stmt, MusicAndMixJoin entity) {
        stmt.clearBindings();
        stmt.bindLong(1, entity.getId());
        stmt.bindLong(2, entity.getMixItemId());
        stmt.bindLong(3, entity.getMusicInfoId());
    }

    @Override
    protected final void bindValues(SQLiteStatement stmt, MusicAndMixJoin entity) {
        stmt.clearBindings();
        stmt.bindLong(1, entity.getId());
        stmt.bindLong(2, entity.getMixItemId());
        stmt.bindLong(3, entity.getMusicInfoId());
    }

    @Override
    public Long readKey(Cursor cursor, int offset) {
        return cursor.getLong(offset + 0);
    }    

    @Override
    public MusicAndMixJoin readEntity(Cursor cursor, int offset) {
        MusicAndMixJoin entity = new MusicAndMixJoin( //
            cursor.getLong(offset + 0), // id
            cursor.getLong(offset + 1), // mixItemId
            cursor.getLong(offset + 2) // musicInfoId
        );
        return entity;
    }
     
    @Override
    public void readEntity(Cursor cursor, MusicAndMixJoin entity, int offset) {
        entity.setId(cursor.getLong(offset + 0));
        entity.setMixItemId(cursor.getLong(offset + 1));
        entity.setMusicInfoId(cursor.getLong(offset + 2));
     }
    
    @Override
    protected final Long updateKeyAfterInsert(MusicAndMixJoin entity, long rowId) {
        entity.setId(rowId);
        return rowId;
    }
    
    @Override
    public Long getKey(MusicAndMixJoin entity) {
        if(entity != null) {
            return entity.getId();
        } else {
            return null;
        }
    }

    @Override
    public boolean hasKey(MusicAndMixJoin entity) {
        throw new UnsupportedOperationException("Unsupported for entities with a non-null key");
    }

    @Override
    protected final boolean isEntityUpdateable() {
        return true;
    }
    
}
