package com.example.asus.syoucloud.presenter;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.example.asus.syoucloud.Contract;
import com.example.asus.syoucloud.MusicService;
import com.example.asus.syoucloud.base.BasePresenter;
import com.example.asus.syoucloud.bean.MusicInfo;
import com.example.asus.syoucloud.onMusicListener;
import com.example.asus.syoucloud.util.MusicLoader;
import com.example.asus.syoucloud.view.MusicPlayActivity;

public class BottomLayoutPresenter extends BasePresenter<Contract.IBottomLayoutFragment>
        implements Contract.IBottomLayoutPresenter, onMusicListener {

    private MusicService.MusicPlayer musicPlayer;
    private Context context;
    private int type;

    public BottomLayoutPresenter(Context context, MusicService.MusicPlayer musicPlayer, int type) {
        this.context = context;
        this.musicPlayer = musicPlayer;
        musicPlayer.addListener(this, type);
        this.type = type;
    }

    @Override
    public void detachView() {
        super.detachView();
        musicPlayer.deleteListener(type);
    }

    @Override
    public void onMusicCompletion() {
        MusicInfo music = musicPlayer.getMusic();
        Contract.IBottomLayoutFragment fragment = mViewRef.get();
        fragment.setArtist(music.getArtist());
        fragment.setTitle(music.getTitle());
        fragment.pause();
        MusicLoader.setBitmap(context, fragment.getIgvView(), music.getAlbumId());
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
    public void start() {
        onMusicCompletion();
        if (musicPlayer.isPlay()) mViewRef.get().pause();
        else mViewRef.get().play();
    }

    @Override
    public void play() {
        musicPlayer.playOrPause();
        if (musicPlayer.isPlay()) mViewRef.get().pause();
        else mViewRef.get().play();
    }

    @Override
    public void click() {
        Intent intent = new Intent(context, MusicPlayActivity.class);
        context.startActivity(intent);
    }
}
