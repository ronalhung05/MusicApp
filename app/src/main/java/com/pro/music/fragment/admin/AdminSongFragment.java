package com.pro.music.fragment.admin;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.pro.music.MyApplication;
import com.pro.music.R;
import com.pro.music.activity.AdminAddSongActivity;
import com.pro.music.activity.PlayMusicActivity;
import com.pro.music.adapter.AdminSongAdapter;
import com.pro.music.constant.Constant;
import com.pro.music.constant.GlobalFunction;
import com.pro.music.databinding.FragmentAdminSongBinding;
import com.pro.music.listener.IOnAdminManagerSongListener;
import com.pro.music.model.Song;
import com.pro.music.service.MusicService;
import com.pro.music.utils.StringUtil;

import java.util.ArrayList;
import java.util.List;

public class AdminSongFragment extends Fragment {

    private FragmentAdminSongBinding binding;
    private List<Song> mListSong;
    private AdminSongAdapter mAdminSongAdapter;
    private ChildEventListener mChildEventListener;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentAdminSongBinding.inflate(inflater, container, false);

        initView();
        initListener();
        loadListSong("");

        return binding.getRoot();
    }

    private void initView() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        binding.rcvSong.setLayoutManager(linearLayoutManager);
        mListSong = new ArrayList<>();
        mAdminSongAdapter = new AdminSongAdapter(mListSong, new IOnAdminManagerSongListener() {
            @Override
            public void onClickUpdateSong(Song song) {
                onClickEditSong(song);
            }

            @Override
            public void onClickDeleteSong(Song song) {
                deleteSongItem(song);
            }

            @Override
            public void onClickDetailSong(Song song) {
                goToSongDetail(song);
            }
        });
        binding.rcvSong.setAdapter(mAdminSongAdapter);
        binding.rcvSong.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                if (dy > 0) {
                    binding.btnAddSong.hide();
                } else {
                    binding.btnAddSong.show();
                }
                super.onScrolled(recyclerView, dx, dy);
            }
        });
    }

    private void initListener() {
        binding.btnAddSong.setOnClickListener(v -> onClickAddSong());

        binding.imgSearch.setOnClickListener(view1 -> searchSong());

        binding.edtSearchName.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                searchSong();
                return true;
            }
            return false;
        });

        binding.edtSearchName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                String strKey = s.toString().trim();
                if (strKey.equals("") || strKey.length() == 0) {
                    searchSong();
                }
            }
        });
    }

    private void goToSongDetail(@NonNull Song song) {
        MusicService.clearListSongPlaying();
        MusicService.mListSongPlaying.add(song);
        MusicService.isPlaying = false;
        GlobalFunction.startMusicService(getActivity(), Constant.PLAY, 0);
        GlobalFunction.startActivity(getActivity(), PlayMusicActivity.class);
    }

    private void onClickAddSong() {
        GlobalFunction.startActivity(getActivity(), AdminAddSongActivity.class);
    }

    private void onClickEditSong(Song song) {
        Bundle bundle = new Bundle();
        bundle.putSerializable(Constant.KEY_INTENT_SONG_OBJECT, song);
        GlobalFunction.startActivity(getActivity(), AdminAddSongActivity.class, bundle);
    }

    private void deleteSongItem(Song song) {
        new AlertDialog.Builder(getActivity())
                .setTitle(getString(R.string.msg_delete_title))
                .setMessage(getString(R.string.msg_confirm_delete))
                .setPositiveButton(getString(R.string.action_ok), (dialogInterface, i) -> {
                    if (getActivity() == null) return;
                    MyApplication.get(getActivity()).getSongsDatabaseReference()
                            .child(String.valueOf(song.getId())).removeValue((error, ref) ->
                                    Toast.makeText(getActivity(),
                                            getString(R.string.msg_delete_song_successfully),
                                            Toast.LENGTH_SHORT).show());
                })
                .setNegativeButton(getString(R.string.action_cancel), null)
                .show();
    }

    private void searchSong() {
        String strKey = binding.edtSearchName.getText().toString().trim();
        resetListSong();
        if (getActivity() != null) {
            MyApplication.get(getActivity()).getSongsDatabaseReference()
                    .removeEventListener(mChildEventListener);
        }
        loadListSong(strKey);
        GlobalFunction.hideSoftKeyboard(getActivity());
    }

    private void resetListSong() {
        if (mListSong != null) {
            mListSong.clear();
        } else {
            mListSong = new ArrayList<>();
        }
    }

    public void loadListSong(String keyword) {
        if (getActivity() == null) return;
        mChildEventListener = new ChildEventListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, String s) {
                Song song = dataSnapshot.getValue(Song.class);
                if (song == null || mListSong == null) return;
                if (StringUtil.isEmpty(keyword)) {
                    mListSong.add(0, song);
                } else {
                    if (GlobalFunction.getTextSearch(song.getTitle()).toLowerCase().trim()
                            .contains(GlobalFunction.getTextSearch(keyword).toLowerCase().trim())) {
                        mListSong.add(0, song);
                    }
                }
                if (mAdminSongAdapter != null) mAdminSongAdapter.notifyDataSetChanged();
            }

            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, String s) {
                Song song = dataSnapshot.getValue(Song.class);
                if (song == null || mListSong == null || mListSong.isEmpty()) return;
                for (int i = 0; i < mListSong.size(); i++) {
                    if (song.getId() == mListSong.get(i).getId()) {
                        mListSong.set(i, song);
                        break;
                    }
                }
                if (mAdminSongAdapter != null) mAdminSongAdapter.notifyDataSetChanged();
            }

            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                Song song = dataSnapshot.getValue(Song.class);
                if (song == null || mListSong == null || mListSong.isEmpty()) return;
                for (Song songObject : mListSong) {
                    if (song.getId() == songObject.getId()) {
                        mListSong.remove(songObject);
                        break;
                    }
                }
                if (mAdminSongAdapter != null) mAdminSongAdapter.notifyDataSetChanged();
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, String s) {
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        };
        MyApplication.get(getActivity()).getSongsDatabaseReference()
                .addChildEventListener(mChildEventListener);
    }
}
