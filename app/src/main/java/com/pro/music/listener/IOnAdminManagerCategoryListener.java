package com.pro.music.listener;

import com.pro.music.model.Category;

public interface IOnAdminManagerCategoryListener {
    void onClickUpdateCategory(Category category);
    void onClickDeleteCategory(Category category);
    void onClickDetailCategory(Category category);
}
