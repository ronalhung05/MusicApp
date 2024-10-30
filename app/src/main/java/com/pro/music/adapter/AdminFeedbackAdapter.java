package com.pro.music.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.pro.music.databinding.ItemAdminFeedbackBinding;
import com.pro.music.model.Feedback;

import java.util.List;

public class AdminFeedbackAdapter extends RecyclerView.Adapter<AdminFeedbackAdapter.AdminFeedbackViewHolder> {

    private final List<Feedback> mListFeedback;

    public AdminFeedbackAdapter(List<Feedback> list) {
        this.mListFeedback = list;
    }

    @NonNull
    @Override
    public AdminFeedbackViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemAdminFeedbackBinding binding = ItemAdminFeedbackBinding.inflate(LayoutInflater.from(parent.getContext()),
                parent, false);
        return new AdminFeedbackViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull AdminFeedbackViewHolder holder, int position) {
        Feedback feedback = mListFeedback.get(position);
        if (feedback == null) return;
        holder.mItemAdminFeedbackBinding.tvEmail.setText(feedback.getEmail());
        holder.mItemAdminFeedbackBinding.tvFeedback.setText(feedback.getComment());
    }

    @Override
    public int getItemCount() {
        if (mListFeedback != null) {
            return mListFeedback.size();
        }
        return 0;
    }

    public static class AdminFeedbackViewHolder extends RecyclerView.ViewHolder {

        private final ItemAdminFeedbackBinding mItemAdminFeedbackBinding;

        public AdminFeedbackViewHolder(@NonNull ItemAdminFeedbackBinding binding) {
            super(binding.getRoot());
            this.mItemAdminFeedbackBinding = binding;
        }
    }
}
