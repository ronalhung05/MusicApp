package com.pro.music.adapter;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.pro.music.fragment.ListSongPlayingFragment;
import com.pro.music.fragment.PlaySongFragment;
//manage 2 fragments for play music activity
public class MusicViewPagerAdapter extends FragmentStateAdapter {
    //fragmentActivity must be not null
    public MusicViewPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }
    //2 fragment -> song playlist and play song
    @NonNull //this method always return non-null object -> ensure fragment not null
    @Override
    public Fragment createFragment(int position) {
        if (position == 0) { //first tab
            return new ListSongPlayingFragment();
        }
        return new PlaySongFragment();
    }

    @Override
    public int getItemCount() {
        return 2; //how many tabs
    }
}
