package com.example.asus.syoucloud.view;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.example.asus.syoucloud.bean.MixItem;
import com.example.asus.syoucloud.R;
import com.example.asus.syoucloud.bean.MusicInfo;
import com.example.asus.syoucloud.util.MusicLoader;
import com.example.asus.syoucloud.MusicService;
import com.example.asus.syoucloud.util.Constant;
import com.example.asus.syoucloud.util.RecyclerDivider;

import org.litepal.LitePal;

import java.util.ArrayList;
import java.util.List;

public class MusicShowActivity extends AppCompatActivity
        implements MusicListAdapter.onMusicClickListener {

    private MusicService.MusicPlayer musicPlayer;
    private Toolbar toolbar;

    private int albumId;

    private ServiceConnection connection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            musicPlayer = (MusicService.MusicPlayer) service;
            BottomLayoutFragment fragment = new BottomLayoutFragment();
//            fragment.setMusicPlayer(musicPlayer);
            getSupportFragmentManager()
                    .beginTransaction()
                    .add(R.id.music_show_bottom, fragment)
                    .commit();
            initView();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music_show);

        Intent bindIntent = new Intent(this, MusicService.class);
        bindService(bindIntent, connection, BIND_AUTO_CREATE);

        initToolbar();
        initView();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unbindService(connection);
    }

    private void initToolbar() {
        toolbar = findViewById(R.id.music_show_toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null)
            actionBar.setDisplayHomeAsUpEnabled(true);
    }

    private void initView() {
        Intent intent = getIntent();
        if (intent == null) return;
        albumId = intent.getIntExtra("albumId", -1);
        List<MusicInfo> musicList;
        if (albumId == -1) {
            musicList = MusicLoader.getInstance(getContentResolver(),
                    getApplicationContext()).getMusicList();
        } else {
            musicList = LitePal
                    .where("albumId=?", String.valueOf(albumId))
                    .findFirst(MixItem.class)
                    .getMusicList();
            if (musicList == null) musicList = new ArrayList<>();
        }

        MusicListAdapter adapter = new MusicListAdapter(musicList);
        adapter.setOnMusicClickListener(this);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        RecyclerView recyclerView = findViewById(R.id.music_show_recycler);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
        recyclerView.addItemDecoration(new RecyclerDivider(getApplicationContext(),
                LinearLayoutManager.VERTICAL, 1, Constant.ITEM_DECORATION));
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            private Drawable mDrawable = ContextCompat.getDrawable(getApplicationContext(),
                    R.drawable.music_show_toolbar_background);

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                int offset = recyclerView.computeVerticalScrollOffset();
                int toolBarHeight = toolbar.getMeasuredHeight();

                // todo why it will return -1
                if (layoutManager.findFirstVisibleItemPosition() == -1) return;

                if (offset >= toolBarHeight * 2.5 || layoutManager.findFirstVisibleItemPosition() > 0) {
                    mDrawable.setAlpha(255);
                    toolbar.setBackground(mDrawable);
                } else if (offset >= toolBarHeight) {
                    mDrawable.setAlpha((int) (255 * (offset - toolBarHeight) / (toolBarHeight * 1.5F)));
                    toolbar.setBackground(mDrawable);
                } else toolbar.setBackground(null);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.music_show_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
            case R.id.music_show_delete:
                break;
            case R.id.music_show_search:
                break;
            default:
        }
        return true;
    }

    @Override
    public void onMusicClick(int position, List<MusicInfo> mList) {
        if (mList.size() == 0) return;
        musicPlayer.changeAlbum(albumId, position, mList);
    }

}
