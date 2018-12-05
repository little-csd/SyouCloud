package com.example.asus.syoucloud.util;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.ImageView;

import com.example.asus.syoucloud.R;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;

public class BitmapHelper {

    private static final String TAG = "BitmapHelper";
    private static final Uri sArtworkUri = Uri.parse("content://media/external/audio/albumart");
    private static float scale;
    private static ContentResolver contentResolver;

    private BitmapHelper() {

    }

    public static void init(float scale, ContentResolver contentResolver) {
        BitmapHelper.scale = scale;
        BitmapHelper.contentResolver = contentResolver;
    }

    public static void setBitmapNetwork(Context context, @NonNull ImageView igv, String title) {
        if (igv.getHeight() == 0) {
            igv.post(() -> setBitmapNetwork(context, igv, title));
            return;
        }

        ThreadPool.getInstance().execute(() -> {
            File file = new File(Constant.bmpTarget + title + ".bmp");
            if (!file.exists()) {
                Log.i(TAG, "setBitmapNetwork: file not found");
                return;
            }
            Uri uri = Uri.fromFile(file);
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
                Log.i(TAG, "setBitmapLocal: 不存在该文件");
                Bitmap bmp = getDefaultBitmap(context, igv.getWidth(), igv.getHeight());
                runOnUi(() -> igv.setImageBitmap(bmp));
            }
        });
    }

    public static void setBitmapLocal(Context context, @NonNull ImageView igv, int albumId) {
        if (igv.getHeight() == 0) {
            igv.post(() -> setBitmapLocal(context, igv, albumId));
            return;
        }

        ThreadPool.getInstance().execute(() -> {
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
                Log.i(TAG, "setBitmapLocal: 不存在该文件");
                Bitmap bmp = getDefaultBitmap(context, igv.getWidth(), igv.getHeight());
                runOnUi(() -> igv.setImageBitmap(bmp));
            }
        });
    }

    public static Bitmap getBitmapLocal(Context context, int albumId, int width, int height) {
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

    public static Bitmap getBitmapNetwork(Context context, String title, int width, int height) {
        File file = new File(Constant.bmpTarget + title + ".bmp");
        width = (int) (width * scale);
        height = (int) (height * scale);
        if (!file.exists()) {
            Log.i(TAG, "setBitmapNetwork: file not found");
            return getDefaultBitmap(context, width, height);
        }
        Uri uri = Uri.fromFile(file);
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

    private static void runOnUi(Runnable r) {
        if (Looper.myLooper() != Looper.getMainLooper()) {
            Handler handler = new Handler(Looper.getMainLooper());
            handler.post(r);
        } else r.run();
    }
}