package com.example.asus.syoucloud;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.design.widget.BottomSheetDialog;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.asus.syoucloud.musicManager.MusicInfo;
import com.example.asus.syoucloud.musicManager.MusicService;
import com.example.asus.syoucloud.musicManager.onMusicListener;

import static com.example.asus.syoucloud.Constant.LIST_LOOP;
import static com.example.asus.syoucloud.Constant.SHUFFLE;
import static com.example.asus.syoucloud.Constant.SINGLE_LOOP;

public class MusicPlayActivity extends AppCompatActivity implements onMusicListener {

    private static final String TAG = "MusicPlayActivity";
    private static final String START_TIME = "00:00";
    private boolean hasPause = false;
    private int loopStyle;

    private BottomSheetDialog bottomSheetDialog;
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
    private ImageView musicPlayList;
    private ImageView albumImage;
    private MusicService.MusicPlayer musicPlayer;
    private MusicInfo music;
    private ObjectAnimator albumAnim;
    private Handler updateHandler;

    private Runnable progressUpd = new Runnable() {
        @Override
        public void run() {
            updateHandler.postDelayed(this, 500);
            seekBar.setProgress(musicPlayer.getCurrentProgress() / 1000);
            musicCurrentTime.setText(MusicService.parseToString(seekBar.getProgress()));
        }
    };

    private ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            musicPlayer = (MusicService.MusicPlayer) service;
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

        initView();
        initAnim();
        initData();
        setOnClickListener();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        musicPlayer.deleteMusicPlayListener();
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
        musicPlayList = findViewById(R.id.music_play_list);
        albumImage = findViewById(R.id.album_image);
        seekBar = findViewById(R.id.music_play_seekBar);

        bottomSheetDialog = new BottomSheetDialog(this);
        View view = getLayoutInflater().inflate(R.layout.bottm_sheet_dialog_layout, null);
        bottomSheetDialog.setContentView(view);
    }

    private void initData() {
        Intent bindIntent = new Intent(this, MusicService.class);
        bindService(bindIntent, connection, BIND_AUTO_CREATE);

        music = musicPlayer.getMusic();
        int barMax = music.getDuration() / 1000;
        seekBar.setMax(barMax);
        seekBar.setProgress(musicPlayer.getCurrentProgress() / 1000);
        updateHandler = new Handler();

        loopStyle = musicPlayer.getPlayStyle();
        musicPlayer.setMusicPlayListener(this);
        musicPlayArtist.setText(music.getArtist());
        musicPlayTitle.setText(music.getTitle());
        musicDuration.setText(MusicService.parseToString(musicPlayer.getDuration() / 1000));
        musicCurrentTime.setText(MusicService.parseToString(musicPlayer.getCurrentProgress() / 1000));

        if (musicPlayer.isPlay()) {
            musicPlayImage.setImageResource(R.drawable.pause_button_selector);
            albumAnim.start();
            updateHandler.post(progressUpd);
        }
    }

    private void initAnim() {
        albumAnim = ObjectAnimator.ofFloat(albumImage,
                "rotation", 0f, 360 * 100f);
        albumAnim.setDuration(25 * 100 * 1000);
        albumAnim.setInterpolator(new LinearInterpolator());
        albumAnim.setRepeatCount(ValueAnimator.INFINITE);
        albumAnim.setRepeatMode(ValueAnimator.REVERSE);
    }

    private void setOnClickListener() {
        musicPlayBack.setOnClickListener(v -> finish());
        musicPlayLast.setOnClickListener(v -> {
            updateHandler.removeCallbacks(progressUpd);
            musicPlayer.last();
            updateHandler.post(progressUpd);

        });
        musicPlayNext.setOnClickListener(v -> {
            updateHandler.removeCallbacks(progressUpd);
            musicPlayer.next();
            updateHandler.post(progressUpd);

        });
        musicPlayList.setOnClickListener(v -> bottomSheetDialog.show());
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
    }

    @Override
    public void onMusicCompletion() {
        music = musicPlayer.getMusic();
        seekBar.setMax(music.getDuration() / 1000);
        musicDuration.setText(MusicService.parseToString(musicPlayer.getDuration() / 1000));
        musicPlayTitle.setText(music.getTitle());
        musicPlayArtist.setText(music.getArtist());
        albumImage.setImageBitmap(music.getBitmap());
        albumAnim.start();
    }

    @Override
    public void onMusicLast() {
        musicCurrentTime.setText(START_TIME);
        seekBar.setProgress(0);
        musicPlayImage.setImageResource(R.drawable.pause_button_selector);
        onMusicCompletion();
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
            albumAnim.pause();
            updateHandler.removeCallbacks(progressUpd);
            hasPause = true;
        } else {
            musicPlayImage.setImageResource(R.drawable.play_button_selector);
            if (hasPause) albumAnim.resume();
            else albumAnim.start();
            updateHandler.post(progressUpd);
        }
    }
}