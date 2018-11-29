package com.example.asus.syoucloud.presenter;

import com.example.asus.syoucloud.Contract;
import com.example.asus.syoucloud.MusicService;
import com.example.asus.syoucloud.base.BasePresenter;
import com.example.asus.syoucloud.bean.MusicInfo;
import com.example.asus.syoucloud.view.MusicListAdapter;

import java.util.List;

public class MusicShowPresenter extends BasePresenter<Contract.IMusicShowActivity>
        implements MusicListAdapter.onMusicClickListener, Contract.IMusicShowPresenter {

    private int albumId;
    private MusicService.MusicPlayer musicPlayer;

    public void initData(MusicService.MusicPlayer musicPlayer, int albumId) {
        this.musicPlayer = musicPlayer;
        this.albumId = albumId;
    }

    @Override
    public void onMusicClick(int position, List<MusicInfo> mList) {
        if (mList.size() == 0) return;
        musicPlayer.changeAlbum(albumId, position, mList);
    }
}
