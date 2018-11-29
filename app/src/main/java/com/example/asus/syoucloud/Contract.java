package com.example.asus.syoucloud;

import android.content.Context;
import android.widget.ImageView;

import com.example.asus.syoucloud.bean.Lyric;
import com.example.asus.syoucloud.bean.MusicInfo;

import java.util.List;

public interface Contract {
    interface IBottomLayoutFragment {
        void setTitle(String Msg);

        void setArtist(String Msg);

        ImageView getIgvView();

        void play();

        void pause();
    }

    interface IBottomLayoutPresenter {
        void start();

        void play();

        void click();
    }

    interface IDiskLayoutPresenter {
        void start();
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

        void setLyricList(List<Lyric> list);
    }

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

        void initData(MusicInfo music, int progress, int style, boolean isPlay);
    }

    interface IOverlayWindowManager {
        void showLyric(boolean isPlay);

        void removeLyric();

        void updateText(String Msg);

        void updateImage(boolean isPlay);

        void lock();

        void unLock();

        void update();

        void setPresenter(IOverlayWindowPresenter presenter);

        void initData(Context context);
    }

    interface IOverlayWindowPresenter {
        void play();

        void next();

        void last();

        void close();

        void lock();
    }

    interface IMusicShowPresenter {

    }

    interface IMusicShowActivity {

    }
}