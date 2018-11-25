package com.example.asus.syoucloud;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.asus.syoucloud.fragment.DiskFragment;
import com.example.asus.syoucloud.fragment.LyricFragment;
import com.example.asus.syoucloud.musicManager.MusicInfo;
import com.example.asus.syoucloud.musicManager.MusicLoader;
import com.example.asus.syoucloud.musicManager.MusicService;
import com.example.asus.syoucloud.musicManager.onMusicListener;

import static com.example.asus.syoucloud.util.Constant.LIST_LOOP;
import static com.example.asus.syoucloud.util.Constant.SHUFFLE;
import static com.example.asus.syoucloud.util.Constant.SINGLE_LOOP;

public class MusicPlayActivity extends AppCompatActivity implements onMusicListener {

    private static final String START_TIME = "00:00";
    private boolean isDisk = false;
    private boolean hasPause = false;
    private int loopStyle;

    private DiskFragment diskFragment;
    private LyricFragment lyricFragment;
    private SeekBar seekBar;
    private TextView musicPlayTitle;
    private TextView musicPlayArtist;
    private TextView musicCurrentTime;
    private TextView musicDuration;
    private ImageView musicPlayImage;
    private ImageView musicPlayBack;
    private ImageView musicPlayNext;
    private ImageView musicPlayLast;
    private ImageView musicLoopStyle;
    private MusicService.MusicPlayer musicPlayer;
    private MusicInfo music;
    private Handler updateHandler;

    private Runnable progressUpd = new Runnable() {
        @Override
        public void run() {
            int progress = musicPlayer.getCurrentProgress() / 1000;
            updateHandler.postDelayed(this, 500);
            seekBar.setProgress(progress);
            musicCurrentTime.setText(MusicService.parseToString(seekBar.getProgress()));
        }
    };

    private ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            musicPlayer = (MusicService.MusicPlayer) service;
            initData();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music_play);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) actionBar.hide();

        Intent bindIntent = new Intent(this, MusicService.class);
        bindService(bindIntent, connection, BIND_AUTO_CREATE);

        initView();
        setOnClickListener();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        musicPlayer.deleteMusicPlayListener();
        unbindService(connection);
        updateHandler.removeCallbacks(progressUpd);
    }

    private void initView() {
        musicPlayBack = findViewById(R.id.music_play_back);
        musicPlayImage = findViewById(R.id.music_play_begin);
        musicCurrentTime = findViewById(R.id.music_play_time);
        musicDuration = findViewById(R.id.music_play_duration);
        musicPlayTitle = findViewById(R.id.music_play_title);
        musicPlayArtist = findViewById(R.id.music_play_artist);
        musicPlayLast = findViewById(R.id.music_play_last);
        musicPlayNext = findViewById(R.id.music_play_next);
        musicLoopStyle = findViewById(R.id.music_play_style);
        seekBar = findViewById(R.id.music_play_seekBar);
    }

    private void initData() {
        changeFragment();
        music = musicPlayer.getMusic();
        int barMax = music.getDuration() / 1000;
        seekBar.setMax(barMax);
        seekBar.setProgress(musicPlayer.getCurrentProgress() / 1000);
        updateHandler = new Handler();

        diskFragment.setMusic(music);

        loopStyle = musicPlayer.getPlayStyle();
        if (loopStyle == SINGLE_LOOP)
            musicLoopStyle.setImageResource(R.drawable.single_loop_selector);
        else if (loopStyle == LIST_LOOP)
            musicLoopStyle.setImageResource(R.drawable.list_loop_selector);
        else musicLoopStyle.setImageResource(R.drawable.shuffle_loop_selector);

        musicPlayer.setMusicPlayListener(this);
        musicPlayArtist.setText(music.getArtist());
        musicPlayTitle.setText(music.getTitle());
        musicDuration.setText(MusicService.parseToString(musicPlayer.getDuration() / 1000));
        musicCurrentTime.setText(MusicService.parseToString(musicPlayer.getCurrentProgress() / 1000));

        if (musicPlayer.isPlay()) {
            musicPlayImage.setImageResource(R.drawable.pause_button_selector);
            diskFragment.setIsPlay(true);
            updateHandler.post(progressUpd);
        }
    }

    private void setOnClickListener() {
        musicPlayBack.setOnClickListener(v -> finish());
        musicPlayLast.setOnClickListener(v -> musicPlayer.last());
        musicPlayNext.setOnClickListener(v -> musicPlayer.next());
        musicPlayImage.setOnClickListener(v -> musicPlayer.playOrPause());
        musicLoopStyle.setOnClickListener(v -> {
            if (loopStyle == SINGLE_LOOP) {
                loopStyle = LIST_LOOP;
                musicPlayer.setPlayStyle(LIST_LOOP);
                musicLoopStyle.setImageResource(R.drawable.list_loop_selector);
                Toast.makeText(this, "Loop all", Toast.LENGTH_SHORT).show();
            } else if (loopStyle == LIST_LOOP) {
                loopStyle = SHUFFLE;
                musicPlayer.setPlayStyle(SHUFFLE);
                musicLoopStyle.setImageResource(R.drawable.shuffle_loop_selector);
                Toast.makeText(this, "Shuffle", Toast.LENGTH_SHORT).show();
            } else {
                loopStyle = SINGLE_LOOP;
                musicPlayer.setPlayStyle(SINGLE_LOOP);
                musicLoopStyle.setImageResource(R.drawable.single_loop_selector);
                Toast.makeText(this, "Loop single", Toast.LENGTH_SHORT).show();
            }
        });
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                musicCurrentTime.setText(MusicService.parseToString(seekBar.getProgress()));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                updateHandler.removeCallbacks(progressUpd);
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                musicPlayer.seekTo(seekBar.getProgress());
                updateHandler.post(progressUpd);
            }
        });
        ImageView imageView = findViewById(R.id.music_play_list);
        imageView.setOnClickListener(v -> changeFragment());
    }

    private void changeFragment() {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out);
        if (isDisk) {
            transaction.hide(diskFragment);
            if (lyricFragment == null) {
                lyricFragment = new LyricFragment();
                transaction.add(R.id.center_fragment, lyricFragment).commit();
                lyricFragment.setMusicPlayer(musicPlayer);
                lyricFragment.setSeekToListener(musicPlayer);
            } else transaction.show(lyricFragment).commit();
            if (musicPlayer.isPlay()) {
                diskFragment.pauseAnim();
                hasPause = true;
            }
        } else {
            if (diskFragment == null) {
                diskFragment = new DiskFragment();
                transaction.add(R.id.center_fragment, diskFragment).commit();
                if (musicPlayer.isPlay()) diskFragment.setIsPlay(true);
            } else {
                transaction.hide(lyricFragment);
                transaction.show(diskFragment).commit();
                if (musicPlayer.isPlay()) {
                    if (!hasPause) diskFragment.startAnim();
                    else diskFragment.resumeAnim();
                }
            }
        }
        isDisk = !isDisk;
    }

    @Override
    public void onMusicCompletion() {
        music = musicPlayer.getMusic();
        seekBar.setMax(music.getDuration() / 1000);
        seekBar.setProgress(0);
        musicDuration.setText(MusicService.parseToString(musicPlayer.getDuration() / 1000));
        musicPlayTitle.setText(music.getTitle());
        musicPlayArtist.setText(music.getArtist());
        diskFragment.startAnim();
        MusicLoader.setBitmap(getApplicationContext(), diskFragment.getAlbumImage(), music.getAlbumId());
        updateHandler.post(progressUpd);
        if (lyricFragment != null) lyricFragment.startUpd();
    }

    @Override
    public void onMusicNext() {
        musicCurrentTime.setText(START_TIME);
        seekBar.setProgress(0);
        musicPlayImage.setImageResource(R.drawable.pause_button_selector);
        onMusicCompletion();
    }

    @Override
    public void onMusicPlayOrPause() {
        if (musicPlayer.isPlay()) {
            musicPlayImage.setImageResource(R.drawable.pause_button_selector);
            if (hasPause) diskFragment.resumeAnim();
            else diskFragment.startAnim();
            updateHandler.post(progressUpd);
        } else {
            musicPlayImage.setImageResource(R.drawable.play_button_selector);
            diskFragment.pauseAnim();
            updateHandler.removeCallbacks(progressUpd);
            hasPause = true;
        }
    }

    @Override
    public void onMusicStop() {
        onMusicPlayOrPause();
        seekBar.setProgress(0);
        musicCurrentTime.setText(START_TIME);
    }

    @Override
    public void onStopUpd() {
        if (lyricFragment != null)
            lyricFragment.stopUpd();
        updateHandler.removeCallbacks(progressUpd);
    }

    @Override
    public void onUpdateLyric() {
        lyricFragment.updateLyric();
    }
}