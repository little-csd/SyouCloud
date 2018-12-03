package com.example.asus.syoucloud.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.NonNull;
import android.util.Log;

import com.example.Gson.ParseHelper;
import com.example.asus.syoucloud.bean.Lyric;
import com.example.asus.syoucloud.bean.LyricItem;
import com.example.asus.syoucloud.bean.MixItem;
import com.example.asus.syoucloud.bean.MusicAndMixJoin;
import com.example.asus.syoucloud.bean.MusicInfo;
import com.example.asus.syoucloud.util.Constant;
import com.example.asus.syoucloud.util.HttpHelper;
import com.example.greendaodemo.db.DaoMaster;
import com.example.greendaodemo.db.DaoSession;
import com.example.greendaodemo.db.LyricDao;
import com.example.greendaodemo.db.LyricItemDao;
import com.example.greendaodemo.db.MixItemDao;
import com.example.greendaodemo.db.MusicAndMixJoinDao;
import com.example.greendaodemo.db.MusicInfoDao;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Response;

public class DataRepository {

    private static final String TAG = "DataRepository";

    private static final long DEFAULT_LYRIC = 1;
    public static DataRepository manager;
    private DaoSession daoSession;
    private MusicInfoDao musicInfoDao;
    private MixItemDao mixItemDao;
    private LyricDao lyricDao;
    private LyricItemDao lyricItemDao;
    private MusicAndMixJoinDao joinDao;
    private Context context;
    private MixLoader mixLoader;
    private MusicLoader musicLoader;
    private DataChangeListener dataChangeListener;
    private LyricDownloadListener downloadListener;
    private long beginTime = 0;
    private long maxJoin = 0;
    private long maxLrcItem = 0;

    private DataRepository() {

    }

    public static DataRepository getInstance() {
        if (manager == null) manager = new DataRepository();
        return manager;
    }

    public void initDatabase(Context context) {
        DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(context, Constant.DATABASE_NAME);
        SQLiteDatabase db = helper.getWritableDatabase();
        DaoMaster daoMaster = new DaoMaster(db);
        daoSession = daoMaster.newSession();
        this.context = context;

        initMixItem();
        initMusic();
        initLyric();
    }

    private void initMixItem() {
        mixItemDao = daoSession.getMixItemDao();
        List<MixItem> mixItems = mixItemDao.loadAll();
        if (mixItems == null) mixItems = new ArrayList<>();
        mixLoader = new MixLoader(mixItems);

        joinDao = daoSession.getMusicAndMixJoinDao();
        maxJoin = joinDao.count();
    }

    private void initMusic() {
        musicInfoDao = daoSession.getMusicInfoDao();
        List<MusicInfo> musicList = musicInfoDao.loadAll();
        musicLoader = new MusicLoader(musicList, context);
        if (musicLoader.getMusicList() == null || musicLoader.getMusicList().size() == 0)
            musicLoader.reSearch();
    }

    private void initLyric() {
        lyricDao = daoSession.getLyricDao();
        lyricItemDao = daoSession.getLyricItemDao();
        maxLrcItem = lyricItemDao.count();
        if (lyricDao.count() == 0) {
            LyricItem item = new LyricItem(0, "找不到歌词", "");
            item.setFromLyric(DEFAULT_LYRIC);
            lyricItemDao.insert(item);
            lyricDao.insert(new Lyric(DEFAULT_LYRIC));
        }
    }

    public void downloadLyric(MusicInfo music) {
        String address = "http://geci.me/api/lyric/" + music.getTitle();
        HttpHelper.sendLyricDownloadRequest(address, new okhttp3.Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                e.printStackTrace();
                Log.i(TAG, "onFailure: " + "download Lyric info");
                downloadListener.mkToast(Constant.DOWNLOAD_FAIL);
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                String responseData = response.body().string();
                String url = ParseHelper.parseLyricInfo(responseData);
                if (url.isEmpty()) {
                    Log.i(TAG, "onResponse: " + "lyric not found");
                    downloadListener.mkToast(Constant.DOWNLOAD_NOT_FOUND);
                    return;
                }
                downloadLyricReal(url, music.getId());
            }
        });
    }

    private void downloadLyricReal(String address, long id) {
        HttpHelper.sendLyricDownloadRequest(address, new okhttp3.Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                Log.i(TAG, "onFailure: " + "download lyric");
                e.printStackTrace();
                if (downloadListener != null) downloadListener.mkToast(Constant.DOWNLOAD_FAIL);
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                String data = response.body().string();
                List<LyricItem> lyric = ParseHelper.parseLyric(data);
                for (int i = 0; i < lyric.size(); i++) {
                    LyricItem item = lyric.get(i);
                    item.setId(++maxLrcItem);
                    item.setFromLyric(id);
                    lyricItemDao.insert(item);
                }
                lyricDao.insert(new Lyric(id));
                if (downloadListener != null) {
                    downloadListener.update();
                    downloadListener.mkToast(Constant.DOWNLOAD_SUCCESS);
                }
            }
        });
    }

    public List<LyricItem> searchLyric(long id) {
        Lyric lyric = lyricDao.load(id);
        if (lyric == null) return lyricDao.load(DEFAULT_LYRIC).getLyricItems();
        else return lyric.getLyricItems();
    }

    public void addMixItem(MixItem item) {
        mixItemDao.insert(item);
        mixLoader.addMix(item);
    }

    public void deleteMix(long albumId) {
        mixItemDao.deleteByKey(albumId);
        mixLoader.deleteMix(albumId);
    }

    public List<MixItem> getMixList() {
        return mixLoader.getMixList();
    }

    public String[] getMixTitleItems() {
        return mixLoader.getTitleItems();
    }

    public int getMixMax() {
        return mixLoader.getMixList().size();
    }

    public void addMusic(MusicInfo music) {
        musicInfoDao.insertOrReplace(music);
        musicLoader.addMusic(music);
    }

    public void addMusicToMix(MusicInfo music, int albumId) {
        MusicAndMixJoin join = new MusicAndMixJoin(++maxJoin, albumId, music.getId());
        mixItemDao.load((long) albumId).resetMusicList();
        joinDao.insert(join);
        if (dataChangeListener != null) dataChangeListener.add(music);
    }

    public void deleteMusicFromMix(long id, int albumId) {
        // TODO: 2018/12/1
    }

    public List<MusicInfo> getMusicList() {
        return musicLoader.getMusicList();
    }

    public List<MusicInfo> getMusicList(int albumId) {
        return mixItemDao.load((long) albumId).getMusicList();
    }

    public void addDataChangeListener(DataChangeListener listener) {
        this.dataChangeListener = listener;
    }

    public void removeDataChangeListener() {
        dataChangeListener = null;
    }

    public void addLyricDownloadListener(LyricDownloadListener listener) {
        this.downloadListener = listener;
    }

    public void removeLyricDownloadListener() {
        downloadListener = null;
    }

    public void saveBeginTime(long beginTime) {
        this.beginTime = beginTime;
    }

    public long getBeginTime() {
        return beginTime;
    }

    public interface DataChangeListener {
        void add(MusicInfo music);
    }

    public interface LyricDownloadListener {
        void mkToast(int type);

        void update();
    }
}