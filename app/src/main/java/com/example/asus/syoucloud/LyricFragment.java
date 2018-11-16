package com.example.asus.syoucloud;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.asus.syoucloud.musicManager.LrcHandle;
import com.example.asus.syoucloud.musicManager.LyricView;

import java.util.List;

public class LyricFragment extends Fragment {

    private static final String TAG = "LyricFragment";
    private int progress = 0;
    private LyricView lyricView;
    private Handler handler = new Handler();
    private int index = 0;

    private List<Integer> timeList;

    private Runnable updateRun = new Runnable() {
        @Override
        public void run() {
            if (index < timeList.size() - 1) {
                handler.postDelayed(this, timeList.get(index+1) - timeList.get(index));
                index++;
            }
            lyricView.invalidate();
        }
    };

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.lyric_fragment, container, false);
        lyricView = view.findViewById(R.id.lyric_view);
        return view;
    }

    public void setCurrentTime(int progress) {
        this.progress = progress;
    }

    @Override
    public void onResume() {
        super.onResume();
        LrcHandle lrcHandle = new LrcHandle();
        lrcHandle.readLRC("/storage/emulated/0/Download/七里香.lrc");
        timeList = lrcHandle.getTimeList();
        for (int i = 0; i < timeList.size(); i++)
            if (timeList.get(i) > progress) {
                index = i;
                break;
            }
        index--;
        if (index < 0) index = 0;
        lyricView.setIndex(index++);
        handler.postDelayed(updateRun, timeList.get(index) - progress);
    }
}
