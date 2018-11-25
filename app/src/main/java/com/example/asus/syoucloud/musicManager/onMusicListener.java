package com.example.asus.syoucloud.musicManager;

public interface onMusicListener {
    void onMusicCompletion();

    void onMusicNext();

    void onMusicPlayOrPause();

    void onMusicStop();

    default void onUpdateLyric() {

    }

    default void onStopUpd() {

    }
}
