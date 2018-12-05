package com.example.asus.syoucloud.overlayWindow;

import android.content.Context;
import android.os.Handler;
import android.util.Log;

import com.example.asus.syoucloud.MusicService;
import com.example.asus.syoucloud.bean.LyricItem;
import com.example.asus.syoucloud.data.DataRepository;
import com.example.asus.syoucloud.onMusicListener;
import com.example.asus.syoucloud.util.Constant;
import com.example.asus.syoucloud.util.ThreadPool;

import java.util.List;

public class OverlayWindowPresenter implements overlayWindowContract.IOverlayWindowPresenter,
        onMusicListener, DataRepository.LyricDownloadListener {

    private static final String TAG = "OverlayWindowPresenter";

    private MusicService.MusicPlayer musicPlayer;
    private overlayWindowContract.IOverlayWindowManager overlayWindowManager;
    private List<LyricItem> lyricList;
    private Handler handler = new Handler();

    private int line = -1;
    private boolean isLyricShow = false;
    private boolean isLock = false;

    private Runnable updLyric = new Runnable() {
        @Override
        public void run() {
            handler.postDelayed(this, 400);
            int mLine = findCurrentLine(musicPlayer.getCurrentProgress());
            if (mLine == line) return;
            if (mLine >= lyricList.size()) {
                Log.i(TAG, "update lyric error");
                return;
            }
            LyricItem lyric = lyricList.get(mLine);
            String text = lyric.getText();
            if (lyric.getTranslate() != null)
                text = text + "\n" + lyric.getTranslate();
            line = mLine;
            overlayWindowManager.updateText(text);
        }
    };

    public OverlayWindowPresenter(MusicService.MusicPlayer musicPlayer, Context context) {
        this.musicPlayer = musicPlayer;
        overlayWindowManager = new OverlayWindowManager();
        overlayWindowManager.setPresenter(this);
        overlayWindowManager.initData(context);

        ThreadPool.getInstance().execute(() -> lyricList =
                DataRepository.getInstance().searchLyric(musicPlayer.getMusic().getId()));
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
    public void close() {
        musicPlayer.lyricClick();
    }

    @Override
    public void lock() {
        overlayWindowManager.lock();
        musicPlayer.sendUnlockNotification();
        isLock = true;
    }

    public void showLyric() {
        if (isLyricShow) return;
        isLyricShow = true;
        handler.post(updLyric);
        musicPlayer.addListener(this, Constant.OVERLAY_TYPE);
        DataRepository.getInstance().addOverlayDownloadListener(this);
        overlayWindowManager.showLyric(musicPlayer.isPlay());
    }

    public void removeLyric() {
        if (!isLyricShow) return;
        isLyricShow = false;
        handler.removeCallbacks(updLyric);
        musicPlayer.deleteListener(Constant.OVERLAY_TYPE);
        DataRepository.getInstance().removeOverlayDownloadListener();
        overlayWindowManager.removeLyric();
    }

    public void unLock() {
        overlayWindowManager.unLock();
        overlayWindowManager.update();
        isLock = false;
    }

    public void cancel() {
        overlayWindowManager.unLock();
        isLock = false;
    }

    public boolean isLock() {
        return isLock;
    }

    @Override
    public void onMusicCompletion() {
        handler.removeCallbacks(updLyric);
        ThreadPool.getInstance().execute(() -> {
            lyricList = DataRepository.getInstance().searchLyric(musicPlayer.getMusic().getId());
            handler.post(updLyric);
        });
    }

    @Override
    public void onMusicPlayOrPause() {
        if (musicPlayer.isPlay()) handler.post(updLyric);
        else handler.removeCallbacks(updLyric);
        overlayWindowManager.updateImage(musicPlayer.isPlay());
    }

    @Override
    public void onMusicStop() {
        handler.removeCallbacks(updLyric);
    }

    @Override
    public void onStopUpd() {
        handler.removeCallbacks(updLyric);
    }

    @Override
    public void update() {
        onMusicCompletion();
    }

    private int findCurrentLine(int time) {
        int l = 0, r = lyricList.size() - 1;
        while (l <= r) {
            int mid = (l + r) / 2;
            if (lyricList.get(mid).getTime() <= time) l = mid + 1;
            else r = mid - 1;
        }
        return r;
    }
}
