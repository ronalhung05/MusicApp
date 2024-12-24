package com.pro.music.listener;

import com.pro.music.model.Staff;


public interface IOnAdminManagerStaffListener {
    void onClickUpdateStaff(Staff staff);
    void onClickDeleteStaff(Staff staff);
    void onClickDetailStaff(Staff staff);
}
