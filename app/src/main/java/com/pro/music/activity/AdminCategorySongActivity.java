package com.pro.music.activity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.pro.music.MyApplication;
import com.pro.music.R;
import com.pro.music.adapter.AdminSongAdapter;
import com.pro.music.constant.Constant;
import com.pro.music.constant.GlobalFunction;
import com.pro.music.databinding.ActivityAdminCategorySongBinding;
import com.pro.music.listener.IOnAdminManagerSongListener;
import com.pro.music.model.Category;
import com.pro.music.model.Song;
import com.pro.music.service.MusicService;

import java.util.ArrayList;
import java.util.List;

public class AdminCategorySongActivity extends BaseActivity {

    private ActivityAdminCategorySongBinding binding;
    private List<Song> mListSong;
    private AdminSongAdapter mAdminSongAdapter;
    private Category mCategory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAdminCategorySongBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        loadDataIntent();
        initToolbar();
        initView();
        loadListSong();
    }

    private void loadDataIntent() {
        Bundle bundleReceived = getIntent().getExtras();
        if (bundleReceived != null) {
            mCategory = (Category) bundleReceived.get(Constant.KEY_INTENT_CATEGORY_OBJECT);
        }
    }

    private void initToolbar() {
        binding.toolbar.imgLeft.setImageResource(R.drawable.ic_back_white);
        binding.toolbar.layoutPlayAll.setVisibility(View.GONE);
        binding.toolbar.imgLeft.setOnClickListener(v -> onBackPressed());
        binding.toolbar.tvTitle.setText(mCategory.getName());
    }

    private void initView() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
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
    }

    private void goToSongDetail(@NonNull Song song) {
        MusicService.clearListSongPlaying();
        MusicService.mListSongPlaying.add(song);
        MusicService.isPlaying = false;
        GlobalFunction.startMusicService(this, Constant.PLAY, 0);
        GlobalFunction.startActivity(this, PlayMusicActivity.class);
    }

    private void onClickEditSong(Song song) {
        Bundle bundle = new Bundle();
        bundle.putSerializable(Constant.KEY_INTENT_SONG_OBJECT, song);
        GlobalFunction.startActivity(this, AdminAddSongActivity.class, bundle);
    }

    private void deleteSongItem(Song song) {
        new AlertDialog.Builder(this)
                .setTitle(getString(R.string.msg_delete_title))
                .setMessage(getString(R.string.msg_confirm_delete))
                .setPositiveButton(getString(R.string.action_ok), (dialogInterface, i)
                        -> MyApplication.get(this).getSongsDatabaseReference()
                        .child(String.valueOf(song.getId())).removeValue((error, ref) ->
                                Toast.makeText(this,
                                        getString(R.string.msg_delete_song_successfully),
                                        Toast.LENGTH_SHORT).show()))
                .setNegativeButton(getString(R.string.action_cancel), null)
                .show();
    }

    private void resetListSong() {
        if (mListSong != null) {
            mListSong.clear();
        } else {
            mListSong = new ArrayList<>();
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    public void loadListSong() {
        MyApplication.get(this).getSongsDatabaseReference()
                .orderByChild("categoryId").equalTo(mCategory.getId())
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        resetListSong();
                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                            Song song = dataSnapshot.getValue(Song.class);
                            if (song == null) return;
                            mListSong.add(0, song);
                        }
                        if (mAdminSongAdapter != null) mAdminSongAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                    }
                });
    }
}