package com.pro.music.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.pro.music.R;
import com.pro.music.constant.GlobalFunction;
import com.pro.music.databinding.ItemSongPopularBinding;
import com.pro.music.listener.IOnClickSongItemListener;
import com.pro.music.model.Song;
import com.pro.music.utils.GlideUtils;

import java.util.List;

public class SongPopularAdapter extends RecyclerView.Adapter<SongPopularAdapter.SongPopularViewHolder> {

    private Context mContext;
    private final List<Song> mListSongs;
    public final IOnClickSongItemListener iOnClickSongItemListener;

    public SongPopularAdapter(Context context, List<Song> mListSongs,
                              IOnClickSongItemListener iOnClickSongItemListener) {
        this.mContext = context;
        this.mListSongs = mListSongs;
        this.iOnClickSongItemListener = iOnClickSongItemListener;
    }

    @NonNull
    @Override
    public SongPopularViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemSongPopularBinding itemSongPopularBinding = ItemSongPopularBinding.inflate(LayoutInflater
                .from(parent.getContext()), parent, false);
        return new SongPopularViewHolder(itemSongPopularBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull SongPopularViewHolder holder, int position) {
        Song song = mListSongs.get(position);
        if (song == null) {
            return;
        }
        GlideUtils.loadUrl(song.getImage(), holder.mItemSongPopularBinding.imgSong);
        holder.mItemSongPopularBinding.tvSongName.setText(song.getTitle());
        holder.mItemSongPopularBinding.tvArtist.setText(song.getArtist());
        String strListen = mContext.getString(R.string.label_listen);
        if (song.getCount() > 1) {
            strListen = mContext.getString(R.string.label_listens);
        }
        String strCountListen = song.getCount() + " " + strListen;
        holder.mItemSongPopularBinding.tvCountListen.setText(strCountListen);

        boolean isFavorite = GlobalFunction.isFavoriteSong(song);
        if (isFavorite) {
            holder.mItemSongPopularBinding.imgFavorite.setImageResource(R.drawable.ic_favorite);
        } else {
            holder.mItemSongPopularBinding.imgFavorite.setImageResource(R.drawable.ic_unfavorite);
        }

        holder.mItemSongPopularBinding.imgFavorite.setOnClickListener(v -> iOnClickSongItemListener.onClickFavoriteSong(song, !isFavorite));
        holder.mItemSongPopularBinding.imgMoreOption.setOnClickListener(v -> iOnClickSongItemListener.onClickMoreOptions(song));
        holder.mItemSongPopularBinding.layoutSongInfo.setOnClickListener(v -> iOnClickSongItemListener.onClickItemSong(song));
    }

    @Override
    public int getItemCount() {
        return null == mListSongs ? 0 : mListSongs.size();
    }

    public void release() {
        if (mContext != null) {
            mContext = null;
        }
    }

    public static class SongPopularViewHolder extends RecyclerView.ViewHolder {

        private final ItemSongPopularBinding mItemSongPopularBinding;

        public SongPopularViewHolder(ItemSongPopularBinding itemSongPopularBinding) {
            super(itemSongPopularBinding.getRoot());
            this.mItemSongPopularBinding = itemSongPopularBinding;
        }
    }
}
