package com.example.asus.syoucloud.internetSearch;

import com.example.Gson.Music.MusicResultItem;

import java.util.List;

public interface InternetSearchContract {
    interface IInternetSearchActivity {
        void setProgressBar(int type);

        void setHint(int type);

        void setRecycler(int type);

        void setResultList(List<MusicResultItem> list);
    }

    interface IInternetSearchPresenter {
        void click(String address);
    }
}
