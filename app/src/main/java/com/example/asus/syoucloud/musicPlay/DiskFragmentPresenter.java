package com.example.asus.syoucloud.musicPlay;

import android.content.Context;

import com.example.asus.syoucloud.MusicService;
import com.example.asus.syoucloud.base.BasePresenter;
import com.example.asus.syoucloud.bean.MusicInfo;
import com.example.asus.syoucloud.data.DataRepository;
import com.example.asus.syoucloud.onMusicListener;
import com.example.asus.syoucloud.util.BitmapHelper;
import com.example.asus.syoucloud.util.Constant;

public class DiskFragmentPresenter extends BasePresenter<musicPlayContract.IDiskLayoutFragment>
        implements musicPlayContract.IDiskLayoutPresenter, onMusicListener {

    private Context context;
    private MusicService.MusicPlayer musicPlayer;
    private MusicInfo music;

    DiskFragmentPresenter(Context context, MusicService.MusicPlayer musicPlayer) {
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
    public void noticeAdd() {
        music = musicPlayer.getMusic();
    }

    @Override
    public void addToDatabase(int albumId) {
        if (music == null) return;
        DataRepository.getInstance().addMusicToMix(music, albumId);
    }

    @Override
    public void onMusicCompletion() {
        BitmapHelper.setBitmap(context, mViewRef.get().getIgvView(), musicPlayer.getMusic().getAlbumId());
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
