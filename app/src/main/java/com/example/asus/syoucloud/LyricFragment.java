package com.example.asus.syoucloud;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.asus.syoucloud.musicManager.LyricView;
import com.example.asus.syoucloud.musicManager.MusicService;

public class LyricFragment extends Fragment {

    private static final String TAG = "LyricFragment";
    private MusicService.MusicPlayer musicPlayer;
    private LyricView lyricView;
    private Handler handler = new Handler();

    private Runnable updateRun = new Runnable() {
        @Override
        public void run() {
            handler.postDelayed(this, 100);
            lyricView.invalidate();
        }
    };

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.lyric_fragment, container, false);
        lyricView = view.findViewById(R.id.lyric_view);
        lyricView.setMusicPlayer(musicPlayer);
        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        handler.post(updateRun);
    }

    public void startUpd() {
        handler.post(updateRun);
    }

    public void stopUpd() {
        handler.removeCallbacks(updateRun);
    }

    public void setMusicPlayer(MusicService.MusicPlayer musicPlayer) {
        this.musicPlayer = musicPlayer;
    }

    public void seekTo(int progress) {
        lyricView.seekTo(progress * 1000);
    }
}
