package com.example.asus.syoucloud.internetSearch;

import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.View;

import com.example.Gson.Music.MusicResultItem;
import com.example.Gson.ParseHelper;
import com.example.asus.syoucloud.base.BasePresenter;
import com.example.asus.syoucloud.util.NetworkHelper;
import com.example.asus.syoucloud.util.ThreadPool;

import java.io.IOException;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class InternetSearchPresenter
        extends BasePresenter<InternetSearchContract.IInternetSearchActivity>
        implements InternetSearchContract.IInternetSearchPresenter, SearchResultAdapter.onMusicClick {

    private static final String TAG = "InternetSearchPresenter";

    private  List<MusicResultItem> musicList;

    @Override
    public void click(String address) {
        mViewRef.get().setRecycler(View.GONE);
        mViewRef.get().setHint(View.GONE);
        mViewRef.get().setProgressBar(View.VISIBLE);

        NetworkHelper.sendDownloadRequest(address, new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                runOnUi(() -> {
                    mViewRef.get().setProgressBar(View.GONE);
                    mViewRef.get().setHint(View.VISIBLE);
                    Log.i(TAG, "onFailure: " + e);
                });
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                String data = response.body().string();
                musicList = ParseHelper.parseMusicList(data);
                runOnUi(() -> {
                    mViewRef.get().setProgressBar(View.GONE);
                    if (musicList.isEmpty()) mViewRef.get().setHint(View.VISIBLE);
                    else {
                        mViewRef.get().setRecycler(View.VISIBLE);
                        mViewRef.get().setResultList(musicList);
                    }
                });
            }
        });
    }

    @Override
    public void onClick(int pos) {
        MusicResultItem music = musicList.get(pos);
        ThreadPool.getInstance().execute(() -> NetworkHelper.downloadMusic(music));
    }

    private void runOnUi(Runnable r) {
        if (Looper.myLooper() == Looper.getMainLooper()) r.run();
        else {
            Handler handler = new Handler(Looper.getMainLooper());
            handler.post(r);
        }
    }
}
