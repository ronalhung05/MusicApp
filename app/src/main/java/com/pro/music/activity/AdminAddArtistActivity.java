package com.pro.music.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.pro.music.MyApplication;
import com.pro.music.R;
import com.pro.music.constant.Constant;
import com.pro.music.constant.GlobalFunction;
import com.pro.music.databinding.ActivityAdminAddArtistBinding;
import com.pro.music.model.Artist;
import com.pro.music.utils.StringUtil;

import java.util.HashMap;
import java.util.Map;

public class AdminAddArtistActivity extends BaseActivity {

    private ActivityAdminAddArtistBinding binding;
    private boolean isUpdate;
    private Artist mArtist;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAdminAddArtistBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        loadDataIntent(); //load to know if new or existing artist
        initToolbar(); //tool bar
        initView(); //view based on the flag

        binding.btnAddOrEdit.setOnClickListener(v -> addOrEditArtist());
    }

    private void loadDataIntent() {
        Bundle bundleReceived = getIntent().getExtras();
        if (bundleReceived != null) {
            isUpdate = true;
            mArtist = (Artist) bundleReceived.get(Constant.KEY_INTENT_ARTIST_OBJECT);//get from the constant
        }
    }

    private void initToolbar() {
        binding.toolbar.imgLeft.setImageResource(R.drawable.ic_back_white);
        binding.toolbar.layoutPlayAll.setVisibility(View.GONE);
        binding.toolbar.imgLeft.setOnClickListener(v -> onBackPressed());
    }
    //update hay
    private void initView() {
        if (isUpdate) {
            binding.toolbar.tvTitle.setText(getString(R.string.label_update_artist));
            binding.btnAddOrEdit.setText(getString(R.string.action_edit));

            binding.edtName.setText(mArtist.getName());
            binding.edtImage.setText(mArtist.getImage());
        } else {
            binding.toolbar.tvTitle.setText(getString(R.string.label_add_artist));
            binding.btnAddOrEdit.setText(getString(R.string.action_add));
        }
    }

    private void addOrEditArtist() {
        String strName = binding.edtName.getText().toString().trim();
        String strImage = binding.edtImage.getText().toString().trim();

        if(!isValidName(strName)) return;

        if (StringUtil.isEmpty(strImage)) {
            Toast.makeText(this, getString(R.string.msg_image_require), Toast.LENGTH_SHORT).show();
            return;
        }

        // Update artist
        if (isUpdate) {
            showProgressDialog(true);
            Map<String, Object> map = new HashMap<>(); //json file
            map.put("name", strName);
            map.put("image", strImage);
            //update in firebase
            MyApplication.get(this).getArtistDatabaseReference()
                    .child(String.valueOf(mArtist.getId())).updateChildren(map, (error, ref) -> {
                showProgressDialog(false); //hide after complete
                Toast.makeText(AdminAddArtistActivity.this,
                        getString(R.string.msg_edit_artist_success), Toast.LENGTH_SHORT).show();
                GlobalFunction.hideSoftKeyboard(this);
            });
            //.child -> point to node child in FB ased on ID
            //.updateChildren -> update from map only -> callback (error, ref) -> show error
            return;
        }

        // Add artist
        showProgressDialog(true);
        long artistId = System.currentTimeMillis(); //get ID based on time with milisecond
        Artist artist = new Artist(artistId, strName, strImage);
        MyApplication.get(this).getArtistDatabaseReference()
                .child(String.valueOf(artistId)).setValue(artist, (error, ref) -> {
            showProgressDialog(false);
            binding.edtName.setText("");
            binding.edtImage.setText("");
            GlobalFunction.hideSoftKeyboard(this);
            Toast.makeText(this, getString(R.string.msg_add_artist_success), Toast.LENGTH_SHORT).show();
        });//add and rest value
    }

    private boolean isValidName(String name){
        if (StringUtil.isEmpty(name)) {
            Toast.makeText(this, getString(R.string.msg_name_require), Toast.LENGTH_SHORT).show();

            return false;
        }

        if (StringUtil.isContainNumber(name)) {
            Toast.makeText(this, getString(R.string.msg_name_contain_number), Toast.LENGTH_SHORT).show();

            return false;
        }

        if (StringUtil.isContainsSpecialCharacter(name)) {
            Toast.makeText(this, getString(R.string.msg_name_contain_special_character), Toast.LENGTH_SHORT).show();

            return false;
        }

        return true;
    }
}