package com.example.asus.syoucloud.internetSearch;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.Gson.Music.MusicResultItem;
import com.example.asus.syoucloud.R;

import java.util.List;

public class SearchResultAdapter extends RecyclerView.Adapter<SearchResultAdapter.ViewHolder> {

    private List<MusicResultItem> musicList;
    private onMusicClick musicClick;

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.search_result_item, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        MusicResultItem music = musicList.get(i);
        viewHolder.resultArtist.setText(music.getSinger());
        viewHolder.resultTitle.setText(music.getName());
        viewHolder.resultNum.setText(String.valueOf(i + 1));
        viewHolder.downloadImg.setOnClickListener(v -> {
            if (musicClick != null) musicClick.onClick(i);
        });
    }

    @Override
    public int getItemCount() {
        return musicList.size();
    }

    public void setMusicList(List<MusicResultItem> musicList) {
        this.musicList = musicList;
        notifyDataSetChanged();
    }

    public void setMusicClick(onMusicClick musicClick) {
        this.musicClick = musicClick;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {

        TextView resultNum;
        TextView resultTitle;
        TextView resultArtist;
        ImageView downloadImg;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            downloadImg = itemView.findViewById(R.id.search_result_download);
            resultNum = itemView.findViewById(R.id.search_result_num);
            resultTitle = itemView.findViewById(R.id.search_result_title);
            resultArtist = itemView.findViewById(R.id.search_result_artist);
        }
    }

    public interface onMusicClick {
        void onClick(int pos);
    }
}