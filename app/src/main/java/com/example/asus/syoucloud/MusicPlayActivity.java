package com.example.asus.syoucloud;

import android.content.ContentResolver;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;

import java.util.List;

public class MusicPlayActivity extends AppCompatActivity {

    private static final String TAG = "MusicPlayActivity";
    private static boolean flag = false;

    private MusicPlayer musicPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music_play);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) actionBar.hide();

        initData();
        initView();
    }

    private void initData() {
        musicPlayer = MusicPlayer.getInstance();
    }

    private void initView() {
        ImageView playImage = findViewById(R.id.music_play_begin);
        playImage.setOnClickListener(v -> {
            musicPlayer.playOrPause();
            if (flag) playImage.setImageResource(R.drawable.play_button);
            else playImage.setImageResource(R.drawable.pause_button);
            flag = !flag;
        });
    }
}