package com.example.asus.syoucloud.musicShow;

import com.example.asus.syoucloud.MusicService;
import com.example.asus.syoucloud.base.BasePresenter;
import com.example.asus.syoucloud.bean.MusicInfo;
import com.example.asus.syoucloud.data.DataRepository;

import java.util.List;

public class MusicShowPresenter extends BasePresenter<musicShowContract.IMusicShowActivity>
        implements MusicListAdapter.onMusicClickListener, musicShowContract.IMusicShowPresenter,
        DataRepository.DataChangeListener {

    private int albumId;
    private MusicService.MusicPlayer musicPlayer;

    public void initData(MusicService.MusicPlayer musicPlayer, int albumId) {
        this.musicPlayer = musicPlayer;
        this.albumId = albumId;
        DataRepository.getInstance().addDataChangeListener(this);
    }

    @Override
    public void detachView() {
        super.detachView();
        DataRepository.getInstance().removeDataChangeListener();
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

    @Override
    public void hasDownload(MusicInfo music) {
        if (albumId == -1) mViewRef.get().add(music);
    }
}
