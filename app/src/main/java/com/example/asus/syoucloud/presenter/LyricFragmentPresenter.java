package com.example.asus.syoucloud.presenter;

import android.os.Handler;

import com.example.asus.syoucloud.Contract;
import com.example.asus.syoucloud.MusicService;
import com.example.asus.syoucloud.base.BasePresenter;
import com.example.asus.syoucloud.onLyricSeekToListener;
import com.example.asus.syoucloud.onMusicListener;
import com.example.asus.syoucloud.util.Constant;
import com.example.asus.syoucloud.util.LrcHandle;

public class LyricFragmentPresenter extends BasePresenter<Contract.ILyricFragment>
        implements Contract.ILyricPresenter, onMusicListener, onLyricSeekToListener {

    private LrcHandle lrcHandle = LrcHandle.getInstance();
    private MusicService.MusicPlayer musicPlayer;
    private Handler handler = new Handler();

    private Runnable timeUpd = new Runnable() {
        @Override
        public void run() {
            mViewRef.get().seekTo(musicPlayer.getCurrentProgress());
            handler.postDelayed(this, 100);
        }
    };

    public LyricFragmentPresenter(MusicService.MusicPlayer musicPlayer) {
        this.musicPlayer = musicPlayer;
        musicPlayer.addListener(this, Constant.LYRIC_TYPE);
    }

    @Override
    public void start() {
        new Thread(() -> {
            lrcHandle.readLRC("/storage/emulated/0/Download/鳥の詩.lrc");
            mViewRef.get().setLyricList(lrcHandle.getLyricList());
            handler.post(timeUpd);
        }).start();
    }

    @Override
    public void detachView() {
        super.detachView();
        musicPlayer.deleteListener(Constant.LYRIC_TYPE);
        handler.removeCallbacks(timeUpd);
    }

    @Override
    public void onMusicCompletion() {
        handler.removeCallbacks(timeUpd);
        handler.post(timeUpd);
    }

    @Override
    public void onMusicPlayOrPause() {
        if (musicPlayer.isPlay()) handler.post(timeUpd);
        else handler.removeCallbacks(timeUpd);
    }

    @Override
    public void onMusicStop() {
        handler.removeCallbacks(timeUpd);
    }

    @Override
    public void onStopUpd() {
        handler.removeCallbacks(timeUpd);
    }

    @Override
    public boolean onSeekTo(int time) {
        musicPlayer.seekTo(time);
        return true;
    }
}