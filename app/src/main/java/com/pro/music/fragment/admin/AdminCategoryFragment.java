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
import com.pro.music.activity.AdminAddCategoryActivity;
import com.pro.music.activity.AdminCategorySongActivity;
import com.pro.music.adapter.AdminCategoryAdapter;
import com.pro.music.constant.Constant;
import com.pro.music.constant.GlobalFunction;
import com.pro.music.databinding.FragmentAdminCategoryBinding;
import com.pro.music.listener.IOnAdminManagerCategoryListener;
import com.pro.music.model.Category;
import com.pro.music.utils.StringUtil;

import java.util.ArrayList;
import java.util.List;

public class AdminCategoryFragment extends Fragment {

    private FragmentAdminCategoryBinding binding;
    private List<Category> mListCategory;
    private AdminCategoryAdapter mAdminCategoryAdapter;
    private ChildEventListener mChildEventListener;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentAdminCategoryBinding.inflate(inflater, container, false);

        initView();
        initListener();
        loadListCategory("");

        return binding.getRoot();
    }

    private void initView() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        binding.rcvCategory.setLayoutManager(linearLayoutManager);
        mListCategory = new ArrayList<>();
        mAdminCategoryAdapter = new AdminCategoryAdapter(mListCategory, new IOnAdminManagerCategoryListener() {
            @Override
            public void onClickUpdateCategory(Category category) {
                onClickEditCategory(category);
            }

            @Override
            public void onClickDeleteCategory(Category category) {
                deleteCategoryItem(category);
            }

            @Override
            public void onClickDetailCategory(Category category) {
                Bundle bundle = new Bundle();
                bundle.putSerializable(Constant.KEY_INTENT_CATEGORY_OBJECT, category);
                GlobalFunction.startActivity(getActivity(), AdminCategorySongActivity.class, bundle);
            }
        });
        binding.rcvCategory.setAdapter(mAdminCategoryAdapter);
        binding.rcvCategory.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                if (dy > 0) {
                    binding.btnAddCategory.hide();
                } else {
                    binding.btnAddCategory.show();
                }
                super.onScrolled(recyclerView, dx, dy);
            }
        });
    }

    private void initListener() {
        binding.btnAddCategory.setOnClickListener(v -> onClickAddCategory());

        binding.imgSearch.setOnClickListener(view1 -> searchCategory());

        binding.edtSearchName.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                searchCategory();
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
                    searchCategory();
                }
            }
        });
    }

    private void onClickAddCategory() {
        GlobalFunction.startActivity(getActivity(), AdminAddCategoryActivity.class);
    }

    private void onClickEditCategory(Category category) {
        Bundle bundle = new Bundle();
        bundle.putSerializable(Constant.KEY_INTENT_CATEGORY_OBJECT, category);
        GlobalFunction.startActivity(getActivity(), AdminAddCategoryActivity.class, bundle);
    }

    private void deleteCategoryItem(Category category) {
        new AlertDialog.Builder(getActivity())
                .setTitle(getString(R.string.msg_delete_title))
                .setMessage(getString(R.string.msg_confirm_delete))
                .setPositiveButton(getString(R.string.action_ok), (dialogInterface, i) -> {
                    if (getActivity() == null) {
                        return;
                    }
                    MyApplication.get(getActivity()).getCategoryDatabaseReference()
                            .child(String.valueOf(category.getId())).removeValue((error, ref) ->
                                    Toast.makeText(getActivity(),
                                            getString(R.string.msg_delete_category_successfully),
                                            Toast.LENGTH_SHORT).show());
                })
                .setNegativeButton(getString(R.string.action_cancel), null)
                .show();
    }

    private void searchCategory() {
        String strKey = binding.edtSearchName.getText().toString().trim();
        resetListCategory();
        if (getActivity() != null) {
            MyApplication.get(getActivity()).getCategoryDatabaseReference()
                    .removeEventListener(mChildEventListener);
        }
        loadListCategory(strKey);
        GlobalFunction.hideSoftKeyboard(getActivity());
    }

    private void resetListCategory() {
        if (mListCategory != null) {
            mListCategory.clear();
        } else {
            mListCategory = new ArrayList<>();
        }
    }

    public void loadListCategory(String keyword) {
        if (getActivity() == null) return;
        mChildEventListener = new ChildEventListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, String s) {
                Category category = dataSnapshot.getValue(Category.class);
                if (category == null || mListCategory == null) return;
                if (StringUtil.isEmpty(keyword)) {
                    mListCategory.add(0, category);
                } else {
                    if (GlobalFunction.getTextSearch(category.getName()).toLowerCase().trim()
                            .contains(GlobalFunction.getTextSearch(keyword).toLowerCase().trim())) {
                        mListCategory.add(0, category);
                    }
                }
                if (mAdminCategoryAdapter != null) mAdminCategoryAdapter.notifyDataSetChanged();
            }

            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, String s) {
                Category category = dataSnapshot.getValue(Category.class);
                if (category == null || mListCategory == null || mListCategory.isEmpty()) return;
                for (int i = 0; i < mListCategory.size(); i++) {
                    if (category.getId() == mListCategory.get(i).getId()) {
                        mListCategory.set(i, category);
                        break;
                    }
                }
                if (mAdminCategoryAdapter != null) mAdminCategoryAdapter.notifyDataSetChanged();
            }

            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                Category category = dataSnapshot.getValue(Category.class);
                if (category == null || mListCategory == null || mListCategory.isEmpty()) return;
                for (Category categoryObject : mListCategory) {
                    if (category.getId() == categoryObject.getId()) {
                        mListCategory.remove(categoryObject);
                        break;
                    }
                }
                if (mAdminCategoryAdapter != null) mAdminCategoryAdapter.notifyDataSetChanged();
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, String s) {
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        };
        MyApplication.get(getActivity()).getCategoryDatabaseReference().addChildEventListener(mChildEventListener);
    }
}
