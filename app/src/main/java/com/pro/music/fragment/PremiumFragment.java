package com.pro.music.fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.pro.music.MyApplication;
import com.pro.music.R;
import com.pro.music.activity.MainActivity;
import com.pro.music.constant.Constant;
import com.pro.music.constant.GlobalFunction;
import com.pro.music.databinding.FragmentPremiumBinding;
import com.pro.music.model.Premium;
import com.pro.music.model.User;
import com.pro.music.prefs.DataStoreManager;

public class PremiumFragment extends Fragment {

    private FragmentPremiumBinding mFragmentPremiumBinding;

    public static PremiumFragment newInstance(boolean isFromMenuLeft) {
        PremiumFragment fragment = new PremiumFragment();
        Bundle bundle = new Bundle();
        bundle.putBoolean(Constant.IS_FROM_MENU_LEFT, isFromMenuLeft);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        mFragmentPremiumBinding = FragmentPremiumBinding.inflate(inflater, container, false);
        
        mFragmentPremiumBinding.btnUpgradePremium.setOnClickListener(v -> onClickUpgradePremium());

        return mFragmentPremiumBinding.getRoot();
    }

    private void onClickUpgradePremium() {
        if (getActivity() == null) {
            return;
        }
        MainActivity activity = (MainActivity) getActivity();

        activity.showProgressDialog(true);
        Premium premium = new Premium(DataStoreManager.getUser().getEmail(), true);
        MyApplication.get(getActivity()).getPremiumDatabaseReference()
                .child(String.valueOf(System.currentTimeMillis()))
                .setValue(premium, (databaseError, databaseReference) -> {
                    activity.showProgressDialog(false);
                    User user = DataStoreManager.getUser();
                    user.setPremium(true);
                    DataStoreManager.setUser(user);
                    sendUpgradeSuccess();
                });
    }

    private void sendUpgradeSuccess() {
        GlobalFunction.showToastMessage(getActivity(), getString(R.string.msg_upgrade_premium_success));
        if (getActivity() != null) {
            getActivity().finish();
            getActivity().startActivity(getActivity().getIntent());
        }
    }
}