package com.example.asus.syoucloud.internetSearch;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.Gson.Music.MusicResultItem;
import com.example.asus.syoucloud.R;
import com.example.asus.syoucloud.base.BaseActivity;
import com.example.asus.syoucloud.util.Constant;
import com.example.asus.syoucloud.util.RecyclerDivider;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class InternetSearchActivity
        extends BaseActivity<InternetSearchContract.IInternetSearchActivity, InternetSearchPresenter>
        implements InternetSearchContract.IInternetSearchActivity {

    @BindView(R.id.search_input)
    EditText searchInput;
    @BindView(R.id.search_check)
    ImageView searchCheck;
    @BindView(R.id.search_progressbar)
    ProgressBar progressbar;
    @BindView(R.id.search_hint)
    TextView searchHint;
    @BindView(R.id.search_result)
    RecyclerView searchResult;

    private SearchResultAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_internet_search);
        ButterKnife.bind(this);

        initToolbar();
        initView();
    }

    @Override
    protected InternetSearchPresenter createPresenter() {
        return new InternetSearchPresenter();
    }

    private void initToolbar() {
        Toolbar toolbar = findViewById(R.id.search_toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null)
            actionBar.setDisplayHomeAsUpEnabled(true);
    }

    public void initView() {
        progressbar.setVisibility(View.GONE);
        searchHint.setVisibility(View.GONE);
        searchResult.setVisibility(View.GONE);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        searchResult.setLayoutManager(linearLayoutManager);
        searchResult.addItemDecoration(new RecyclerDivider(getApplicationContext(),
                LinearLayoutManager.VERTICAL, 1, Constant.ITEM_DECORATION));
        adapter = new SearchResultAdapter();
        adapter.setMusicClick(mPresenter);
        searchResult.setAdapter(adapter);
    }

    @Override
    public void setProgressBar(int type) {
        progressbar.setVisibility(type);
    }

    @Override
    public void setHint(int type) {
        searchHint.setVisibility(type);
    }

    @Override
    public void setRecycler(int type) {
        searchResult.setVisibility(type);
    }

    @Override
    public void setResultList(List<MusicResultItem> list) {
        adapter.setMusicList(list);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home)
            finish();
        return false;
    }

    @OnClick(R.id.search_check)
    public void onViewClicked() {
        String address = Constant.SEARCH_MUSIC + searchInput.getText().toString();
        mPresenter.click(address);
    }
}
