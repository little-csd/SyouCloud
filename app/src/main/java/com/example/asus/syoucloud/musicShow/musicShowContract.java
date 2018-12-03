package com.example.asus.syoucloud.musicShow;

import com.example.asus.syoucloud.bean.MusicInfo;

public interface musicShowContract {

    interface IMusicShowPresenter {

    }

    interface IMusicShowActivity {
        void add(MusicInfo music);
    }
}
