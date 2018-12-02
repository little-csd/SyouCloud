package com.example.asus.syoucloud.presenter;

import com.example.asus.syoucloud.Contract;
import com.example.asus.syoucloud.MusicService;
import com.example.asus.syoucloud.base.BasePresenter;
import com.example.asus.syoucloud.bean.MusicInfo;
import com.example.asus.syoucloud.data.DatabaseManager;
import com.example.asus.syoucloud.view.MusicListAdapter;

import java.util.List;

public class MusicShowPresenter extends BasePresenter<Contract.IMusicShowActivity>
        implements MusicListAdapter.onMusicClickListener, Contract.IMusicShowPresenter,
            DatabaseManager.DataChangeListener {

    private int albumId;
    private MusicService.MusicPlayer musicPlayer;

    public void initData(MusicService.MusicPlayer musicPlayer, int albumId) {
        this.musicPlayer = musicPlayer;
        this.albumId = albumId;
        DatabaseManager.getInstance().addDataChangeListener(this);
    }

    @Override
    public void detachView() {
        super.detachView();
        DatabaseManager.getInstance().removeDataChangeListener();
    }

    @Override
    public void onMusicClick(int position, List<MusicInfo> mList) {
        if (mList.size() == 0) return;
        musicPlayer.changeAlbum(albumId, position, mList);
    }

    @Override
    public void add(MusicInfo music) {
        mViewRef.get().add(music);
    }
}
