package com.example.asus.syoucloud.view;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.asus.syoucloud.R;
import com.example.asus.syoucloud.bean.MusicInfo;

import java.util.List;

public class MusicListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int TYPE_IMAGE = 1;
    private static final int TYPE_CONTENT = 2;
    private List<MusicInfo> musicList;
    private onMusicClickListener listener;

    MusicListAdapter(List<MusicInfo> mList) {
        musicList = mList;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        if (i == TYPE_IMAGE)
            return new ImageViewHolder(LayoutInflater.from(viewGroup.getContext())
                    .inflate(R.layout.music_show_image_layout, viewGroup, false));
        else {
            ContentViewHolder viewHolder = new ContentViewHolder(LayoutInflater.from(viewGroup
                    .getContext()).inflate(R.layout.music_item, viewGroup, false));
            viewHolder.itemLayout.setOnClickListener(v -> listener.onMusicClick(
                    viewHolder.getAdapterPosition() - 1, musicList));
            return viewHolder;
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        int type = getItemViewType(position);
        if (type == TYPE_IMAGE) {
            ImageViewHolder viewHolder = (ImageViewHolder) holder;
            viewHolder.playLayout.setOnClickListener(v -> listener.onMusicClick(
                    position, musicList));
        } else {
            ContentViewHolder viewHolder = (ContentViewHolder) holder;
            MusicInfo item = musicList.get(position - 1);
            viewHolder.itemArtist.setText(item.getArtist());
            viewHolder.itemNum.setText(String.valueOf(position));
            viewHolder.itemTitle.setText(item.getTitle());
        }
    }

    @Override
    public int getItemCount() {
        return musicList.size() + 1;
    }

    @Override
    public int getItemViewType(int position) {
        if (position == 0) return TYPE_IMAGE;
        else return TYPE_CONTENT;
    }

    public void setOnMusicClickListener(onMusicClickListener listener) {
        this.listener = listener;
    }

    public interface onMusicClickListener {
        void onMusicClick(int position, List<MusicInfo> mList);
    }

    static class ContentViewHolder extends RecyclerView.ViewHolder {
        TextView itemTitle;
        TextView itemArtist;
        TextView itemNum;
        ImageView itemMore;
        LinearLayout itemLayout;

        ContentViewHolder(View view) {
            super(view);
            itemTitle = view.findViewById(R.id.show_item_title);
            itemArtist = view.findViewById(R.id.show_item_artist);
            itemMore = view.findViewById(R.id.music_item_more);
            itemLayout = view.findViewById(R.id.music_item_layout);
            itemNum = view.findViewById(R.id.show_item_num);
        }
    }

    static class ImageViewHolder extends RecyclerView.ViewHolder {

        TextView titleText;
        LinearLayout playLayout;

        ImageViewHolder(View view) {
            super(view);
            titleText = view.findViewById(R.id.music_show_album_name);
            playLayout = view.findViewById(R.id.music_show_play_layout);
        }
    }
}
