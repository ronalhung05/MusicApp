package com.pro.music.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.view.GravityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.afollestad.materialdialogs.MaterialDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.pro.music.R;
import com.pro.music.constant.Constant;
import com.pro.music.constant.GlobalFunction;
import com.pro.music.databinding.ActivityMainBinding;
import com.pro.music.fragment.AllSongsFragment;
import com.pro.music.fragment.ArtistFragment;
import com.pro.music.fragment.CategoryFragment;
import com.pro.music.fragment.ChangePasswordFragment;
import com.pro.music.fragment.ContactFragment;
import com.pro.music.fragment.FavoriteFragment;
import com.pro.music.fragment.FeedbackFragment;
import com.pro.music.fragment.HomeFragment;
import com.pro.music.fragment.PopularSongsFragment;
import com.pro.music.fragment.SearchFragment;
import com.pro.music.fragment.SongsByArtistFragment;
import com.pro.music.fragment.SongsByCategoryFragment;
import com.pro.music.model.Artist;
import com.pro.music.model.Category;
import com.pro.music.model.Song;
import com.pro.music.model.User;
import com.pro.music.prefs.DataStoreManager;
import com.pro.music.service.MusicService;
import com.pro.music.utils.GlideUtils;

@SuppressLint("NonConstantResourceId")
public class MainActivity extends BaseActivity implements View.OnClickListener {

    public static final int TYPE_HOME = 1;
    public static final int TYPE_CATEGORY = 2;
    public static final int TYPE_ARTIST = 3;
    public static final int TYPE_ALL_SONGS = 4;
    public static final int TYPE_POPULAR_SONGS = 5;
    public static final int TYPE_FAVORITE_SONGS = 6;
    public static final int TYPE_FEEDBACK = 7;
    public static final int TYPE_CONTACT = 8;
    public static final int TYPE_CHANGE_PASSWORD = 9;

    private static final int REQUEST_PERMISSION_CODE = 10;
    private Song mSong;

    private int mTypeScreen = TYPE_HOME;
    private ActivityMainBinding mActivityMainBinding;
    private int mAction;
    private final BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            mAction = intent.getIntExtra(Constant.MUSIC_ACTION, 0);
            handleMusicAction();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivityMainBinding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(mActivityMainBinding.getRoot());

        checkNotificationPermission();
        LocalBroadcastManager.getInstance(this).registerReceiver(mBroadcastReceiver,
                new IntentFilter(Constant.CHANGE_LISTENER));
        openHomeScreen();
        displayUserInformation();
        initListener();
        displayLayoutBottom();
    }

    private void checkNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ActivityCompat.checkSelfPermission(this,
                    Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.POST_NOTIFICATIONS}, 1);
            }
        }
    }

    private void displayUserInformation() {
        User user = DataStoreManager.getUser();
        mActivityMainBinding.menuLeft.tvUserEmail.setText(user.getEmail());
    }

    private void initHeader() {
        switch (mTypeScreen) {
            case TYPE_HOME:
                handleToolbarTitle(getString(R.string.app_name));
                handleDisplayIconHeader(true);
                handleDisplayButtonPlayAll(false);
                break;

            case TYPE_CATEGORY:
                handleToolbarTitle(getString(R.string.menu_category));
                handleDisplayIconHeader(true);
                handleDisplayButtonPlayAll(false);
                break;

            case TYPE_ARTIST:
                handleToolbarTitle(getString(R.string.menu_artist));
                handleDisplayIconHeader(true);
                handleDisplayButtonPlayAll(false);
                break;

            case TYPE_ALL_SONGS:
                handleToolbarTitle(getString(R.string.menu_all_songs));
                handleDisplayIconHeader(true);
                handleDisplayButtonPlayAll(true);
                break;

            case TYPE_POPULAR_SONGS:
                handleToolbarTitle(getString(R.string.menu_popular_songs));
                handleDisplayIconHeader(true);
                handleDisplayButtonPlayAll(true);
                break;

            case TYPE_FAVORITE_SONGS:
                handleToolbarTitle(getString(R.string.menu_favorite_songs));
                handleDisplayIconHeader(true);
                handleDisplayButtonPlayAll(true);
                break;

            case TYPE_FEEDBACK:
                handleToolbarTitle(getString(R.string.menu_feedback));
                handleDisplayIconHeader(true);
                handleDisplayButtonPlayAll(false);
                break;

            case TYPE_CONTACT:
                handleToolbarTitle(getString(R.string.menu_contact));
                handleDisplayIconHeader(true);
                handleDisplayButtonPlayAll(false);
                break;

            case TYPE_CHANGE_PASSWORD:
                handleToolbarTitle(getString(R.string.menu_change_password));
                handleDisplayIconHeader(true);
                handleDisplayButtonPlayAll(false);
                break;
        }
    }

    private void handleToolbarTitle(String title) {
        mActivityMainBinding.header.tvTitle.setText(title);
    }

    public void handleDisplayIconHeader(boolean isShowMenuLeft) {
        if (isShowMenuLeft) {
            mActivityMainBinding.header.imgLeft.setImageResource(R.drawable.ic_menu_left);
            mActivityMainBinding.header.imgLeft.setOnClickListener(
                    v -> mActivityMainBinding.drawerLayout.openDrawer(GravityCompat.START)
            );
        } else {
            mActivityMainBinding.header.imgLeft.setImageResource(R.drawable.ic_back_white);
            mActivityMainBinding.header.imgLeft.setOnClickListener(
                    v -> onBackPressed()
            );
        }
    }

    public void handleDisplayButtonPlayAll(boolean isShow) {
        if (isShow) {
            mActivityMainBinding.header.layoutPlayAll.setVisibility(View.VISIBLE);
        } else {
            mActivityMainBinding.header.layoutPlayAll.setVisibility(View.GONE);
        }
    }

    private void initListener() {
        mActivityMainBinding.header.layoutPlayAll.setOnClickListener(this);

        mActivityMainBinding.menuLeft.layoutClose.setOnClickListener(this);
        mActivityMainBinding.menuLeft.layoutMenuHome.setOnClickListener(this);
        mActivityMainBinding.menuLeft.layoutMenuCategory.setOnClickListener(this);
        mActivityMainBinding.menuLeft.layoutMenuArtist.setOnClickListener(this);
        mActivityMainBinding.menuLeft.layoutMenuAllSongs.setOnClickListener(this);
        mActivityMainBinding.menuLeft.layoutMenuPopularSongs.setOnClickListener(this);
        mActivityMainBinding.menuLeft.layoutMenuFavoriteSongs.setOnClickListener(this);
        mActivityMainBinding.menuLeft.layoutMenuFeedback.setOnClickListener(this);
        mActivityMainBinding.menuLeft.layoutMenuContact.setOnClickListener(this);
        mActivityMainBinding.menuLeft.layoutMenuChangePassword.setOnClickListener(this);
        mActivityMainBinding.menuLeft.layoutMenuSignOut.setOnClickListener(this);

        mActivityMainBinding.layoutBottom.imgPrevious.setOnClickListener(this);
        mActivityMainBinding.layoutBottom.imgPlay.setOnClickListener(this);
        mActivityMainBinding.layoutBottom.imgNext.setOnClickListener(this);
        mActivityMainBinding.layoutBottom.imgClose.setOnClickListener(this);
        mActivityMainBinding.layoutBottom.layoutText.setOnClickListener(this);
        mActivityMainBinding.layoutBottom.imgSong.setOnClickListener(this);

        getSupportFragmentManager().addOnBackStackChangedListener(() -> {
            Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.content_frame);
            if (fragment instanceof HomeFragment) {
                handleToolbarTitle(getString(R.string.app_name));
                handleDisplayIconHeader(true);
                handleDisplayButtonPlayAll(false);
            } else if (fragment instanceof CategoryFragment) {
                handleToolbarTitle(getString(R.string.menu_category));
                handleDisplayButtonPlayAll(false);
                CategoryFragment categoryFragment = (CategoryFragment) fragment;
                handleDisplayIconHeader(categoryFragment.mIsFromMenuLeft);
            } else if (fragment instanceof ArtistFragment) {
                handleToolbarTitle(getString(R.string.menu_artist));
                handleDisplayButtonPlayAll(false);
                ArtistFragment artistFragment = (ArtistFragment) fragment;
                handleDisplayIconHeader(artistFragment.mIsFromMenuLeft);
            }
        });
    }

    private void openHomeScreen() {
        replaceFragment(new HomeFragment());
        mTypeScreen = TYPE_HOME;
        initHeader();
    }

    public void openCategoryScreen() {
        replaceFragment(CategoryFragment.newInstance(true));
        mTypeScreen = TYPE_CATEGORY;
        initHeader();
    }

    private void openArtistScreen() {
        replaceFragment(ArtistFragment.newInstance(true));
        mTypeScreen = TYPE_ARTIST;
        initHeader();
    }

    private void openAllSongsScreen() {
        replaceFragment(new AllSongsFragment());
        mTypeScreen = TYPE_ALL_SONGS;
        initHeader();
    }

    public void openPopularSongsScreen() {
        replaceFragment(new PopularSongsFragment());
        mTypeScreen = TYPE_POPULAR_SONGS;
        initHeader();
    }

    public void openFavoriteSongsScreen() {
        replaceFragment(new FavoriteFragment());
        mTypeScreen = TYPE_FAVORITE_SONGS;
        initHeader();
    }

    private void openFeedbackScreen() {
        replaceFragment(new FeedbackFragment());
        mTypeScreen = TYPE_FEEDBACK;
        initHeader();
    }

    private void openContactScreen() {
        replaceFragment(new ContactFragment());
        mTypeScreen = TYPE_CONTACT;
        initHeader();
    }

    private void openChangePasswordScreen() {
        replaceFragment(new ChangePasswordFragment());
        mTypeScreen = TYPE_CHANGE_PASSWORD;
        initHeader();
    }

    public void clickSeeAllCategory() {
        addFragment(CategoryFragment.newInstance(false));
        handleToolbarTitle(getString(R.string.menu_category));
        handleDisplayIconHeader(false);
        handleDisplayButtonPlayAll(false);
    }

    public void clickSeeAllArtist() {
        addFragment(ArtistFragment.newInstance(false));
        handleToolbarTitle(getString(R.string.menu_artist));
        handleDisplayIconHeader(false);
        handleDisplayButtonPlayAll(false);
    }

    public void clickSeeAllPopularSongs() {
        addFragment(new PopularSongsFragment());
        handleToolbarTitle(getString(R.string.menu_popular_songs));
        handleDisplayIconHeader(false);
        handleDisplayButtonPlayAll(true);
    }

    public void clickSeeAllFavoriteSongs() {
        addFragment(new FavoriteFragment());
        handleToolbarTitle(getString(R.string.menu_favorite_songs));
        handleDisplayIconHeader(false);
        handleDisplayButtonPlayAll(true);
    }

    public void clickOpenSongsByCategory(Category category) {
        addFragment(SongsByCategoryFragment.newInstance(category.getId()));
        handleToolbarTitle(category.getName());
        handleDisplayIconHeader(false);
        handleDisplayButtonPlayAll(true);
    }

    public void clickOpenSongsByArtist(Artist artist) {
        addFragment(SongsByArtistFragment.newInstance(artist.getId()));
        handleToolbarTitle(artist.getName());
        handleDisplayIconHeader(false);
        handleDisplayButtonPlayAll(true);
    }

    public void clickSearchSongScreen() {
        addFragment(new SearchFragment());
        handleToolbarTitle(getString(R.string.label_search));
        handleDisplayIconHeader(false);
        handleDisplayButtonPlayAll(true);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.layout_close) {
            mActivityMainBinding.drawerLayout.closeDrawer(GravityCompat.START);
        } else if (id == R.id.layout_menu_home) {
            mActivityMainBinding.drawerLayout.closeDrawer(GravityCompat.START);
            openHomeScreen();
        } else if (id == R.id.layout_menu_category) {
            mActivityMainBinding.drawerLayout.closeDrawer(GravityCompat.START);
            openCategoryScreen();
        } else if (id == R.id.layout_menu_artist) {
            mActivityMainBinding.drawerLayout.closeDrawer(GravityCompat.START);
            openArtistScreen();
        } else if (id == R.id.layout_menu_all_songs) {
            mActivityMainBinding.drawerLayout.closeDrawer(GravityCompat.START);
            openAllSongsScreen();
        } else if (id == R.id.layout_menu_popular_songs) {
            mActivityMainBinding.drawerLayout.closeDrawer(GravityCompat.START);
            openPopularSongsScreen();
        } else if (id == R.id.layout_menu_favorite_songs) {
            mActivityMainBinding.drawerLayout.closeDrawer(GravityCompat.START);
            openFavoriteSongsScreen();
        } else if (id == R.id.layout_menu_feedback) {
            mActivityMainBinding.drawerLayout.closeDrawer(GravityCompat.START);
            openFeedbackScreen();
        } else if (id == R.id.layout_menu_contact) {
            mActivityMainBinding.drawerLayout.closeDrawer(GravityCompat.START);
            openContactScreen();
        } else if (id == R.id.layout_menu_change_password) {
            mActivityMainBinding.drawerLayout.closeDrawer(GravityCompat.START);
            openChangePasswordScreen();
        } else if (id == R.id.layout_menu_sign_out) {
            onClickSignOut();
        } else if (id == R.id.img_previous) {
            clickOnPrevButton();
        } else if (id == R.id.img_play) {
            clickOnPlayButton();
        } else if (id == R.id.img_next) {
            clickOnNextButton();
        } else if (id == R.id.img_close) {
            clickOnCloseButton();
        } else if (id == R.id.layout_text || id == R.id.img_song) {
            openPlayMusicActivity();
        }
    }

    public void replaceFragment(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.content_frame, fragment).commitAllowingStateLoss();
    }

    public void addFragment(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.add(R.id.content_frame, fragment)
                .addToBackStack(fragment.getClass().getName())
                .commit();
    }

    private void showConfirmExitApp() {
        new MaterialDialog.Builder(this)
                .title(getString(R.string.app_name))
                .content(getString(R.string.msg_exit_app))
                .positiveText(getString(R.string.action_ok))
                .onPositive((dialog, which) -> finish())
                .negativeText(getString(R.string.action_cancel))
                .cancelable(false)
                .show();
    }

    private void displayLayoutBottom() {
        if (MusicService.mPlayer == null) {
            mActivityMainBinding.layoutBottom.layoutItem.setVisibility(View.GONE);
            return;
        }
        mActivityMainBinding.layoutBottom.layoutItem.setVisibility(View.VISIBLE);
        showInforSong();
        showStatusButtonPlay();
    }

    private void handleMusicAction() {
        if (Constant.CANNEL_NOTIFICATION == mAction) {
            mActivityMainBinding.layoutBottom.layoutItem.setVisibility(View.GONE);
            return;
        }
        mActivityMainBinding.layoutBottom.layoutItem.setVisibility(View.VISIBLE);
        showInforSong();
        showStatusButtonPlay();
    }

    private void showInforSong() {
        if (MusicService.mListSongPlaying == null || MusicService.mListSongPlaying.isEmpty()) {
            return;
        }
        Song currentSong = MusicService.mListSongPlaying.get(MusicService.mSongPosition);
        mActivityMainBinding.layoutBottom.tvSongName.setText(currentSong.getTitle());
        mActivityMainBinding.layoutBottom.tvArtist.setText(currentSong.getArtist());
        GlideUtils.loadUrl(currentSong.getImage(), mActivityMainBinding.layoutBottom.imgSong);
    }

    private void showStatusButtonPlay() {
        if (MusicService.isPlaying) {
            mActivityMainBinding.layoutBottom.imgPlay.setImageResource(R.drawable.ic_pause_black);
        } else {
            mActivityMainBinding.layoutBottom.imgPlay.setImageResource(R.drawable.ic_play_black);
        }
    }

    private void clickOnPrevButton() {
        GlobalFunction.startMusicService(this, Constant.PREVIOUS, MusicService.mSongPosition);
    }

    private void clickOnNextButton() {
        GlobalFunction.startMusicService(this, Constant.NEXT, MusicService.mSongPosition);
    }

    private void clickOnPlayButton() {
        if (MusicService.isPlaying) {
            GlobalFunction.startMusicService(this, Constant.PAUSE, MusicService.mSongPosition);
        } else {
            GlobalFunction.startMusicService(this, Constant.RESUME, MusicService.mSongPosition);
        }
    }

    private void clickOnCloseButton() {
        GlobalFunction.startMusicService(this, Constant.CANNEL_NOTIFICATION, MusicService.mSongPosition);
    }

    private void openPlayMusicActivity() {
        GlobalFunction.startActivity(this, PlayMusicActivity.class);
    }

    private void onClickSignOut() {
        FirebaseAuth.getInstance().signOut();
        DataStoreManager.setUser(null);
        // Stop service when user sign out
        clickOnCloseButton();
        GlobalFunction.startActivity(this, SignInActivity.class);
        finishAffinity();
    }

    public ActivityMainBinding getActivityMainBinding() {
        return mActivityMainBinding;
    }

    @Override
    public void onBackPressed() {
        if (getSupportFragmentManager().getBackStackEntryCount() < 1) {
            showConfirmExitApp();
        } else {
            GlobalFunction.hideSoftKeyboard(this);
            super.onBackPressed();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mBroadcastReceiver);
    }

    public void downloadSong(Song song) {
        mSong = song;
        checkPermission();
    }

    private void checkPermission() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU) {
            if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
                String[] permission = {Manifest.permission.WRITE_EXTERNAL_STORAGE};
                requestPermissions(permission, REQUEST_PERMISSION_CODE);
            } else {
                GlobalFunction.startDownloadFile(this, mSong);
            }
        } else {
            GlobalFunction.startDownloadFile(this, mSong);
        }
    }

    @SuppressLint("MissingSuperCall")
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == REQUEST_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                GlobalFunction.startDownloadFile(this, mSong);
            } else {
                Toast.makeText(this, getString(R.string.msg_permission_denied),
                        Toast.LENGTH_SHORT).show();
            }
        }
    }
}
