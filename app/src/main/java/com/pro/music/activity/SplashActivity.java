package com.pro.music.activity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.view.Window;
import android.view.WindowManager;

import androidx.appcompat.app.AppCompatActivity;

import com.pro.music.constant.AboutUsConfig;
import com.pro.music.constant.GlobalFunction;
import com.pro.music.databinding.ActivitySplashBinding;
import com.pro.music.prefs.DataStoreManager;
import com.pro.music.utils.StringUtil;
//display the splash screen with title and slogan
@SuppressLint("CustomSplashScreen")
public class SplashActivity extends AppCompatActivity {

    private ActivitySplashBinding mActivitySplashBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 2 code lines ensure hire everything just show the full screen -> don't show any distraction
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE); //title bar
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN); //hide the status bar
        mActivitySplashBinding = ActivitySplashBinding.inflate(getLayoutInflater()); //view binding instead of viewById -> naming
        setContentView(mActivitySplashBinding.getRoot());

        initUi();

        Handler handler = new Handler();
        handler.postDelayed(this::goToActivity, 2000); //pause for 2 seconds the logo
    }

    private void initUi() {
        mActivitySplashBinding.tvAboutUsTitle.setText(AboutUsConfig.ABOUT_US_TITLE);
        mActivitySplashBinding.tvAboutUsSlogan.setText(AboutUsConfig.ABOUT_US_SLOGAN);
    }
    //display based on admin or user role - session
    private void goToActivity() {
        if (DataStoreManager.getUser() != null
                && !StringUtil.isEmpty(DataStoreManager.getUser().getEmail())) {
            if (DataStoreManager.getUser().isAdmin()) {
                GlobalFunction.startActivity(this, AdminMainActivity.class);
            } else {
                GlobalFunction.startActivity(this, MainActivity.class);
            }
        } else {
            GlobalFunction.startActivity(this, SignInActivity.class);
        }
        finish();
    }
}
