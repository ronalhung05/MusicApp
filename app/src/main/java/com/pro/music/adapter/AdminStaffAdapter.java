package com.pro.music.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.pro.music.databinding.ItemAdminStaffBinding;
import com.pro.music.listener.IOnAdminManagerStaffListener;
import com.pro.music.model.Staff;

import java.util.List;

public class AdminStaffAdapter extends RecyclerView.Adapter<AdminStaffAdapter.AdminStaffViewHolder>{
    private final List<Staff> mListStaff;
    private final IOnAdminManagerStaffListener mListener;

    public AdminStaffAdapter(List<Staff> mListStaff, IOnAdminManagerStaffListener mListener) {
        this.mListStaff = mListStaff;
        this.mListener = mListener;
    }

    @NonNull
    @Override
    public AdminStaffAdapter.AdminStaffViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemAdminStaffBinding binding = ItemAdminStaffBinding.inflate(LayoutInflater.from(parent.getContext()),
                parent, false);

        return new AdminStaffViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull AdminStaffViewHolder holder, int position) {
        Staff staff = mListStaff.get(position);
        if (staff == null) return;
        holder.itemBinding.tvName.setText(staff.getEmail());
        holder.itemBinding.imgEdit.setOnClickListener(v -> mListener.onClickUpdateStaff(staff));
        holder.itemBinding.imgDelete.setOnClickListener(v -> mListener.onClickDeleteStaff(staff));
        holder.itemBinding.layoutItem.setOnClickListener(v -> mListener.onClickDetailStaff(staff));
    }

    @Override
    public int getItemCount() {
        if (mListStaff != null) {
            return mListStaff.size();
        }
        return 0;
    }

    public static class AdminStaffViewHolder extends RecyclerView.ViewHolder {

        private final ItemAdminStaffBinding itemBinding;

        public AdminStaffViewHolder(@NonNull ItemAdminStaffBinding binding) {
            super(binding.getRoot());
            this.itemBinding = binding;
        }
    }
}
