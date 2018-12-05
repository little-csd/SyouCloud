package com.example.asus.syoucloud.musicPlay;

import android.os.Handler;
import android.util.Log;

import com.example.asus.syoucloud.MusicService;
import com.example.asus.syoucloud.base.BasePresenter;
import com.example.asus.syoucloud.bean.LyricItem;
import com.example.asus.syoucloud.customView.LyricView;
import com.example.asus.syoucloud.data.DataRepository;
import com.example.asus.syoucloud.onMusicListener;
import com.example.asus.syoucloud.util.Constant;
import com.example.asus.syoucloud.util.ThreadPool;

import java.util.List;

public class LyricFragmentPresenter extends BasePresenter<musicPlayContract.ILyricFragment>
        implements musicPlayContract.ILyricPresenter, onMusicListener,
        LyricView.onLyricListener, DataRepository.LyricDownloadListener {

    private MusicService.MusicPlayer musicPlayer;
    private Handler handler = new Handler();
    private boolean isDownloading = false;

    private Runnable timeUpd = new Runnable() {
        @Override
        public void run() {
            mViewRef.get().seekTo(musicPlayer.getCurrentProgress());
            handler.postDelayed(this, 100);
        }
    };

    LyricFragmentPresenter(MusicService.MusicPlayer musicPlayer) {
        this.musicPlayer = musicPlayer;
        musicPlayer.addListener(this, Constant.LYRIC_TYPE);
        DataRepository.getInstance().addLyricDownloadListener(this);
    }

    @Override
    public void start() {
        ThreadPool.getInstance().execute(() -> {
            long id = musicPlayer.getMusic().getId();
            List<LyricItem> lyrics = DataRepository.getInstance().searchLyric(id);
            mViewRef.get().setLyricList(lyrics);
            handler.post(timeUpd);
        });
    }

    @Override
    public void detachView() {
        super.detachView();
        musicPlayer.deleteListener(Constant.LYRIC_TYPE);
        DataRepository.getInstance().removeLyricDownloadListener();
        handler.removeCallbacks(timeUpd);
    }

    @Override
    public void onMusicCompletion() {
        handler.removeCallbacks(timeUpd);
        ThreadPool.getInstance().execute(() -> {
            long id = musicPlayer.getMusic().getId();
            List<LyricItem> lyrics = DataRepository.getInstance().searchLyric(id);
            mViewRef.get().setLyricList(lyrics);
            handler.post(timeUpd);
            isDownloading = false;
        });
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

    @Override
    public void onDownloadLyric() {
        if (isDownloading) mViewRef.get().mkToast(Constant.DOWNLOADING);
        else {
            mViewRef.get().mkToast(Constant.DOWNLOAD_BEGIN);
            isDownloading = true;
            DataRepository.getInstance().downloadLyric(musicPlayer.getMusic());
        }
    }

    @Override
    public void mkToast(int type) {
        mViewRef.get().mkToast(type);
        isDownloading = false;
    }

    @Override
    public void update() {
        onMusicCompletion();
    }
}