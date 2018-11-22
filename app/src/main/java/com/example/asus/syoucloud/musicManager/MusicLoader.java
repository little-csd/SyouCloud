package com.example.asus.syoucloud.musicManager;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.ImageView;

import com.example.asus.syoucloud.R;

import java.util.ArrayList;
import java.util.List;

import static com.example.asus.syoucloud.util.Constant.UPDATE;

public class MusicLoader {
    private static final String TAG = "MusicLoader";
    private static MusicLoader musicLoader;
    private static ContentResolver contentResolver;
    private static Bitmap defaultBmp;
    private final Uri contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
    private final String sortOrder = MediaStore.Audio.Media.DATA;
    private final String where = "mime_type in ('audio/mpeg','audio/x-ms-wma') " +
            "and _display_name <> 'audio' and is_music > 0 ";
    private List<MusicInfo> musicList;
    private String[] projection = {
            MediaStore.Audio.Media._ID,
            MediaStore.Audio.Media.TITLE,
            MediaStore.Audio.Media.DATA,
            MediaStore.Audio.Media.ALBUM,
            MediaStore.Audio.Media.ARTIST,
            MediaStore.Audio.Media.DURATION,
            MediaStore.Audio.Media.SIZE
    };

    private MusicLoader() {
        musicList = new ArrayList<>();
        reSearch();
    }

    public static MusicLoader getInstance(ContentResolver mContentResolver, Context context) {
        if (musicLoader == null) {
            contentResolver = mContentResolver;
            musicLoader = new MusicLoader();
            defaultBmp = BitmapFactory.decodeResource(context.getResources(), R.drawable.default_bitmap);
        }
        return musicLoader;
    }

    // todo remember to change the size
    public static void setBitmap(Context context, @NonNull ImageView igv, @NonNull MusicInfo music) {
        new Thread(() -> {
            if (music.getBitmap() == null) {
                Bitmap bitmap;
                String url = music.getUrl();
                Uri selectedAudio = Uri.parse(url);
                MediaMetadataRetriever retriever = new MediaMetadataRetriever();
                retriever.setDataSource(context, selectedAudio);
                byte[] artwork = retriever.getEmbeddedPicture();

                if (artwork != null)
                    bitmap = BitmapFactory.decodeByteArray(artwork, 0, artwork.length);
                else bitmap = defaultBmp;
                music.setBitmap(bitmap);
            }
            Handler handler = new Handler(Looper.getMainLooper());
            handler.post(() -> igv.setImageBitmap(music.getBitmap()));

            Intent intent = new Intent(UPDATE);
            context.sendBroadcast(intent);
        }).start();
    }

    private void reSearch() {
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
                MusicInfo musicInfo = new MusicInfo(id, size, duration, title, album, artist, url);
                musicList.add(musicInfo);
            } while (cursor.moveToNext());
            cursor.close();
        }
    }

    public List<MusicInfo> getMusicList() {
        return musicList;
    }
}