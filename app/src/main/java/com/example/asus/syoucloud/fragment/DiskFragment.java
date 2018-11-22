package com.example.asus.syoucloud.fragment;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;

import com.example.asus.syoucloud.R;
import com.example.asus.syoucloud.musicManager.MusicInfo;
import com.example.asus.syoucloud.musicManager.MusicLoader;
import com.example.asus.syoucloud.musicManager.onLyricSeekToListener;

import java.net.URL;

public class DiskFragment extends Fragment {

    private MusicInfo music;
    private ImageView albumImage;
    private ObjectAnimator albumAnim;
    private boolean isPlay = false;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.disk_fragment, container, false);
        albumImage = view.findViewById(R.id.album_image);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        MusicLoader.setBitmap(getContext(), albumImage, music);
        initAnim();
        if (isPlay) startAnim();
    }

    public void setIsPlay(boolean isPlay) {
        this.isPlay = isPlay;
    }

    public void initAnim() {
        albumAnim = ObjectAnimator.ofFloat(albumImage,
                "rotation", 0f, 360f);
        albumAnim.setDuration(25 * 1000);
        albumAnim.setInterpolator(new LinearInterpolator());
        albumAnim.setRepeatCount(ValueAnimator.INFINITE);
        albumAnim.setRepeatMode(ValueAnimator.RESTART);
    }

    public void startAnim() {
        albumAnim.start();
    }

    public void pauseAnim() {
        albumAnim.pause();
    }

    public void resumeAnim() {
        albumAnim.resume();
    }

    public void setMusic(MusicInfo music) {
        this.music = music;
    }

    public ImageView getAlbumImage() {
        return albumImage;
    }
}

