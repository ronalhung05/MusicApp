package com.pro.music.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.pro.music.MyApplication;
import com.pro.music.R;
import com.pro.music.constant.Constant;
import com.pro.music.constant.GlobalFunction;
import com.pro.music.databinding.ActivityAdminAddStaffBinding;
import com.pro.music.model.Staff;
import com.pro.music.utils.StringUtil;

import java.util.HashMap;
import java.util.Map;

public class AdminAddStaffActivity extends BaseActivity{
    private ActivityAdminAddStaffBinding binding;
    private boolean isUpdate;
    private Staff mStaff;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAdminAddStaffBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        loadDataIntent();
        initToolbar();
        initView();

        binding.btnAddOrEdit.setOnClickListener(v -> addOrEditStaff());
    }

    private void loadDataIntent() {
        Bundle bundleReceived = getIntent().getExtras();
        if (bundleReceived != null) {
            isUpdate = true;
            mStaff = (Staff) bundleReceived.get(Constant.KEY_INTENT_STAFF_OBJECT);
        }
    }

    private void initToolbar() {
        binding.toolbar.imgLeft.setImageResource(R.drawable.ic_back_white);
        binding.toolbar.layoutPlayAll.setVisibility(View.GONE);
        binding.toolbar.imgLeft.setOnClickListener(v -> onBackPressed());
    }

    private void initView() {
        if (isUpdate) {
            binding.toolbar.tvTitle.setText(getString(R.string.label_update_staff));
            binding.btnAddOrEdit.setText(getString(R.string.action_edit));

            binding.edtName.setText(mStaff.getName());
            binding.edtImage.setText(mStaff.getImage());
            binding.edtEmail.setText(mStaff.getEmail());
        } else {
            binding.toolbar.tvTitle.setText(getString(R.string.label_add_staff));
            binding.btnAddOrEdit.setText(getString(R.string.action_add));
        }
    }

    private void addOrEditStaff() {
        String strName = binding.edtName.getText().toString().trim();
        String strImage = binding.edtImage.getText().toString().trim();
        String strEmail = binding.edtEmail.getText().toString().trim();
        String strPassword = binding.edtPassword.getText().toString().trim();

        if (!isValidName(strName)) return;

        if (StringUtil.isEmpty(strImage)) {
            Toast.makeText(this, getString(R.string.msg_image_require), Toast.LENGTH_SHORT).show();
            return;
        }

        if (StringUtil.isEmpty(strEmail)) {
            Toast.makeText(this, getString(R.string.msg_email_require), Toast.LENGTH_SHORT).show();
            return;
        }

//        if (StringUtil.isValidEmail(strEmail)) {
//            Toast.makeText(this, getString(R.string.msg_email_invalid), Toast.LENGTH_SHORT).show();
//            return;
//        }

        if (StringUtil.isEmpty(strPassword) && !isUpdate) {
            Toast.makeText(this, getString(R.string.msg_password_require), Toast.LENGTH_SHORT).show();
            return;
        }

        if (isUpdate) {
            showProgressDialog(true);
            Map<String, Object> map = new HashMap<>();
            map.put("name", strName);
            map.put("image", strImage);
            map.put("email", strEmail);
            if(!strPassword.isEmpty()){
                map.put("password", strPassword);
            }

            MyApplication.get(this).getStaffDatabaseReference()
                    .child(String.valueOf(mStaff.getId())).updateChildren(map, (error, ref) -> {
                        showProgressDialog(false);
                        Toast.makeText(AdminAddStaffActivity.this,
                                getString(R.string.msg_edit_staff_success), Toast.LENGTH_SHORT).show();
                        GlobalFunction.hideSoftKeyboard(this);
                    });
            return;
        }

        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        firebaseAuth.createUserWithEmailAndPassword(strEmail, strPassword);
        showProgressDialog(true);
        long staffId = System.currentTimeMillis();
        Staff staff = new Staff(staffId, strName, strImage, strEmail, strPassword);
        MyApplication.get(this).getStaffDatabaseReference()
                .child(String.valueOf(staff.getId())).setValue(staff, (error, ref) -> {
                    showProgressDialog(false);
                    binding.edtName.setText("");
                    binding.edtImage.setText("");
                    binding.edtEmail.setText("");
                    binding.edtPassword.setText("");
                    GlobalFunction.hideSoftKeyboard(this);
                    Toast.makeText(this, getString(R.string.msg_add_staff_success), Toast.LENGTH_SHORT).show();
        });
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
