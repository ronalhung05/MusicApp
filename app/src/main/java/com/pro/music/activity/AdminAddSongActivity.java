package com.pro.music.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.pro.music.MyApplication;
import com.pro.music.R;
import com.pro.music.adapter.AdminSelectAdapter;
import com.pro.music.constant.Constant;
import com.pro.music.constant.GlobalFunction;
import com.pro.music.databinding.ActivityAdminAddSongBinding;
import com.pro.music.model.Artist;
import com.pro.music.model.Category;
import com.pro.music.model.SelectObject;
import com.pro.music.model.Song;
import com.pro.music.utils.StringUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AdminAddSongActivity extends BaseActivity {

    private ActivityAdminAddSongBinding binding;
    private boolean isUpdate;
    private Song mSong;
    private SelectObject mCategorySelected;
    private SelectObject mArtistSelected;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAdminAddSongBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        loadDataIntent();
        initToolbar();
        initView();

        binding.btnAddOrEdit.setOnClickListener(v -> addOrEditSong());
    }

    private void loadDataIntent() {
        Bundle bundleReceived = getIntent().getExtras();
        if (bundleReceived != null) {
            isUpdate = true;
            mSong = (Song) bundleReceived.get(Constant.KEY_INTENT_SONG_OBJECT);
        }
    }

    private void initToolbar() {
        binding.toolbar.imgLeft.setImageResource(R.drawable.ic_back_white);
        binding.toolbar.layoutPlayAll.setVisibility(View.GONE);
        binding.toolbar.imgLeft.setOnClickListener(v -> onBackPressed());
    }

    private void initView() {
        if (isUpdate) {
            binding.toolbar.tvTitle.setText(getString(R.string.label_update_song));
            binding.btnAddOrEdit.setText(getString(R.string.action_edit));

            binding.edtName.setText(mSong.getTitle());
            binding.edtImage.setText(mSong.getImage());
            binding.edtLink.setText(mSong.getUrl());
            binding.chbFeatured.setChecked(mSong.isFeatured());
        } else {
            binding.toolbar.tvTitle.setText(getString(R.string.label_add_song));
            binding.btnAddOrEdit.setText(getString(R.string.action_add));
        }
        loadListCategory();
        loadListArtist();
    }

    private void loadListCategory() {
        MyApplication.get(this).getCategoryDatabaseReference()
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        List<SelectObject> list = new ArrayList<>();
                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                            Category category = dataSnapshot.getValue(Category.class);
                            if (category == null) return;
                            list.add(0, new SelectObject(category.getId(), category.getName()));
                        }
                        AdminSelectAdapter adapter = new AdminSelectAdapter(AdminAddSongActivity.this,
                                R.layout.item_choose_option, list);
                        binding.spnCategory.setAdapter(adapter);
                        binding.spnCategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                mCategorySelected = adapter.getItem(position);
                            }

                            @Override
                            public void onNothingSelected(AdapterView<?> parent) {}
                        });

                        if (mSong != null && mSong.getCategoryId() > 0) {
                            binding.spnCategory.setSelection(getPositionSelected(list, mSong.getCategoryId()));
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {}
                });
    }

    private void loadListArtist() {
        MyApplication.get(this).getArtistDatabaseReference()
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        List<SelectObject> list = new ArrayList<>();
                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                            Artist artist = dataSnapshot.getValue(Artist.class);
                            if (artist == null) return;
                            list.add(0, new SelectObject(artist.getId(), artist.getName()));
                        }
                        AdminSelectAdapter adapter = new AdminSelectAdapter(AdminAddSongActivity.this,
                                R.layout.item_choose_option, list);
                        binding.spnArtist.setAdapter(adapter);
                        binding.spnArtist.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                            @Override
                            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                mArtistSelected = adapter.getItem(position);
                            }

                            @Override
                            public void onNothingSelected(AdapterView<?> parent) {}
                        });

                        if (mSong != null && mSong.getArtistId() > 0) {
                            binding.spnArtist.setSelection(getPositionSelected(list, mSong.getArtistId()));
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {}
                });
    }

    private int getPositionSelected(List<SelectObject> list, long id) {
        int position = 0;
        for (int i = 0; i < list.size(); i++) {
            if (id == list.get(i).getId()) {
                position = i;
                break;
            }
        }
        return position;
    }

    private void addOrEditSong() {
        String strName = binding.edtName.getText().toString().trim();
        String strImage = binding.edtImage.getText().toString().trim();
        String strUrl = binding.edtLink.getText().toString().trim();

        if (StringUtil.isEmpty(strName)) {
            Toast.makeText(this, getString(R.string.msg_name_require), Toast.LENGTH_SHORT).show();
            return;
        }

        if (StringUtil.isEmpty(strImage)) {
            Toast.makeText(this, getString(R.string.msg_image_require), Toast.LENGTH_SHORT).show();
            return;
        }

        if (StringUtil.isEmpty(strUrl)) {
            Toast.makeText(this, getString(R.string.msg_url_require), Toast.LENGTH_SHORT).show();
            return;
        }

        // Update song
        if (isUpdate) {
            showProgressDialog(true);
            Map<String, Object> map = new HashMap<>();
            map.put("title", strName);
            map.put("image", strImage);
            map.put("url", strUrl);
            map.put("featured", binding.chbFeatured.isChecked());
            map.put("categoryId", mCategorySelected.getId());
            map.put("category", mCategorySelected.getName());
            map.put("artistId", mArtistSelected.getId());
            map.put("artist", mArtistSelected.getName());

            MyApplication.get(this).getSongsDatabaseReference()
                    .child(String.valueOf(mSong.getId())).updateChildren(map, (error, ref) -> {
                showProgressDialog(false);
                Toast.makeText(AdminAddSongActivity.this,
                        getString(R.string.msg_edit_song_success), Toast.LENGTH_SHORT).show();
                GlobalFunction.hideSoftKeyboard(this);
            });
            return;
        }

        // Add song
        showProgressDialog(true);
        long songId = System.currentTimeMillis();
        Song song = new Song(songId, strName, strImage, strUrl, mArtistSelected.getId(),
                mArtistSelected.getName(), mCategorySelected.getId(), mCategorySelected.getName(),
                binding.chbFeatured.isChecked());
        MyApplication.get(this).getSongsDatabaseReference()
                .child(String.valueOf(songId)).setValue(song, (error, ref) -> {
            showProgressDialog(false);
            binding.edtName.setText("");
            binding.edtImage.setText("");
            binding.edtLink.setText("");
            binding.chbFeatured.setChecked(false);
            binding.spnCategory.setSelection(0);
            binding.spnArtist.setSelection(0);
            GlobalFunction.hideSoftKeyboard(this);
            Toast.makeText(this, getString(R.string.msg_add_song_success), Toast.LENGTH_SHORT).show();
        });
    }
}