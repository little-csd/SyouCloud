package com.example.asus.syoucloud.presenter;

import android.content.Context;

import com.example.asus.syoucloud.Contract;
import com.example.asus.syoucloud.MusicService;
import com.example.asus.syoucloud.base.BasePresenter;
import com.example.asus.syoucloud.onMusicListener;
import com.example.asus.syoucloud.util.Constant;
import com.example.asus.syoucloud.util.MusicLoader;

public class DiskFragmentPresenter extends BasePresenter<Contract.IDiskLayoutFragment>
        implements Contract.IDiskLayoutPresenter, onMusicListener {

    private Context context;
    private MusicService.MusicPlayer musicPlayer;

    public DiskFragmentPresenter(Context context, MusicService.MusicPlayer musicPlayer) {
        this.context = context;
        this.musicPlayer = musicPlayer;
        musicPlayer.addListener(this, Constant.DISK_TYPE);
    }

    @Override
    public void detachView() {
        super.detachView();
        musicPlayer.deleteListener(Constant.DISK_TYPE);
    }

    @Override
    public void start() {
        onMusicCompletion();
        if (musicPlayer.isPlay()) mViewRef.get().startAnim();
        else mViewRef.get().pauseAnim();
    }

    @Override
    public void onMusicCompletion() {
        MusicLoader.setBitmap(context, mViewRef.get().getIgvView(), musicPlayer.getMusic().getAlbumId());
        mViewRef.get().startAnim();
    }

    @Override
    public void onMusicPlayOrPause() {
        if (musicPlayer.isPlay()) mViewRef.get().startAnim();
        else mViewRef.get().pauseAnim();
    }

    @Override
    public void onMusicStop() {
        mViewRef.get().pauseAnim();
    }

    @Override
    public void onStopUpd() {
        mViewRef.get().pauseAnim();
    }
}
