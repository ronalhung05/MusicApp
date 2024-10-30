package com.pro.music.activity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;

import androidx.viewpager2.widget.ViewPager2;

import com.afollestad.materialdialogs.MaterialDialog;
import com.pro.music.R;
import com.pro.music.adapter.AdminViewPagerAdapter;
import com.pro.music.databinding.ActivityAdminMainBinding;

public class AdminMainActivity extends BaseActivity {

    private ActivityAdminMainBinding mActivityAdminMainBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivityAdminMainBinding = ActivityAdminMainBinding.inflate(getLayoutInflater());
        setContentView(mActivityAdminMainBinding.getRoot());

        setToolBar();

        mActivityAdminMainBinding.viewpager2.setUserInputEnabled(false);
        mActivityAdminMainBinding.viewpager2.setOffscreenPageLimit(5);
        AdminViewPagerAdapter adminViewPagerAdapter = new AdminViewPagerAdapter(this);
        mActivityAdminMainBinding.viewpager2.setAdapter(adminViewPagerAdapter);

        mActivityAdminMainBinding.viewpager2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                switch (position) {
                    case 0:
                        mActivityAdminMainBinding.bottomNavigation.getMenu().findItem(R.id.nav_category).setChecked(true);
                        break;

                    case 1:
                        mActivityAdminMainBinding.bottomNavigation.getMenu().findItem(R.id.nav_artist).setChecked(true);
                        break;

                    case 2:
                        mActivityAdminMainBinding.bottomNavigation.getMenu().findItem(R.id.nav_song).setChecked(true);
                        break;

                    case 3:
                        mActivityAdminMainBinding.bottomNavigation.getMenu().findItem(R.id.nav_feedback).setChecked(true);
                        break;

                    case 4:
                        mActivityAdminMainBinding.bottomNavigation.getMenu().findItem(R.id.nav_account).setChecked(true);
                        break;
                }
            }
        });

        mActivityAdminMainBinding.bottomNavigation.setOnNavigationItemSelectedListener(item -> {
            int id = item.getItemId();
            if (id == R.id.nav_category) {
                mActivityAdminMainBinding.viewpager2.setCurrentItem(0);
            } else if (id == R.id.nav_artist) {
                mActivityAdminMainBinding.viewpager2.setCurrentItem(1);
            } else if (id == R.id.nav_song) {
                mActivityAdminMainBinding.viewpager2.setCurrentItem(2);
            }  else if (id == R.id.nav_feedback) {
                mActivityAdminMainBinding.viewpager2.setCurrentItem(3);
            } else if (id == R.id.nav_account) {
                mActivityAdminMainBinding.viewpager2.setCurrentItem(4);
            }
            return true;
        });
    }

    @SuppressLint("MissingSuperCall")
    @Override
    public void onBackPressed() {
        showConfirmExitApp();
    }

    private void showConfirmExitApp() {
        new MaterialDialog.Builder(this)
                .title(getString(R.string.app_name))
                .content(getString(R.string.msg_exit_app))
                .positiveText(getString(R.string.action_ok))
                .onPositive((dialog, which) -> finishAffinity())
                .negativeText(getString(R.string.action_cancel))
                .cancelable(false)
                .show();
    }

    public void setToolBar() {
        mActivityAdminMainBinding.header.imgLeft.setVisibility(View.GONE);
        mActivityAdminMainBinding.header.tvTitle.setText(getString(R.string.app_name));
    }
}