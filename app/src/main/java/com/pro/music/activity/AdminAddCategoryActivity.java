package com.pro.music.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.pro.music.MyApplication;
import com.pro.music.R;
import com.pro.music.constant.Constant;
import com.pro.music.constant.GlobalFunction;
import com.pro.music.databinding.ActivityAdminAddCategoryBinding;
import com.pro.music.model.Category;
import com.pro.music.utils.StringUtil;

import java.util.HashMap;
import java.util.Map;

public class AdminAddCategoryActivity extends BaseActivity {

    private ActivityAdminAddCategoryBinding binding;
    private boolean isUpdate;
    private Category mCategory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAdminAddCategoryBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        loadDataIntent();
        initToolbar();
        initView();

        binding.btnAddOrEdit.setOnClickListener(v -> addOrEditCategory());
    }

    private void loadDataIntent() {
        Bundle bundleReceived = getIntent().getExtras();
        if (bundleReceived != null) {
            isUpdate = true;
            mCategory = (Category) bundleReceived.get(Constant.KEY_INTENT_CATEGORY_OBJECT);
        }
    }

    private void initToolbar() {
        binding.toolbar.imgLeft.setImageResource(R.drawable.ic_back_white);
        binding.toolbar.layoutPlayAll.setVisibility(View.GONE);
        binding.toolbar.imgLeft.setOnClickListener(v -> onBackPressed());
    }

    private void initView() {
        if (isUpdate) {
            binding.toolbar.tvTitle.setText(getString(R.string.label_update_category));
            binding.btnAddOrEdit.setText(getString(R.string.action_edit));

            binding.edtName.setText(mCategory.getName());
            binding.edtImage.setText(mCategory.getImage());
        } else {
            binding.toolbar.tvTitle.setText(getString(R.string.label_add_category));
            binding.btnAddOrEdit.setText(getString(R.string.action_add));
        }
    }

    private void addOrEditCategory() {
        String strName = binding.edtName.getText().toString().trim();
        String strImage = binding.edtImage.getText().toString().trim();

        if (StringUtil.isEmpty(strName)) {
            Toast.makeText(this, getString(R.string.msg_name_require), Toast.LENGTH_SHORT).show();
            return;
        }

        if (StringUtil.isEmpty(strImage)) {
            Toast.makeText(this, getString(R.string.msg_image_require), Toast.LENGTH_SHORT).show();
            return;
        }

        // Update category
        if (isUpdate) {
            showProgressDialog(true);
            Map<String, Object> map = new HashMap<>();
            map.put("name", strName);
            map.put("image", strImage);

            MyApplication.get(this).getCategoryDatabaseReference()
                    .child(String.valueOf(mCategory.getId())).updateChildren(map, (error, ref) -> {
                showProgressDialog(false);
                Toast.makeText(AdminAddCategoryActivity.this,
                        getString(R.string.msg_edit_category_success), Toast.LENGTH_SHORT).show();
                GlobalFunction.hideSoftKeyboard(this);
            });
            return;
        }

        // Add category
        showProgressDialog(true);
        long categoryId = System.currentTimeMillis();
        Category category = new Category(categoryId, strName, strImage);
        MyApplication.get(this).getCategoryDatabaseReference()
                .child(String.valueOf(categoryId)).setValue(category, (error, ref) -> {
            showProgressDialog(false);
            binding.edtName.setText("");
            binding.edtImage.setText("");
            GlobalFunction.hideSoftKeyboard(this);
            Toast.makeText(this, getString(R.string.msg_add_category_success), Toast.LENGTH_SHORT).show();
        });
    }
}