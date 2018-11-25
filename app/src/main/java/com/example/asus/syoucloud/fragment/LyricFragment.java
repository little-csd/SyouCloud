package com.example.asus.syoucloud.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.asus.syoucloud.R;
import com.example.asus.syoucloud.musicManager.LyricView;
import com.example.asus.syoucloud.musicManager.MusicService;
import com.example.asus.syoucloud.musicManager.onLyricSeekToListener;

public class LyricFragment extends Fragment {

    private MusicService.MusicPlayer musicPlayer;
    private onLyricSeekToListener seekToListener;
    private LyricView lyricView;
    private Handler handler = new Handler();

    private Runnable updateRun = new Runnable() {
        @Override
        public void run() {
            handler.postDelayed(this, 100);
            lyricView.updateTime(musicPlayer.getCurrentProgress());
        }
    };

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.lyric_fragment, container, false);
        lyricView = view.findViewById(R.id.lyric_view);
        lyricView.setLyricList(musicPlayer.getLyricList());
        lyricView.setSeekToListener(seekToListener);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (lyricView != null)
        handler.post(updateRun);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        handler.removeCallbacks(updateRun);
    }

    public void startUpd() {
        handler.post(updateRun);
    }

    public void stopUpd() {
        handler.removeCallbacks(updateRun);
    }

    public void updateLyric() {
        handler.removeCallbacks(updateRun);
        lyricView.setLyricList(musicPlayer.getLyricList());
        handler.post(updateRun);
    }

    public void setMusicPlayer(MusicService.MusicPlayer musicPlayer) {
        this.musicPlayer = musicPlayer;
    }

    public void setSeekToListener(onLyricSeekToListener seekToListener) {
        this.seekToListener = seekToListener;
    }
}
