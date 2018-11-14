package com.example.asus.syoucloud.musicManager;

import android.content.ContentResolver;
import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.ImageView;

import com.example.asus.syoucloud.R;

import java.io.FileDescriptor;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MusicLoader {
    private static final String TAG = "MusicLoader";

    private Context context;
    private List<MusicInfo> musicList;
    private static MusicLoader musicLoader;
    private static ContentResolver contentResolver;

    private final Uri contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
    private final String sortOrder = MediaStore.Audio.Media.DATA;
    private final String where =  "mime_type in ('audio/mpeg','audio/x-ms-wma') " +
            "and _display_name <> 'audio' and is_music > 0 " ;
    private String[] projection = {
            MediaStore.Audio.Media._ID,
            MediaStore.Audio.Media.TITLE,
            MediaStore.Audio.Media.DATA,
            MediaStore.Audio.Media.ALBUM,
            MediaStore.Audio.Media.ARTIST,
            MediaStore.Audio.Media.DURATION,
            MediaStore.Audio.Media.SIZE
    };

    public static MusicLoader getInstance(ContentResolver mContentResolver, Context context) {
        if (musicLoader == null) {
            contentResolver = mContentResolver;
            musicLoader = new MusicLoader(context);
        }
        return musicLoader;
    }

    private MusicLoader(Context context) {
        musicList = new ArrayList<>();
        this.context = context;
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
            int titleCol = cursor.getColumnIndex(MediaStore.Audio.Media.TITLE);
            int urlCol = cursor.getColumnIndex(MediaStore.Audio.Media.DATA);
            int albumCol = cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM);
            int artistCol = cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST);
            int durationCol = cursor.getColumnIndex(MediaStore.Audio.Media.DURATION);
            int sizeCol = cursor.getColumnIndex(MediaStore.Audio.Media.SIZE);
            do {
                long id = cursor.getLong(idCol);
                long size = cursor.getLong(sizeCol);
                int duration = cursor.getInt(durationCol);
                String title = cursor.getString(titleCol);
                String album = cursor.getString(albumCol);
                String artist = cursor.getString(artistCol);
                String url = cursor.getString(urlCol);
                Bitmap bitmap = getBitmap(context, url);
//                MusicInfo musicInfo = new MusicInfo(id, size , duration, title, album, artist, url, bitmap);
//                musicList.add(musicInfo);
            } while(cursor.moveToNext());
            cursor.close();
        }
    }

    public List<MusicInfo> getMusicList() {
        return musicList;
    }

    private Bitmap getBitmap(Context context, String url) {
        Uri selectedAudio = Uri.parse(url);
        MediaMetadataRetriever retriever = new MediaMetadataRetriever();
        if (selectedAudio == null) Log.i(TAG, "getBitmap: null");
        retriever.setDataSource(context, selectedAudio); // the URI of audio file
        return null;
        /*byte[] artwork;

        artwork = retriever.getEmbeddedPicture();
        if (artwork != null)
            return BitmapFactory.decodeByteArray(artwork, 0, artwork.length);
        else
            return BitmapFactory.decodeResource(context.getResources(), R.drawable.play_button);*/
    }
}
