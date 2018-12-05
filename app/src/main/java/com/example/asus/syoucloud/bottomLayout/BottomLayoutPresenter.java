package com.example.asus.syoucloud.bottomLayout;

import android.content.Context;
import android.content.Intent;

import com.example.asus.syoucloud.MusicService;
import com.example.asus.syoucloud.base.BasePresenter;
import com.example.asus.syoucloud.bean.MusicInfo;
import com.example.asus.syoucloud.musicPlay.MusicPlayActivity;
import com.example.asus.syoucloud.onMusicListener;
import com.example.asus.syoucloud.util.BitmapHelper;

public class BottomLayoutPresenter extends BasePresenter<bottomLayoutContract.IBottomLayoutFragment>
        implements bottomLayoutContract.IBottomLayoutPresenter, onMusicListener {

    private MusicService.MusicPlayer musicPlayer;
    private Context context;
    private int type;

    BottomLayoutPresenter(Context context, MusicService.MusicPlayer musicPlayer, int type) {
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
        bottomLayoutContract.IBottomLayoutFragment fragment = mViewRef.get();
        fragment.setArtist(music.getArtist());
        fragment.setTitle(music.getTitle());
        fragment.pause();
        if (music.getAlbumId() != -1)
            BitmapHelper.setBitmapLocal(context, fragment.getIgvView(), music.getAlbumId());
        else
            BitmapHelper.setBitmapNetwork(context, fragment.getIgvView(), music.getTitle());
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
