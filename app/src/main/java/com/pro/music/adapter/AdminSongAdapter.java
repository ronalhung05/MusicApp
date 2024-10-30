package com.pro.music.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.pro.music.databinding.ItemAdminSongBinding;
import com.pro.music.listener.IOnAdminManagerSongListener;
import com.pro.music.model.Song;
import com.pro.music.utils.GlideUtils;

import java.util.List;

public class AdminSongAdapter extends RecyclerView.Adapter<AdminSongAdapter.AdminSongViewHolder> {

    private final List<Song> mListSongs;
    public final IOnAdminManagerSongListener mListener;

    public AdminSongAdapter(List<Song> list, IOnAdminManagerSongListener listener) {
        this.mListSongs = list;
        this.mListener = listener;
    }

    @NonNull
    @Override
    public AdminSongViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemAdminSongBinding itemAdminSongBinding = ItemAdminSongBinding
                .inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new AdminSongViewHolder(itemAdminSongBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull AdminSongViewHolder holder, int position) {
        Song song = mListSongs.get(position);
        if (song == null) return;
        GlideUtils.loadUrl(song.getImage(), holder.mItemAdminSongBinding.imgSong);
        holder.mItemAdminSongBinding.tvName.setText(song.getTitle());
        holder.mItemAdminSongBinding.tvCategory.setText(song.getCategory());
        holder.mItemAdminSongBinding.tvArtist.setText(song.getArtist());
        if (song.isFeatured()) {
            holder.mItemAdminSongBinding.tvFeatured.setText("Yes");
        } else {
            holder.mItemAdminSongBinding.tvFeatured.setText("No");
        }

        holder.mItemAdminSongBinding.imgEdit.setOnClickListener(v -> mListener.onClickUpdateSong(song));
        holder.mItemAdminSongBinding.imgDelete.setOnClickListener(v -> mListener.onClickDeleteSong(song));
        holder.mItemAdminSongBinding.layoutItem.setOnClickListener(v -> mListener.onClickDetailSong(song));
    }

    @Override
    public int getItemCount() {
        return null == mListSongs ? 0 : mListSongs.size();
    }

    public static class AdminSongViewHolder extends RecyclerView.ViewHolder {

        private final ItemAdminSongBinding mItemAdminSongBinding;

        public AdminSongViewHolder(ItemAdminSongBinding itemAdminSongBinding) {
            super(itemAdminSongBinding.getRoot());
            this.mItemAdminSongBinding = itemAdminSongBinding;
        }
    }
}
