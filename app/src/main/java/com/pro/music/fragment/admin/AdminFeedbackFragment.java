package com.pro.music.fragment.admin;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.pro.music.MyApplication;
import com.pro.music.adapter.AdminFeedbackAdapter;
import com.pro.music.databinding.FragmentAdminFeedbackBinding;
import com.pro.music.model.Feedback;

import java.util.ArrayList;
import java.util.List;

public class AdminFeedbackFragment extends Fragment {

    private FragmentAdminFeedbackBinding binding;
    private List<Feedback> mListFeedback;
    private AdminFeedbackAdapter mFeedbackAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentAdminFeedbackBinding.inflate(inflater, container, false);

        initView();
        loadListFeedback();
        return binding.getRoot();
    }

    private void initView() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        binding.rcvFeedback.setLayoutManager(linearLayoutManager);
        mListFeedback = new ArrayList<>();
        mFeedbackAdapter = new AdminFeedbackAdapter(mListFeedback);
        binding.rcvFeedback.setAdapter(mFeedbackAdapter);
    }

    @SuppressLint("NotifyDataSetChanged")
    public void loadListFeedback() {
        if (getActivity() == null) return;
        MyApplication.get(getActivity()).getFeedbackDatabaseReference()
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        clearListFeedback();
                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                            Feedback feedback = dataSnapshot.getValue(Feedback.class);
                            if (feedback != null) {
                                mListFeedback.add(0, feedback);
                            }
                        }
                        if (mFeedbackAdapter != null) mFeedbackAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                    }
                });
    }

    private void clearListFeedback() {
        if (mListFeedback != null) {
            mListFeedback.clear();
        } else {
            mListFeedback = new ArrayList<>();
        }
    }
}
