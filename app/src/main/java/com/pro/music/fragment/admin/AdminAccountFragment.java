package com.pro.music.fragment.admin;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.pro.music.R;
import com.pro.music.activity.AdminChangePasswordActivity;
import com.pro.music.activity.AdminStaffManagementActivity;
import com.pro.music.activity.SignInActivity;
import com.pro.music.constant.Constant;
import com.pro.music.constant.GlobalFunction;
import com.pro.music.databinding.FragmentAdminAccountBinding;
import com.pro.music.prefs.DataStoreManager;

public class AdminAccountFragment extends Fragment {

    private FragmentAdminAccountBinding binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentAdminAccountBinding.inflate(inflater, container, false);

        initUi();

        return binding.getRoot();
    }

    private void initUi() {
        binding.tvEmail.setText(DataStoreManager.getUser().getEmail());
        binding.tvChangePassword.setOnClickListener(v -> onClickChangePassword());
        binding.tvSignOut.setOnClickListener(v -> onClickSignOut());

        if(DataStoreManager.getUser().getEmail().contains(Constant.STAFF_EMAIL_FORMAT)) {
            binding.tvStaffManagement.setVisibility(View.GONE);
        } else {
            binding.tvStaffManagement.setOnClickListener(v -> onClickStaffManagement());
        }
    }

    private void onClickChangePassword() {
        GlobalFunction.startActivity(getActivity(), AdminChangePasswordActivity.class);
    }

    private void onClickStaffManagement(){
        GlobalFunction.startActivity(getActivity(), AdminStaffManagementActivity.class);
    }

    private void onClickSignOut() {
        if (getActivity() == null) return;
        FirebaseAuth.getInstance().signOut();
        DataStoreManager.setUser(null);
        GlobalFunction.startActivity(getActivity(), SignInActivity.class);
        getActivity().finishAffinity();
    }
}
