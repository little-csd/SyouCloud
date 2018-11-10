package com.example.asus.syoucloud;

import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class MusicLoader {
    private static final String TAG = "MusicLoader";

    private List<MusicInfo> musicList;
    private static MusicLoader musicLoader;
    private static ContentResolver contentResolver;

    private final Uri contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
    private final String sortOrder = MediaStore.Audio.Media.DATA;
    private final String where =  "mime_type in ('audio/mpeg','audio/x-ms-wma') " +
            "and _display_name <> 'audio' and is_music > 0 " ;
    private String[] projection = {
            MediaStore.Audio.Media._ID,
            MediaStore.Audio.Media.DISPLAY_NAME,
            MediaStore.Audio.Media.DATA,
            MediaStore.Audio.Media.ALBUM,
            MediaStore.Audio.Media.ARTIST,
            MediaStore.Audio.Media.DURATION,
            MediaStore.Audio.Media.SIZE
    };

    public static MusicLoader getInstance(ContentResolver mContentResolver) {
        if (musicLoader == null) {
            contentResolver = mContentResolver;
            musicLoader = new MusicLoader();
        }
        return musicLoader;
    }

    private MusicLoader() {
        musicList = new ArrayList<>();
        reSearch();
    }

    public void reSearch() {
        Cursor cursor = contentResolver.query(contentUri, projection, where,
                null, sortOrder);
        musicList.clear();
        if (cursor == null) Log.i(TAG, "MusicLoader: cursor = null");
        else if (!cursor.moveToFirst()) Log.i(TAG, "MusicLoader: cursor moveToFirst error");
        else {
            int idCol = cursor.getColumnIndex(MediaStore.Audio.Media._ID);
            int displayNameCol = cursor.getColumnIndex(MediaStore.Audio.Media.DISPLAY_NAME);
            int urlCol = cursor.getColumnIndex(MediaStore.Audio.Media.DATA);
            int albumCol = cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM);
            int artistCol = cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST);
            int durationCol = cursor.getColumnIndex(MediaStore.Audio.Media.DURATION);
            int sizeCol = cursor.getColumnIndex(MediaStore.Audio.Media.SIZE);
            do {
                long id = cursor.getLong(idCol);
                long size = cursor.getLong(sizeCol);
                int duration = cursor.getInt(durationCol);
                String title = cursor.getString(displayNameCol);
                String album = cursor.getString(albumCol);
                String artist = cursor.getString(artistCol);
                String url = cursor.getString(urlCol);
                MusicInfo musicInfo = new MusicInfo(id, size , duration, title, album, artist, url);
                musicList.add(musicInfo);
            } while(cursor.moveToNext());
            cursor.close();
        }
    }

    public List<MusicInfo> getMusicList() {
        return musicList;
    }
}
