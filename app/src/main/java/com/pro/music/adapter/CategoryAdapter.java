package com.pro.music.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.pro.music.databinding.ItemCategoryBinding;
import com.pro.music.listener.IOnClickCategoryItemListener;
import com.pro.music.model.Category;
import com.pro.music.utils.GlideUtils;

import java.util.List;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder> {

    private final List<Category> mListCategory;
    public final IOnClickCategoryItemListener iOnClickCategoryItemListener;

    public CategoryAdapter(List<Category> list, IOnClickCategoryItemListener listener) {
        this.mListCategory = list;
        this.iOnClickCategoryItemListener = listener;
    }

    @NonNull
    @Override
    public CategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemCategoryBinding itemCategoryBinding = ItemCategoryBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new CategoryViewHolder(itemCategoryBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryViewHolder holder, int position) {
        Category category = mListCategory.get(position);
        if (category == null) {
            return;
        }
        GlideUtils.loadUrl(category.getImage(), holder.mItemCategoryBinding.imgCategory);
        holder.mItemCategoryBinding.tvCategory.setText(category.getName());

        holder.mItemCategoryBinding.layoutItem.setOnClickListener(v -> iOnClickCategoryItemListener.onClickItemCategory(category));
    }

    @Override
    public int getItemCount() {
        return null == mListCategory ? 0 : mListCategory.size();
    }

    public static class CategoryViewHolder extends RecyclerView.ViewHolder {

        private final ItemCategoryBinding mItemCategoryBinding;

        public CategoryViewHolder(ItemCategoryBinding itemCategoryBinding) {
            super(itemCategoryBinding.getRoot());
            this.mItemCategoryBinding = itemCategoryBinding;
        }
    }
}
