package com.example.asus.syoucloud;

import android.Manifest;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.asus.syoucloud.musicManager.MusicInfo;
import com.example.asus.syoucloud.musicManager.MusicLoader;
import com.example.asus.syoucloud.musicManager.MusicService;
import com.example.asus.syoucloud.musicManager.onMusicListener;

public class MainActivity extends AppCompatActivity implements onMusicListener {

    private static final String TAG = "MainActivity";

    private DrawerLayout mDrawerLayout;
    private ImageView bottomPlay;
    private ImageView bottomBitmap;
    private TextView bottomTitle;
    private TextView bottomArtist;
    private MusicService.MusicPlayer musicPlayer;
    private MusicInfo music;

    private ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            musicPlayer = (MusicService.MusicPlayer) service;
            musicPlayer.setBottomPlayListener(MainActivity.this);
            initData();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        requestPermission();

        initToolbar();
        initView();
        setOnClickListener();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(connection);
        musicPlayer.deleteBottomPlayListener();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (musicPlayer != null) {
            ImageView imageButton = findViewById(R.id.bottom_play);
            if (musicPlayer.isPlay()) imageButton.setImageResource(R.drawable.notification_pause);
            else imageButton.setImageResource(R.drawable.notification_play);
        }
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addCategory(Intent.CATEGORY_HOME);
        startActivity(intent);
    }

    public void bindService() {
        Intent bindIntent = new Intent(this, MusicService.class);
        bindService(bindIntent, connection, BIND_AUTO_CREATE);
    }

    private void requestPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
        } else {
            Log.i(TAG, "onCreate: succeed");
            bindService();
        }
    }

    private void initToolbar() {
        Toolbar toolbar = findViewById(R.id.main_toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.menu_open_icon);
        }
    }

    private void initData() {
        music = musicPlayer.getMusic();
        bottomTitle.setText(music.getTitle());
        bottomArtist.setText(music.getArtist());
        Bitmap bmp = music.getBitmap();
        if (bmp == null) {
            bmp = MusicLoader.getBitmap(this, music.getUrl());
            music.setBitmap(bmp);
        }
        bottomBitmap.setImageBitmap(bmp);
    }

    private void initView() {
        mDrawerLayout = findViewById(R.id.drawer_layout);
        bottomPlay = findViewById(R.id.bottom_play);
        bottomTitle = findViewById(R.id.bottom_title);
        bottomArtist = findViewById(R.id.bottom_artist);
        bottomBitmap = findViewById(R.id.bottom_bitmap);
    }


    private void setOnClickListener() {
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener((item) -> {
            mDrawerLayout.closeDrawers();
            return true;
        });

        bottomPlay.setOnClickListener(v -> musicPlayer.playOrPause());
        LinearLayout bottomLinear = findViewById(R.id.bottom_text);
        bottomLinear.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, MusicPlayActivity.class);
            startActivity(intent);
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                mDrawerLayout.openDrawer(GravityCompat.START);
                break;
            default:
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 1:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    bindService();
                    Log.i(TAG, "onRequestPermissionsResult: succeed");
                } else {
                    Log.i(TAG, "onRequestPermissionsResult: false");
                    Toast.makeText(this, "If we can not acquire this permission, "
                            + "the app can not work", Toast.LENGTH_SHORT).show();
                    finish();
                }
            default:
        }
    }

    @Override
    public void onMusicCompletion() {
        music = musicPlayer.getMusic();
        Bitmap bitmap = music.getBitmap();
        if (bitmap == null) {
            bitmap = MusicLoader.getBitmap(this, music.getUrl());
            music.setBitmap(bitmap);
        }
        bottomBitmap.setImageBitmap(bitmap);
        bottomTitle.setText(music.getTitle());
        bottomArtist.setText(music.getArtist());
    }

    @Override
    public void onMusicLast() {
        onMusicCompletion();
    }

    @Override
    public void onMusicNext() {
        onMusicCompletion();
    }

    @Override
    public void onMusicPlayOrPause() {
        if (musicPlayer.isPlay()) bottomPlay.setImageResource(R.drawable.notification_pause);
        else bottomPlay.setImageResource(R.drawable.notification_play);
    }

    @Override
    public void onMusicStop() {
        onMusicPlayOrPause();
    }
}