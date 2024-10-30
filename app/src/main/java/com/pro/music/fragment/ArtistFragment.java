package com.pro.music.fragment;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.pro.music.MyApplication;
import com.pro.music.R;
import com.pro.music.activity.MainActivity;
import com.pro.music.adapter.ArtistVerticalAdapter;
import com.pro.music.constant.Constant;
import com.pro.music.constant.GlobalFunction;
import com.pro.music.databinding.FragmentArtistBinding;
import com.pro.music.model.Artist;

import java.util.ArrayList;
import java.util.List;

public class ArtistFragment extends Fragment {

    private FragmentArtistBinding mFragmentArtistBinding;
    private List<Artist> mListArtist;
    private ArtistVerticalAdapter mArtistVerticalAdapter;
    public boolean mIsFromMenuLeft;

    public static ArtistFragment newInstance(boolean isFromMenuLeft) {
        ArtistFragment fragment = new ArtistFragment();
        Bundle bundle = new Bundle();
        bundle.putBoolean(Constant.IS_FROM_MENU_LEFT, isFromMenuLeft);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        mFragmentArtistBinding = FragmentArtistBinding.inflate(inflater, container, false);

        getDataIntent();
        initUi();
        getListAllArtist();

        return mFragmentArtistBinding.getRoot();
    }

    private void getDataIntent() {
        Bundle bundle = getArguments();
        if (bundle == null) return;
        mIsFromMenuLeft = bundle.getBoolean(Constant.IS_FROM_MENU_LEFT);
    }

    private void initUi() {
        if (getActivity() == null) return;
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(), 2);
        mFragmentArtistBinding.rcvData.setLayoutManager(gridLayoutManager);

        mListArtist = new ArrayList<>();
        mArtistVerticalAdapter = new ArtistVerticalAdapter(mListArtist, artist -> {
            MainActivity mainActivity = (MainActivity) getActivity();
            if (mainActivity != null) {
                mainActivity.clickOpenSongsByArtist(artist);
            }
        });
        mFragmentArtistBinding.rcvData.setAdapter(mArtistVerticalAdapter);
    }
    @SuppressLint("NotifyDataSetChanged")
    private void getListAllArtist() {
        if (getActivity() == null) return;
        MyApplication.get(getActivity()).getArtistDatabaseReference()
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (mListArtist == null) {
                            mListArtist = new ArrayList<>();
                        } else {
                            mListArtist.clear();
                        }
                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                            Artist artist = dataSnapshot.getValue(Artist.class);
                            if (artist == null) return;
                            mListArtist.add(0, artist);
                        }
                        if (mArtistVerticalAdapter != null) mArtistVerticalAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        GlobalFunction.showToastMessage(getActivity(), getString(R.string.msg_get_date_error));
                    }
                });
    }
}
