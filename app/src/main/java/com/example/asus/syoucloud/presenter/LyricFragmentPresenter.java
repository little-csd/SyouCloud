package com.example.asus.syoucloud.presenter;

import android.os.Handler;
import android.util.Log;

import com.example.asus.syoucloud.Contract;
import com.example.asus.syoucloud.LyricView;
import com.example.asus.syoucloud.MusicService;
import com.example.asus.syoucloud.base.BasePresenter;
import com.example.asus.syoucloud.bean.LyricItem;
import com.example.asus.syoucloud.data.DatabaseManager;
import com.example.asus.syoucloud.onMusicListener;
import com.example.asus.syoucloud.util.Constant;

import java.util.List;

public class LyricFragmentPresenter extends BasePresenter<Contract.ILyricFragment>
        implements Contract.ILyricPresenter, onMusicListener,
        LyricView.onLyricListener, DatabaseManager.LyricDownloadListener {

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

    public LyricFragmentPresenter(MusicService.MusicPlayer musicPlayer) {
        this.musicPlayer = musicPlayer;
        musicPlayer.addListener(this, Constant.LYRIC_TYPE);
        DatabaseManager.getInstance().addLyricDownloadListener(this);
    }

    @Override
    public void start() {
        new Thread(() -> {
            long id = musicPlayer.getMusic().getId();
            String TAG = "Lyric";
            Log.i(TAG, "start: " + id);
            List<LyricItem> lyrics = DatabaseManager.getInstance().searchLyric(id);
            if (lyrics.size() > 2)
                Log.i(TAG, "start: " + lyrics.get(2).getText());
            mViewRef.get().setLyricList(lyrics);
            handler.post(timeUpd);
        }).start();
    }

    @Override
    public void detachView() {
        super.detachView();
        musicPlayer.deleteListener(Constant.LYRIC_TYPE);
        DatabaseManager.getInstance().removeLyricDownloadListener();
        handler.removeCallbacks(timeUpd);
    }

    @Override
    public void onMusicCompletion() {
        handler.removeCallbacks(timeUpd);
        new Thread(() -> {
            long id = musicPlayer.getMusic().getId();
            List<LyricItem> lyrics = DatabaseManager.getInstance().searchLyric(id);
            mViewRef.get().setLyricList(lyrics);
            handler.post(timeUpd);
            isDownloading = false;
        }).start();
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
            DatabaseManager.getInstance().downloadLyric(musicPlayer.getMusic());
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