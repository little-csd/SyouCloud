package com.example.asus.syoucloud.musicManager;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.ImageView;

import com.example.asus.syoucloud.R;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class MusicLoader {

    private static final String TAG = "MusicLoader";
    private static final Uri sArtworkUri = Uri.parse("content://media/external/audio/albumart");

    private static MusicLoader musicLoader;
    private static ContentResolver contentResolver;
    private static float scale;
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

    private MusicLoader() {
        musicList = new ArrayList<>();
        reSearch();
    }

    public static MusicLoader getInstance(ContentResolver mContentResolver, Context context) {
        if (musicLoader == null) {
            contentResolver = mContentResolver;
            musicLoader = new MusicLoader();
            scale = context.getResources().getDisplayMetrics().density;
        }
        return musicLoader;
    }

    public static void setBitmap(@NonNull ImageView igv, int albumId) {
        if (igv.getHeight() == 0) {
            igv.post(() -> setBitmap(igv, albumId));
            return;
        }

        new Thread(() -> {
            Uri uri = ContentUris.withAppendedId(sArtworkUri, albumId);
            BitmapFactory.Options opts = new BitmapFactory.Options();
            opts.inJustDecodeBounds = true;
            try {
                InputStream in = contentResolver.openInputStream(uri);
                BitmapFactory.decodeStream(in, null, opts);
                opts.inSampleSize = calculateSampleSize(opts, igv.getWidth(), igv.getHeight());
                opts.inJustDecodeBounds = false;
                in = contentResolver.openInputStream(uri);
                Bitmap artwork = BitmapFactory.decodeStream(in, null, opts);
                Handler handler = new Handler(Looper.getMainLooper());
                handler.post(() -> igv.setImageBitmap(artwork));
            } catch (FileNotFoundException e) {
                Log.i(TAG, "setBitmap: 不存在该文件");
            }
        }).start();
    }

    public static Bitmap getBitmap(Context context, int albumId, int width, int height) {
        width = (int) (width * scale);
        height = (int) (height * scale);
        Uri uri = ContentUris.withAppendedId(sArtworkUri, albumId);
        BitmapFactory.Options opts = new BitmapFactory.Options();
        opts.inJustDecodeBounds = true;
        try {
            InputStream in = contentResolver.openInputStream(uri);
            BitmapFactory.decodeStream(in, null, opts);
            opts.inSampleSize = calculateSampleSize(opts, width, height);
            opts.inJustDecodeBounds = false;
            in = contentResolver.openInputStream(uri);
            return BitmapFactory.decodeStream(in, null, opts);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return getDefaultBitmap(context, width, height);
        }
    }

    private static Bitmap getDefaultBitmap(Context context, int width, int height) {
        BitmapFactory.Options opts = new BitmapFactory.Options();
        opts.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(context.getResources(), R.drawable.default_bitmap, opts);
        opts.inSampleSize = calculateSampleSize(opts, width, height);
        opts.inJustDecodeBounds = false;
        return BitmapFactory.decodeResource(context.getResources(), R.drawable.default_bitmap, opts);
    }

    private static int calculateSampleSize(BitmapFactory.Options opts, int reqWidth, int reqHeight) {
        final int height = opts.outHeight;
        final int width = opts.outWidth;
        int inSampleSize = 1;
        if (height > reqHeight || width > reqWidth) {
            final int halfHeight = height / 2;
            final int halfWidth = width / 2;
            while ((halfHeight / inSampleSize) >= reqHeight && (halfWidth / inSampleSize) >= reqWidth)
                inSampleSize *= 2;
        }
        return inSampleSize;
    }

    private void reSearch() {
        Cursor cursor = contentResolver.query(contentUri, projection, null,
                null, null);
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
                MusicInfo musicInfo = new MusicInfo(id, size, duration, title, album, artist, url, albumId);
                musicList.add(musicInfo);
            } while (cursor.moveToNext());
            cursor.close();
        }
    }

    public List<MusicInfo> getMusicList() {
        return musicList;
    }
}