package com.pro.music.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.pro.music.R;
import com.pro.music.databinding.ItemSongPlayingBinding;
import com.pro.music.listener.IOnClickSongPlayingItemListener;
import com.pro.music.model.Song;
import com.pro.music.utils.GlideUtils;

import java.util.List;

public class SongPlayingAdapter extends RecyclerView.Adapter<SongPlayingAdapter.SongPlayingViewHolder> {

    private final List<Song> mListSongs;
    public final IOnClickSongPlayingItemListener iOnClickSongPlayingItemListener;

    public SongPlayingAdapter(List<Song> mListSongs, IOnClickSongPlayingItemListener iOnClickSongPlayingItemListener) {
        this.mListSongs = mListSongs;
        this.iOnClickSongPlayingItemListener = iOnClickSongPlayingItemListener;
    }

    @NonNull
    @Override
    public SongPlayingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemSongPlayingBinding itemSongPlayingBinding = ItemSongPlayingBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new SongPlayingViewHolder(itemSongPlayingBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull SongPlayingViewHolder holder, int position) {
        Song song = mListSongs.get(position);
        if (song == null) {
            return;
        }
        if (song.isPlaying()) {
            holder.mItemSongPlayingBinding.layoutItem.setBackgroundResource(R.color.background_bottom);
            holder.mItemSongPlayingBinding.imgAction.setImageResource(R.drawable.ic_play_black);
            holder.mItemSongPlayingBinding.imgAction.setOnClickListener(null);
        } else {
            holder.mItemSongPlayingBinding.layoutItem.setBackgroundResource(R.color.white);
            holder.mItemSongPlayingBinding.imgAction.setImageResource(R.drawable.ic_delete_black);
            holder.mItemSongPlayingBinding.imgAction.setOnClickListener(
                    v -> iOnClickSongPlayingItemListener.onClickRemoveFromPlaylist(holder.getAdapterPosition()));
        }
        if (song.isPriority()) {
            holder.mItemSongPlayingBinding.tvPrioritized.setVisibility(View.VISIBLE);
        } else {
            holder.mItemSongPlayingBinding.tvPrioritized.setVisibility(View.GONE);
        }
        GlideUtils.loadUrl(song.getImage(), holder.mItemSongPlayingBinding.imgSong);
        holder.mItemSongPlayingBinding.tvSongName.setText(song.getTitle());
        holder.mItemSongPlayingBinding.tvArtist.setText(song.getArtist());

        holder.mItemSongPlayingBinding.layoutSong.setOnClickListener(v
                -> iOnClickSongPlayingItemListener.onClickItemSongPlaying(holder.getAdapterPosition()));
    }

    @Override
    public int getItemCount() {
        return null == mListSongs ? 0 : mListSongs.size();
    }

    public static class SongPlayingViewHolder extends RecyclerView.ViewHolder {

        private final ItemSongPlayingBinding mItemSongPlayingBinding;

        public SongPlayingViewHolder(ItemSongPlayingBinding itemSongPlayingBinding) {
            super(itemSongPlayingBinding.getRoot());
            this.mItemSongPlayingBinding = itemSongPlayingBinding;
        }
    }
}
