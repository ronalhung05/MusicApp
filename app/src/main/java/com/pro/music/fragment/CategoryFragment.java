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
import com.pro.music.adapter.CategoryAdapter;
import com.pro.music.constant.Constant;
import com.pro.music.constant.GlobalFunction;
import com.pro.music.databinding.FragmentCategoryBinding;
import com.pro.music.model.Category;

import java.util.ArrayList;
import java.util.List;

public class CategoryFragment extends Fragment {

    private FragmentCategoryBinding mFragmentCategoryBinding;
    private List<Category> mListCategory;
    private CategoryAdapter mCategoryAdapter;
    public boolean mIsFromMenuLeft;

    public static CategoryFragment newInstance(boolean isFromMenuLeft) {
        CategoryFragment fragment = new CategoryFragment();
        Bundle bundle = new Bundle();
        bundle.putBoolean(Constant.IS_FROM_MENU_LEFT, isFromMenuLeft);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        mFragmentCategoryBinding = FragmentCategoryBinding.inflate(inflater, container, false);

        getDataIntent();
        initUi();
        getListAllCategory();

        return mFragmentCategoryBinding.getRoot();
    }

    private void getDataIntent() {
        Bundle bundle = getArguments();
        if (bundle == null) return;
        mIsFromMenuLeft = bundle.getBoolean(Constant.IS_FROM_MENU_LEFT);
    }

    private void initUi() {
        if (getActivity() == null) return;
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(), 2);
        mFragmentCategoryBinding.rcvData.setLayoutManager(gridLayoutManager);

        mListCategory = new ArrayList<>();
        mCategoryAdapter = new CategoryAdapter(mListCategory, category -> {
            MainActivity mainActivity = (MainActivity) getActivity();
            if (mainActivity != null) {
                mainActivity.clickOpenSongsByCategory(category);
            }
        });
        mFragmentCategoryBinding.rcvData.setAdapter(mCategoryAdapter);
    }
    @SuppressLint("NotifyDataSetChanged")
    private void getListAllCategory() {
        if (getActivity() == null) return;
        MyApplication.get(getActivity()).getCategoryDatabaseReference()
                .addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (mListCategory == null) {
                    mListCategory = new ArrayList<>();
                } else {
                    mListCategory.clear();
                }
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Category category = dataSnapshot.getValue(Category.class);
                    if (category == null) return;
                    mListCategory.add(0, category);
                }
                if (mCategoryAdapter != null) mCategoryAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                GlobalFunction.showToastMessage(getActivity(), getString(R.string.msg_get_date_error));
            }
        });
    }
}
