package com.example.asus.syoucloud.musicPlay;

import android.os.Handler;

import com.example.asus.syoucloud.MusicService;
import com.example.asus.syoucloud.base.BasePresenter;
import com.example.asus.syoucloud.onMusicListener;
import com.example.asus.syoucloud.util.Constant;

import static com.example.asus.syoucloud.util.Constant.LIST_LOOP;
import static com.example.asus.syoucloud.util.Constant.SHUFFLE;
import static com.example.asus.syoucloud.util.Constant.SINGLE_LOOP;

public class MusicPlayActivityPresenter extends BasePresenter<musicPlayContract.IMusicPlayActivity>
        implements musicPlayContract.IMusicPlayActivityPresenter, onMusicListener {

    private MusicService.MusicPlayer musicPlayer;
    private Handler updateHandler = new Handler();
    private int loopStyle;
    private int fragmentType = 0;

    private Runnable progressUpd = new Runnable() {
        @Override
        public void run() {
            int progress = musicPlayer.getCurrentProgress() / 1000;
            updateHandler.postDelayed(this, 300);
            mViewRef.get().update(progress);
        }
    };

    @Override
    public void detachView() {
        super.detachView();
        musicPlayer.deleteListener(Constant.MUSIC_PLAY_TYPE);
        updateHandler.removeCallbacks(progressUpd);
    }

    @Override
    public void play() {
        musicPlayer.playOrPause();
    }

    @Override
    public void next() {
        musicPlayer.next();
    }

    @Override
    public void last() {
        musicPlayer.last();
    }

    @Override
    public void start(MusicService.MusicPlayer musicPlayer) {
        this.musicPlayer = musicPlayer;
        musicPlayer.addListener(this, Constant.MUSIC_PLAY_TYPE);
        loopStyle = musicPlayer.getPlayStyle();
        boolean isPlay = musicPlayer.isPlay();
        int duration = musicPlayer.getDuration();
        mViewRef.get().initData(musicPlayer.getMusic(), duration,
                musicPlayer.getCurrentProgress() / 1000, loopStyle, isPlay);
        mViewRef.get().addFragment(fragmentType++);
        updateHandler.post(progressUpd);
    }

    @Override
    public void stopUpd() {
        updateHandler.removeCallbacks(progressUpd);
    }

    @Override
    public void startUpd() {
        updateHandler.post(progressUpd);
    }

    @Override
    public void changeStyle() {
        if (loopStyle == SINGLE_LOOP) {
            loopStyle = LIST_LOOP;
            musicPlayer.setPlayStyle(LIST_LOOP);
        } else if (loopStyle == LIST_LOOP) {
            loopStyle = SHUFFLE;
            musicPlayer.setPlayStyle(SHUFFLE);
        } else {
            loopStyle = SINGLE_LOOP;
            musicPlayer.setPlayStyle(SINGLE_LOOP);
        }
        mViewRef.get().setStyle(loopStyle);
        mViewRef.get().toastStyle(loopStyle);
    }

    @Override
    public void seekTo(int time) {
        musicPlayer.seekTo(time * 1000);
    }

    @Override
    public void changeFragment() {
        if (fragmentType == Constant.LYRIC_TYPE)
            mViewRef.get().addFragment(fragmentType);
        else mViewRef.get().changeFragment(fragmentType & 1);
        fragmentType++;
    }

    @Override
    public void onMusicCompletion() {
        int duration = musicPlayer.getDuration();
        mViewRef.get().initData(musicPlayer.getMusic(), duration, 0, loopStyle, true);
        updateHandler.removeCallbacks(progressUpd);
        updateHandler.post(progressUpd);
    }

    @Override
    public void onMusicPlayOrPause() {
        if (musicPlayer.isPlay()) mViewRef.get().pause();
        else mViewRef.get().play();
    }

    @Override
    public void onMusicStop() {
        mViewRef.get().play();
    }

    @Override
    public void onStopUpd() {
        updateHandler.removeCallbacks(progressUpd);
    }
}