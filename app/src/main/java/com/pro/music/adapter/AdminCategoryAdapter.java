package com.pro.music.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.pro.music.databinding.ItemAdminCategoryBinding;
import com.pro.music.listener.IOnAdminManagerCategoryListener;
import com.pro.music.model.Category;
import com.pro.music.utils.GlideUtils;

import java.util.List;

public class AdminCategoryAdapter extends RecyclerView.Adapter<AdminCategoryAdapter.AdminCategoryViewHolder> {

    private final List<Category> mListCategory;
    private final IOnAdminManagerCategoryListener mListener;

    public AdminCategoryAdapter(List<Category> list, IOnAdminManagerCategoryListener listener) {
        this.mListCategory = list;
        this.mListener = listener;
    }

    @NonNull
    @Override
    public AdminCategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemAdminCategoryBinding binding = ItemAdminCategoryBinding.inflate(LayoutInflater.from(parent.getContext()),
                parent, false);
        return new AdminCategoryViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull AdminCategoryViewHolder holder, int position) {
        Category category = mListCategory.get(position);
        if (category == null) return;
        GlideUtils.loadUrl(category.getImage(), holder.itemBinding.imgCategory);
        holder.itemBinding.tvName.setText(category.getName());
        holder.itemBinding.imgEdit.setOnClickListener(v -> mListener.onClickUpdateCategory(category));
        holder.itemBinding.imgDelete.setOnClickListener(v -> mListener.onClickDeleteCategory(category));
        holder.itemBinding.layoutItem.setOnClickListener(v -> mListener.onClickDetailCategory(category));
    }

    @Override
    public int getItemCount() {
        if (mListCategory != null) {
            return mListCategory.size();
        }
        return 0;
    }

    public static class AdminCategoryViewHolder extends RecyclerView.ViewHolder {

        private final ItemAdminCategoryBinding itemBinding;

        public AdminCategoryViewHolder(@NonNull ItemAdminCategoryBinding binding) {
            super(binding.getRoot());
            this.itemBinding = binding;
        }
    }
}
