package com.example.asus.syoucloud;

import android.media.MediaPlayer;
import android.util.Log;

import java.io.IOException;

public class MusicPlayer {
    private static final String TAG = "MusicPlayer";

    private final String path = "/storage/emulated/0/Download/music/sora/saya - またね.mp3";
    private static boolean flag = false;

    private static MusicPlayer musicPlayer;
    private MediaPlayer mediaPlayer;

    private MusicPlayer() {
        mediaPlayer = new MediaPlayer();
        initMediaPlayer();
    }

    public static MusicPlayer getInstance() {
        if (musicPlayer == null) musicPlayer = new MusicPlayer();
        return musicPlayer;
    }

    private void initMediaPlayer() {
        try {
            mediaPlayer.setDataSource(path);
            mediaPlayer.prepare();
        } catch (IOException e) {
            Log.i(TAG, "initMediaPlayer: error");
        }
    }

    public void playOrPause() {
        flag = !flag;
        if (mediaPlayer.isPlaying()) mediaPlayer.pause();
        else mediaPlayer.start();
    }

    public void stop() {
        mediaPlayer.reset();
        initMediaPlayer();
    }
}
