package com.example.asus.syoucloud.util;

import android.util.Log;

import com.example.Gson.Music.MusicResultItem;
import com.example.asus.syoucloud.bean.MusicInfo;
import com.example.asus.syoucloud.data.DataRepository;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;

import okhttp3.OkHttpClient;
import okhttp3.Request;

import static com.example.asus.syoucloud.util.Constant.bmpTarget;
import static com.example.asus.syoucloud.util.Constant.lyricTarget;
import static com.example.asus.syoucloud.util.Constant.musicTarget;

public class NetworkHelper {

    private static final String TAG = "NetworkHelper";
    private static boolean isDownloading = false;

    public static void sendDownloadRequest(String address, okhttp3.Callback callback) {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(address)
                .build();
        client.newCall(request).enqueue(callback);
    }

    public static void downloadMusic(MusicResultItem music) {
        if (isDownloading) {
            Log.i(TAG, "downloadMusic: isDownloading");
            return;
        }
        isDownloading = true;
        try {
            File target = new File(musicTarget);
            if (!target.exists()) {
                boolean mkFile = target.mkdirs();
                if (!mkFile) {
                    Log.i(TAG, "downloadMusic: make file fail");
                    isDownloading = false;
                    return;
                }
            }
            String fileName = musicTarget + music.getName() + ".mp3";
            File file = new File(fileName);
            if (file.exists()) {
                Log.i(TAG, "downloadMusic: file has exist");
                isDownloading = false;
                return;
            }
            byte[] bt = new byte[2048];
            int len;
            URL url = new URL(music.getUrl());
            InputStream in = url.openStream();
            OutputStream os = new FileOutputStream(file);
            while ((len = in.read(bt)) != -1) {
                os.write(bt, 0, len);
            }
            os.close();
            in.close();
            Log.i(TAG, "downloadMusic: Download succeed");
            downloadLyric(music, fileName);
        } catch (Exception e) {
            e.printStackTrace();
            Log.i(TAG, "downloadMusic: " + e);
            isDownloading = false;
        }
    }

    private static void downloadLyric(MusicResultItem music, String address) {
        long id = DataRepository.getInstance().getMaxMusicId() + 1;
        try {
            File target = new File(lyricTarget);
            if (!target.exists()) {
                boolean mkFile = target.mkdirs();
                if (!mkFile) {
                    Log.i(TAG, "downloadLyric: make file fail");
                    isDownloading = false;
                    return;
                }
            }
            String fileName = lyricTarget + music.getName() + ".lrc";
            File file = new File(fileName);
            if (file.exists()) {
                Log.i(TAG, "downloadLyric: file has exist");
                isDownloading = false;
                return;
            }
            byte[] bt = new byte[2048];
            int len;
            URL url = new URL(music.getLrc());
            InputStream in = url.openStream();
            OutputStream os = new FileOutputStream(file);
            while ((len = in.read(bt)) != -1) {
                os.write(bt, 0, len);
            }
            os.close();
            in.close();
            Log.i(TAG, "downloadLyric: Download succeed");
            downloadBmp(music, () -> {
                MusicInfo mMusic = new MusicInfo();
                mMusic.setId(id);
                mMusic.setAlbumId(-1);
                mMusic.setArtist(music.getSinger());
                mMusic.setTitle(music.getName());
                mMusic.setUrl(address);
                DataRepository.getInstance().addMusic(mMusic);
                DataRepository.getInstance().hasLyricDownload(id, music.getName());
                isDownloading = false;
            });
        } catch (Exception e) {
            Log.i(TAG, "downloadLyric: " + e);
            e.printStackTrace();
        }
    }

    private static void downloadBmp(MusicResultItem music, Callback callback) {
        try {
            File target = new File(bmpTarget);
            if (!target.exists()) {
                boolean mkFile = target.mkdirs();
                if (!mkFile) {
                    Log.i(TAG, "downloadBmp: make file fail");
                    isDownloading = false;
                    return;
                }
            }
            String fileName = bmpTarget + music.getName() + ".bmp";
            File file = new File(fileName);
            if (file.exists()) {
                Log.i(TAG, "downloadBitmap: file has exist");
                isDownloading = false;
                return;
            }
            byte[] bt = new byte[2048];
            int len;
            URL url = new URL(music.getPic());
            InputStream in = url.openStream();
            OutputStream os = new FileOutputStream(file);
            while ((len = in.read(bt)) != -1) {
                os.write(bt, 0, len);
            }
            os.close();
            in.close();
            Log.i(TAG, "downloadBitmap: Download succeed");
            callback.onFinish();
        } catch (Exception e) {
            Log.i(TAG, "downloadBitmap: " + e);
            e.printStackTrace();
            isDownloading = false;
        }
    }

    interface Callback {
        void onFinish();
    }
}
