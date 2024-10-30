package com.pro.music.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.pro.music.databinding.ItemAdminArtistBinding;
import com.pro.music.listener.IOnAdminManagerArtistListener;
import com.pro.music.model.Artist;
import com.pro.music.utils.GlideUtils;

import java.util.List;

public class AdminArtistAdapter extends RecyclerView.Adapter<AdminArtistAdapter.AdminArtistViewHolder> {

    private final List<Artist> mListArtist;
    private final IOnAdminManagerArtistListener mListener;

    public AdminArtistAdapter(List<Artist> list, IOnAdminManagerArtistListener listener) {
        this.mListArtist = list;
        this.mListener = listener;
    }

    @NonNull
    @Override
    public AdminArtistViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemAdminArtistBinding binding = ItemAdminArtistBinding.inflate(LayoutInflater.from(parent.getContext()),
                parent, false);
        return new AdminArtistViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull AdminArtistViewHolder holder, int position) {
        Artist artist = mListArtist.get(position);
        if (artist == null) return;
        GlideUtils.loadUrl(artist.getImage(), holder.itemBinding.imgArtist);
        holder.itemBinding.tvName.setText(artist.getName());
        holder.itemBinding.imgEdit.setOnClickListener(v -> mListener.onClickUpdateArtist(artist));
        holder.itemBinding.imgDelete.setOnClickListener(v -> mListener.onClickDeleteArtist(artist));
        holder.itemBinding.layoutItem.setOnClickListener(v -> mListener.onClickDetailArtist(artist));
    }

    @Override
    public int getItemCount() {
        if (mListArtist != null) {
            return mListArtist.size();
        }
        return 0;
    }

    public static class AdminArtistViewHolder extends RecyclerView.ViewHolder {

        private final ItemAdminArtistBinding itemBinding;

        public AdminArtistViewHolder(@NonNull ItemAdminArtistBinding binding) {
            super(binding.getRoot());
            this.itemBinding = binding;
        }
    }
}
