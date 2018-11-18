package com.example.asus.syoucloud;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.graphics.Bitmap;
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

import com.example.asus.syoucloud.musicManager.onLyricSeekToListener;

public class DiskFragment extends Fragment {

    private Bitmap bitmap;
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
        albumImage.setImageBitmap(bitmap);
        initAnim();
        if (isPlay) startAnim();
    }

    public void setImageBitmap(Bitmap bitmap) {
        albumImage.setImageBitmap(bitmap);
    }

    public void setIsPlay(boolean isPlay) {
        this.isPlay = isPlay;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    public void initAnim() {
        albumAnim = ObjectAnimator.ofFloat(albumImage,
                "rotation", 0f, 360 * 100f);
        albumAnim.setDuration(25 * 100 * 1000);
        albumAnim.setInterpolator(new LinearInterpolator());
        albumAnim.setRepeatCount(ValueAnimator.INFINITE);
        albumAnim.setRepeatMode(ValueAnimator.REVERSE);
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
}
