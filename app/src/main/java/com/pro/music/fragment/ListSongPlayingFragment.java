package com.pro.music.fragment;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.pro.music.R;
import com.pro.music.adapter.SongPlayingAdapter;
import com.pro.music.constant.Constant;
import com.pro.music.constant.GlobalFunction;
import com.pro.music.databinding.FragmentListSongPlayingBinding;
import com.pro.music.listener.IOnClickSongPlayingItemListener;
import com.pro.music.model.Song;
import com.pro.music.service.MusicService;
// display playlist - remove - play the song on playlist
public class ListSongPlayingFragment extends Fragment {

    private FragmentListSongPlayingBinding mFragmentListSongPlayingBinding;
    private SongPlayingAdapter mSongPlayingAdapter;
    private final BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            updateStatusListSongPlaying();
        }
    };

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mFragmentListSongPlayingBinding = FragmentListSongPlayingBinding.inflate(inflater, container, false);

        //register broad cast receiver -> listen to event (change_listener) (from music service
        if (getActivity() != null) {
            LocalBroadcastManager.getInstance(getActivity()).registerReceiver(mBroadcastReceiver,
                    new IntentFilter(Constant.CHANGE_LISTENER));
        }
        displayListSongPlaying();

        return mFragmentListSongPlayingBinding.getRoot();
    }
    //initialize playlist with event
    private void displayListSongPlaying() {
        if (getActivity() == null || MusicService.mListSongPlaying == null) {
            return;
        }
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        mFragmentListSongPlayingBinding.rcvData.setLayoutManager(linearLayoutManager);

        //playlist from MusicService - listener into adapter
        mSongPlayingAdapter = new SongPlayingAdapter(MusicService.mListSongPlaying,
                new IOnClickSongPlayingItemListener() {
            @Override
            public void onClickItemSongPlaying(int position) {
                clickItemSongPlaying(position);
            }

            @Override
            public void onClickRemoveFromPlaylist(int position) {
                deleteSongFromPlaylist(position);
            }
        });
        mFragmentListSongPlayingBinding.rcvData.setAdapter(mSongPlayingAdapter);

        updateStatusListSongPlaying();
    }

    @SuppressLint("NotifyDataSetChanged")
    private void updateStatusListSongPlaying() {
        if (getActivity() == null || MusicService.mListSongPlaying == null || MusicService.mListSongPlaying.isEmpty()) {
            return;
        }
        for (int i = 0; i < MusicService.mListSongPlaying.size(); i++) {
            MusicService.mListSongPlaying.get(i).setPlaying(i == MusicService.mSongPosition);
        }
        mSongPlayingAdapter.notifyDataSetChanged();
    }

    private void clickItemSongPlaying(int position) {
        MusicService.isPlaying = false;
        GlobalFunction.startMusicService(getActivity(), Constant.PLAY, position);
    }

    @SuppressLint("NotifyDataSetChanged")
    private void deleteSongFromPlaylist(int position) {
        if (getActivity() == null) return;
        if (MusicService.mListSongPlaying == null || MusicService.mListSongPlaying.isEmpty()) {
            return;
        }
        Song songDelete = MusicService.mListSongPlaying.get(position);
        new AlertDialog.Builder(getActivity())
                .setTitle(songDelete.getTitle())
                .setMessage(getString(R.string.msg_confirm_delete))
                .setPositiveButton(getString(R.string.action_ok), (dialogInterface, i) -> {
                    if (MusicService.isSongPlaying(songDelete.getId())) {
                        GlobalFunction.showToastMessage(getActivity(),
                                getActivity().getString(R.string.msg_cannot_delete_song));
                    } else {
                        MusicService.deleteSongFromPlaylist(songDelete.getId());
                        if (mSongPlayingAdapter != null) mSongPlayingAdapter.notifyDataSetChanged();
                        GlobalFunction.showToastMessage(getActivity(),
                                getActivity().getString(R.string.msg_delete_song_from_playlist_success));
                    }
                })
                .setNegativeButton(getString(R.string.action_cancel), null)
                .show();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (getActivity() != null) {
            LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(mBroadcastReceiver);
        }
    }
}
