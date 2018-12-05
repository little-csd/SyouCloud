package com.example.asus.syoucloud.musicPlay;

import android.widget.ImageView;

import com.example.asus.syoucloud.MusicService;
import com.example.asus.syoucloud.bean.LyricItem;
import com.example.asus.syoucloud.bean.MusicInfo;

import java.util.List;

public interface musicPlayContract {

    interface IMusicPlayActivityPresenter {
        void play();

        void next();

        void last();

        void start(MusicService.MusicPlayer musicPlayer);

        void changeStyle();

        void seekTo(int time);

        void startUpd();

        void stopUpd();

        void changeFragment();
    }

    interface IMusicPlayActivity {
        void play();

        void pause();

        void setStyle(int style);

        void toastStyle(int style);

        void addFragment(int type);

        void changeFragment(int type);

        void update(int time);

        void initData(MusicInfo music, int duration, int progress, int style, boolean isPlay);
    }

    interface IDiskLayoutPresenter {
        void start();

        void noticeAdd();

        void addToDatabase(int albumId);
    }

    interface IDiskLayoutFragment {
        ImageView getIgvView();

        void startAnim();

        void pauseAnim();
    }

    interface ILyricPresenter {
        void start();
    }

    interface ILyricFragment {
        void seekTo(int time);

        void setLyricList(List<LyricItem> list);

        void mkToast(int type);
    }
}
