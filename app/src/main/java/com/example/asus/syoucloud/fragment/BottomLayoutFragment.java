package com.example.asus.syoucloud.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.asus.syoucloud.R;
import com.example.asus.syoucloud.musicManager.MusicInfo;
import com.example.asus.syoucloud.musicManager.MusicLoader;
import com.example.asus.syoucloud.musicManager.MusicService;
import com.example.asus.syoucloud.musicManager.onMusicListener;

public class BottomLayoutFragment extends Fragment implements onMusicListener {

    private static BottomLayoutFragment fragment;
    private ImageView bottomPlay;
    private ImageView bottomBitmap;
    private TextView bottomTitle;
    private TextView bottomArtist;
    private LinearLayout bottomLinear;
    private MusicInfo music;
    private MusicService.MusicPlayer musicPlayer;

    public static BottomLayoutFragment getInstance() {
        if (fragment == null)
            fragment = new BottomLayoutFragment();
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.bottom_layout_fragment, container, false);
        bottomPlay = view.findViewById(R.id.bottom_play);
        bottomTitle = view.findViewById(R.id.bottom_title);
        bottomArtist = view.findViewById(R.id.bottom_artist);
        bottomBitmap = view.findViewById(R.id.bottom_bitmap);
        bottomLinear = view.findViewById(R.id.bottom_text);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (musicPlayer != null && bottomPlay != null) {
            if (musicPlayer.isPlay()) bottomPlay.setImageResource(R.drawable.notification_pause);
            else bottomPlay.setImageResource(R.drawable.notification_play);
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        musicPlayer.deleteBottomPlayListener();
    }

    @Override
    public void onStart() {
        super.onStart();
        initData();
    }

    public void setMusicPlayer(MusicService.MusicPlayer musicPlayer) {
        this.musicPlayer = musicPlayer;
    }

    public void initData() {
        if (musicPlayer == null) return;

        musicPlayer.setBottomPlayListener(this);

        onMusicPlayOrPause();
        music = musicPlayer.getMusic();
        bottomTitle.setText(music.getTitle());
        bottomArtist.setText(music.getArtist());

        MusicLoader.setBitmap(getContext(), bottomBitmap, music);

        bottomPlay.setOnClickListener(v -> musicPlayer.playOrPause());
        bottomLinear.setOnClickListener(v -> {
            Intent intent = new Intent("MUSIC_PLAY_ACTIVITY");
            startActivity(intent);
        });
    }

    @Override
    public void onMusicCompletion() {
        music = musicPlayer.getMusic();
        MusicLoader.setBitmap(getContext(), bottomBitmap, music);
        bottomTitle.setText(music.getTitle());
        bottomArtist.setText(music.getArtist());
    }

    @Override
    public void onMusicLast() {
        onMusicCompletion();
    }

    @Override
    public void onMusicNext() {
        onMusicCompletion();
    }

    @Override
    public void onMusicPlayOrPause() {
        if (musicPlayer.isPlay()) bottomPlay.setImageResource(R.drawable.notification_pause);
        else bottomPlay.setImageResource(R.drawable.notification_play);
    }

    @Override
    public void onMusicStop() {
        onMusicPlayOrPause();
    }
}
