package com.example.asus.syoucloud.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.asus.syoucloud.MusicShowActivity;
import com.example.asus.syoucloud.R;

import org.litepal.LitePal;

import java.util.ArrayList;
import java.util.List;

public class MixListAdapter extends RecyclerView.Adapter<MixListAdapter.ViewHolder> {

    private List<MixItem> mixList;
    private Context mContext;

    public MixListAdapter(Context context) {
        mixList = LitePal.findAll(MixItem.class);
        if (mixList == null) mixList = new ArrayList<>();
        mContext = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.mix_item, viewGroup, false);
        final ViewHolder holder = new ViewHolder(view);
        holder.title.setOnClickListener(v -> {
            int position = holder.getAdapterPosition();
            int albumId = mixList.get(position).getAlbumId();
            String password = mixList.get(position).getPassword();
            if (password.equals("")) enterMix(albumId);
            else mkPasswordDialog(password, albumId);
        });
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull MixListAdapter.ViewHolder holder, int position) {
        MixItem item = mixList.get(position);
        holder.title.setText(item.getTitle());
    }

    @Override
    public int getItemCount() {
        return mixList.size();
    }

    public void add(MixItem item) {
        mixList.add(item);
        notifyItemInserted(mixList.size());
    }

    private void enterMix(int albumId) {
        Intent intent = new Intent(mContext, MusicShowActivity.class);
        intent.putExtra("albumId", albumId);
        mContext.startActivity(intent);
    }

    private void mkPasswordDialog(String password, int albumId) {
        LayoutInflater factory = LayoutInflater.from(mContext);
        final View view = factory.inflate(R.layout.password_enter_layout, null);
        final EditText editPassword = view.findViewById(R.id.mix_enter_password);
        new AlertDialog.Builder(mContext)
                .setIcon(R.drawable.mix_add_dialog_icon)
                .setTitle("Please input your password")
                .setView(view)
                .setPositiveButton("OK", (dialog, which) -> {
                    String mPassword = editPassword.getText().toString();
                    if (mPassword.equals(password)) enterMix(albumId);
                    else Toast.makeText(mContext, "password wrong", Toast.LENGTH_SHORT).show();
                })
                .show();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView title;
        ImageView albumImg;

        ViewHolder(View view) {
            super(view);
            title = view.findViewById(R.id.mix_title);
            albumImg = view.findViewById(R.id.mix_image);
        }
    }
}