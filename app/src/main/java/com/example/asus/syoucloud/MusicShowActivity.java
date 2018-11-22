package com.example.asus.syoucloud;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;

import com.example.asus.syoucloud.fragment.BottomLayoutFragment;
import com.example.asus.syoucloud.musicManager.MusicInfo;
import com.example.asus.syoucloud.musicManager.MusicService;

import org.litepal.LitePal;

import java.util.List;

public class MusicShowActivity extends AppCompatActivity {

    private MusicService.MusicPlayer musicPlayer;

    private ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            musicPlayer = (MusicService.MusicPlayer) service;
            BottomLayoutFragment fragment = BottomLayoutFragment.getInstance();
            getSupportFragmentManager()
                    .beginTransaction()
                    .add(R.id.bottom_layout, fragment)
                    .commit();
            initData();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music_show);

        Intent bindIntent = new Intent(this, MusicService.class);
        bindService(bindIntent, connection, BIND_AUTO_CREATE);


    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(connection);
    }

    private void initData() {
        new Thread(() -> {
            Intent intent = getIntent();
            int albumId = intent.getIntExtra("albumId", 0);
            if (albumId == 0) return;
            List<MusicInfo> musicList = LitePal
                    .where("albumId=?", String.valueOf(albumId))
                    .findFirst(MixItem.class)
                    .getMusicList();
        }).start();
    }
}
