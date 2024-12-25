package com.pro.music.activity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.pro.music.MyApplication;
import com.pro.music.R;
import com.pro.music.adapter.AdminStaffAdapter;
import com.pro.music.constant.Constant;
import com.pro.music.constant.GlobalFunction;
import com.pro.music.databinding.ActivityAdminStaffBinding;
import com.pro.music.listener.IOnAdminManagerStaffListener;
import com.pro.music.model.Staff;
import com.pro.music.utils.StringUtil;

import java.util.ArrayList;
import java.util.List;

public class AdminStaffManagementActivity extends BaseActivity{
    private ActivityAdminStaffBinding binding;
    private List<Staff> mListStaff;
    private AdminStaffAdapter mAdminStaffAdapter;
    private ChildEventListener mChildEventListener;
    private Staff staff;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAdminStaffBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        loadDataIntent();
        initToolbar();
        initListener();
        initView();
        loadListStaff("");
    }

    private void loadDataIntent() {
        Bundle bundleReceived = getIntent().getExtras();
        if (bundleReceived != null) {
            staff = (Staff) bundleReceived.get(Constant.KEY_INTENT_STAFF_OBJECT);
        }
    }

    private void initToolbar() {
        binding.toolbar.imgLeft.setImageResource(R.drawable.ic_back_white);
        binding.toolbar.layoutPlayAll.setVisibility(View.GONE);
        binding.toolbar.imgLeft.setOnClickListener(v -> onBackPressed());
    }

    private void initView() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        binding.rcvStaff.setLayoutManager(linearLayoutManager);
        mListStaff = new ArrayList<>();
        mAdminStaffAdapter = new AdminStaffAdapter(mListStaff, new IOnAdminManagerStaffListener() {
            @Override
            public void onClickUpdateStaff(Staff staff) {
                onClickEditStaff(staff);
            }

            @Override
            public void onClickDeleteStaff(Staff staff) {
                deleteStaffItem(staff);
            }

            @Override
            public void onClickDetailStaff(Staff staff) {
                // Chưa xử lí
            }
        });
        binding.rcvStaff.setAdapter(mAdminStaffAdapter);
        binding.rcvStaff.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                if (dy > 0) {
                    binding.btnAddStaff.hide();
                } else {
                    binding.btnAddStaff.show();
                }
                super.onScrolled(recyclerView, dx, dy);
            }
        });
    }

    private void initListener() {
        binding.btnAddStaff.setOnClickListener(v -> onClickAddStaff());

        binding.imgSearch.setOnClickListener(v -> searchStaff());

        // Còn thiếu xử lí cho nút search trên bàn phím.
    }

    private void onClickAddStaff(){
        GlobalFunction.startActivity(this, AdminAddStaffActivity.class);
    }

    private void onClickEditStaff(Staff staff){
        Bundle bundle = new Bundle();
        bundle.putSerializable(Constant.KEY_INTENT_STAFF_OBJECT, staff);
        GlobalFunction.startActivity(this, AdminAddStaffActivity.class, bundle);
    }

    private void deleteStaffItem(Staff staff) {
        new AlertDialog.Builder(this)
                .setTitle(getString(R.string.msg_delete_title))
                .setMessage(getString(R.string.msg_confirm_delete))
                .setPositiveButton(getString(R.string.action_ok), (dialogInterface, i)
                        -> MyApplication.get(this).getStaffDatabaseReference()
                        .child(String.valueOf(staff.getId())).removeValue((error, ref) ->
                                Toast.makeText(this,
                                        getString(R.string.msg_delete_staff_successfully),
                                        Toast.LENGTH_SHORT).show()))
                .setNegativeButton(getString(R.string.action_cancel), null)
                .show();
    }

    private void searchStaff() {
        String strKey = binding.edtSearchName.getText().toString().trim();
        resetListStaff();
        MyApplication.get(this).getStaffDatabaseReference()
                .removeEventListener(mChildEventListener);

        loadListStaff(strKey);
        GlobalFunction.hideSoftKeyboard(this);
    }

    private void resetListStaff() {
        if (mListStaff != null) {
            mListStaff.clear();
        } else {
            mListStaff = new ArrayList<>();
        }
    }

    public void loadListStaff(String keyword) {
        mChildEventListener = new ChildEventListener() {
            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, String s) {
                Staff staff = dataSnapshot.getValue(Staff.class);
                if (staff == null || mListStaff == null) return;
                if (StringUtil.isEmpty(keyword)) {
                    mListStaff.add(0, staff);
                } else {
                    if (GlobalFunction.getTextSearch(staff.getName()).toLowerCase().trim()
                            .contains(GlobalFunction.getTextSearch(keyword).toLowerCase().trim())) {
                        mListStaff.add(0, staff);
                    }
                }
                if (mAdminStaffAdapter != null) mAdminStaffAdapter.notifyDataSetChanged();
            }

            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, String s) {
                Staff staff = dataSnapshot.getValue(Staff.class);
                if (staff == null || mListStaff == null || mListStaff.isEmpty()) return;
                for (int i = 0; i < mListStaff.size(); i++) {
                    if (staff.getId() == mListStaff.get(i).getId()) {
                        mListStaff.set(i, staff);
                        break;
                    }
                }
                if (mAdminStaffAdapter != null) mAdminStaffAdapter.notifyDataSetChanged();
            }

            @SuppressLint("NotifyDataSetChanged")
            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {
                Staff staff = dataSnapshot.getValue(Staff.class);
                if (staff == null || mListStaff == null || mListStaff.isEmpty()) return;
                for (Staff staffObject : mListStaff) {
                    if (staff.getId() == staffObject.getId()) {
                        mListStaff.remove(staffObject);
                        break;
                    }
                }
                if (mAdminStaffAdapter != null) mAdminStaffAdapter.notifyDataSetChanged();
            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, String s) {
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        };
        MyApplication.get(this).getStaffDatabaseReference()
                .addChildEventListener(mChildEventListener);
    }
}
