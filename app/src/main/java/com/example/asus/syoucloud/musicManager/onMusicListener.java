package com.example.asus.syoucloud.musicManager;

public interface onMusicListener {
    void onMusicCompletion();

    void onMusicLast();

    void onMusicNext();

    void onMusicPlayOrPause();

    void onMusicStop();

    default void onStopUpd() {

    }
}
