package com.example.asus.syoucloud;

public interface onMusicListener {
    void onMusicCompletion();

    void onMusicPlayOrPause();

    void onMusicStop();

    default void onStopUpd() {

    }
}
