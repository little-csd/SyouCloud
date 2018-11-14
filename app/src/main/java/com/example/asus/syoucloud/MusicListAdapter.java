package com.example.asus.syoucloud;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.asus.syoucloud.musicManager.MusicInfo;

import java.util.ArrayList;
import java.util.List;

public class MusicListAdapter extends RecyclerView.Adapter<MusicListAdapter.ViewHolder> {
    private List<MusicItem> musicList;

    public MusicListAdapter(List<MusicInfo> mList) {
        musicList = new ArrayList<>();
        for (int i = 0; i < mList.size(); i++) {
            MusicItem item = new MusicItem();
            MusicInfo info = mList.get(i);
            item.setArtist(info.getArtist());
            item.setTitle(info.getTitle());
            musicList.add(item);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.music_item, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MusicListAdapter.ViewHolder holder, int position) {
        MusicItem item = musicList.get(position);
        holder.musicTitle.setText(item.getTitle());
    }

    @Override
    public int getItemCount() {
        return musicList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView musicTitle;

        public ViewHolder(View view) {
            super(view);
            musicTitle = view.findViewById(R.id.music_item_title);
        }
    }
}
