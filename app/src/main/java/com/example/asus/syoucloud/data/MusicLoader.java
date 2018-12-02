package com.example.asus.syoucloud.data;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;

import com.example.asus.syoucloud.bean.MusicInfo;

import java.util.List;

public class MusicLoader {

    private static final String TAG = "MusicLoader";

    private static ContentResolver contentResolver;
    private final Uri contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
    private List<MusicInfo> musicList;
    private String[] projection = {
            MediaStore.Audio.Media._ID,
            MediaStore.Audio.Media.TITLE,
            MediaStore.Audio.Media.DATA,
            MediaStore.Audio.Media.ALBUM,
            MediaStore.Audio.Media.ARTIST,
            MediaStore.Audio.Media.DURATION,
            MediaStore.Audio.Media.SIZE,
            MediaStore.Audio.Media.ALBUM_ID
    };

    MusicLoader(List<MusicInfo> musicList, Context context) {
        this.musicList = musicList;
        contentResolver = context.getContentResolver();
    }

    public void reSearch() {
        Cursor cursor = contentResolver.query(contentUri, projection, null,
                null, null);
        musicList.clear();
        if (cursor == null) Log.i(TAG, "BitmapHelper: cursor = null");
        else if (!cursor.moveToFirst()) Log.i(TAG, "BitmapHelper: cursor moveToFirst error");
        else {
            DatabaseManager databaseManager = DatabaseManager.getInstance();
            int idCol = cursor.getColumnIndex(MediaStore.Audio.Media._ID);
            int titleCol = cursor.getColumnIndex(MediaStore.Audio.Media.TITLE);
            int urlCol = cursor.getColumnIndex(MediaStore.Audio.Media.DATA);
            int albumCol = cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM);
            int artistCol = cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST);
            int durationCol = cursor.getColumnIndex(MediaStore.Audio.Media.DURATION);
            int sizeCol = cursor.getColumnIndex(MediaStore.Audio.Media.SIZE);
            int albumIdCol = cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM_ID);
            do {
                long id = cursor.getLong(idCol);
                long size = cursor.getLong(sizeCol);
                int duration = cursor.getInt(durationCol);
                int albumId = cursor.getInt(albumIdCol);
                String title = cursor.getString(titleCol);
                String album = cursor.getString(albumCol);
                String artist = cursor.getString(artistCol);
                String url = cursor.getString(urlCol);
                MusicInfo musicInfo = new MusicInfo(id, size, duration, albumId, title, album, artist, url);
                databaseManager.addMusic(musicInfo);
            } while (cursor.moveToNext());
            cursor.close();
        }
    }

    public List<MusicInfo> getMusicList() {
        return musicList;
    }

    public void addMusic(MusicInfo music) {
        musicList.add(music);
    }

    public void deleteMusic(long id) {
        for (int i = 0; i < musicList.size(); i++) {
            if (musicList.get(i).getId() == id)
                musicList.remove(i);
        }
    }
}
