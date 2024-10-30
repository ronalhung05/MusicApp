package com.pro.music.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.pro.music.fragment.admin.AdminAccountFragment;
import com.pro.music.fragment.admin.AdminArtistFragment;
import com.pro.music.fragment.admin.AdminCategoryFragment;
import com.pro.music.fragment.admin.AdminFeedbackFragment;
import com.pro.music.fragment.admin.AdminSongFragment;

public class AdminViewPagerAdapter extends FragmentStateAdapter {

    public AdminViewPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 1:
                return new AdminArtistFragment();

            case 2:
                return new AdminSongFragment();

            case 3:
                return new AdminFeedbackFragment();

            case 4:
                return new AdminAccountFragment();

            default:
                return new AdminCategoryFragment();
        }
    }

    @Override
    public int getItemCount() {
        return 5;
    }
}
