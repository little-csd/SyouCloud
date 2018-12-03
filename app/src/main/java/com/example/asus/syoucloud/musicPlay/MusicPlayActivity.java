package com.example.asus.syoucloud.musicPlay;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v7.app.ActionBar;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.asus.syoucloud.MusicService;
import com.example.asus.syoucloud.R;
import com.example.asus.syoucloud.base.BaseActivity;
import com.example.asus.syoucloud.bean.MusicInfo;
import com.example.asus.syoucloud.util.ActivityUtils;
import com.example.asus.syoucloud.util.Constant;
import com.example.asus.syoucloud.util.TimeUtil;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.example.asus.syoucloud.util.Constant.LIST_LOOP;
import static com.example.asus.syoucloud.util.Constant.SINGLE_LOOP;

public class MusicPlayActivity extends BaseActivity<musicPlayContract.IMusicPlayActivity,
        MusicPlayActivityPresenter> implements musicPlayContract.IMusicPlayActivity {

    @BindView(R.id.music_play_back)
    ImageView musicPlayBack;
    @BindView(R.id.music_play_title)
    TextView musicPlayTitle;
    @BindView(R.id.music_play_artist)
    TextView musicPlayArtist;
    @BindView(R.id.music_play_seekBar)
    SeekBar seekBar;
    @BindView(R.id.music_play_time)
    TextView musicPlayTime;
    @BindView(R.id.music_play_duration)
    TextView musicPlayDuration;
    @BindView(R.id.music_play_begin)
    ImageView musicPlayImage;
    @BindView(R.id.music_play_next)
    ImageView musicPlayNext;
    @BindView(R.id.music_play_last)
    ImageView musicPlayLast;
    @BindView(R.id.music_play_style)
    ImageView musicPlayStyle;
    @BindView(R.id.music_play_list)
    ImageView musicPlayList;

    private LyricFragment lyricFragment;
    private DiskFragment diskFragment;

    private ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mPresenter.start((MusicService.MusicPlayer) service);
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

        ButterKnife.bind(this);

        Intent bindIntent = new Intent(this, MusicService.class);
        bindService(bindIntent, connection, BIND_AUTO_CREATE);

        setOnClickListener();
    }

    @Override
    protected MusicPlayActivityPresenter createPresenter() {
        return new MusicPlayActivityPresenter();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(connection);
    }

    private void setOnClickListener() {
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                musicPlayTime.setText(TimeUtil.parseToString(seekBar.getProgress()));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                mPresenter.stopUpd();
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                mPresenter.seekTo(seekBar.getProgress());
                mPresenter.startUpd();
            }
        });
    }

    @Override
    public void initData(MusicInfo music, int progress, int style, boolean isPlay) {
        musicPlayArtist.setText(music.getArtist());
        musicPlayTitle.setText(music.getTitle());
        musicPlayDuration.setText(TimeUtil.parseToString(music.getDuration() / 1000));
        musicPlayTime.setText(TimeUtil.parseToString(progress));
        seekBar.setMax(music.getDuration() / 1000);
        seekBar.setProgress(progress);
        setStyle(style);
        if (isPlay) pause();
        else play();
    }

    @Override
    public void play() {
        musicPlayImage.setImageResource(R.drawable.play_button_selector);
    }

    @Override
    public void pause() {
        musicPlayImage.setImageResource(R.drawable.pause_button_selector);
    }

    @Override
    public void setStyle(int style) {
        if (style == SINGLE_LOOP) musicPlayStyle.setImageResource(R.drawable.single_loop_selector);
        else if (style == LIST_LOOP) musicPlayStyle.setImageResource(R.drawable.list_loop_selector);
        else musicPlayStyle.setImageResource(R.drawable.shuffle_loop_selector);
    }

    @Override
    public void toastStyle(int style) {
        if (style == SINGLE_LOOP)
            Toast.makeText(this, "Loop single", Toast.LENGTH_SHORT).show();
        else if (style == LIST_LOOP)
            Toast.makeText(this, "Loop all", Toast.LENGTH_SHORT).show();
        else Toast.makeText(this, "Shuffle", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void update(int time) {
        if (time == seekBar.getProgress()) return;
        musicPlayTime.setText(TimeUtil.parseToString(time));
        seekBar.setProgress(time);
    }

    @Override
    public void addFragment(int type) {
        if (type == Constant.LYRIC_TYPE) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .hide(diskFragment)
                    .commit();
            lyricFragment = new LyricFragment();
            ActivityUtils.addFragmentToActivity(getSupportFragmentManager(), lyricFragment,
                    R.id.center_fragment);
        } else {
            diskFragment = new DiskFragment();
            ActivityUtils.addFragmentToActivity(getSupportFragmentManager(), diskFragment,
                    R.id.center_fragment);
        }
    }

    @Override
    public void changeFragment(int type) {
        if (type == Constant.DISK_TYPE) {
            ActivityUtils.changeFragmentInActivity(getSupportFragmentManager(), lyricFragment,
                    diskFragment);
        } else {
            ActivityUtils.changeFragmentInActivity(getSupportFragmentManager(), diskFragment,
                    lyricFragment);
        }
    }

    @OnClick({R.id.music_play_back, R.id.music_play_begin, R.id.music_play_next,
            R.id.music_play_last, R.id.music_play_style, R.id.music_play_list})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.music_play_back:
                finish();
                break;
            case R.id.music_play_begin:
                mPresenter.play();
                break;
            case R.id.music_play_next:
                mPresenter.next();
                break;
            case R.id.music_play_last:
                mPresenter.last();
                break;
            case R.id.music_play_style:
                mPresenter.changeStyle();
                break;
            case R.id.music_play_list:
                mPresenter.changeFragment();
                break;
        }
    }
}