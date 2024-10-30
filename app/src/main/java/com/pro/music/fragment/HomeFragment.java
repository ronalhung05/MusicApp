package com.pro.music.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.viewpager2.widget.ViewPager2;

import com.pro.music.MyApplication;
import com.pro.music.R;
import com.pro.music.activity.MainActivity;
import com.pro.music.activity.PlayMusicActivity;
import com.pro.music.adapter.ArtistHorizontalAdapter;
import com.pro.music.adapter.BannerSongAdapter;
import com.pro.music.adapter.CategoryAdapter;
import com.pro.music.adapter.SongAdapter;
import com.pro.music.adapter.SongPopularAdapter;
import com.pro.music.constant.Constant;
import com.pro.music.constant.GlobalFunction;
import com.pro.music.databinding.FragmentHomeBinding;
import com.pro.music.listener.IOnClickSongItemListener;
import com.pro.music.model.Artist;
import com.pro.music.model.Category;
import com.pro.music.model.Song;
import com.pro.music.service.MusicService;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding mFragmentHomeBinding;

    private List<Category> mListCategory;
    private List<Artist> mListArtist;
    private List<Song> mListSong;
    private List<Song> mListSongBanner;
    private SongPopularAdapter mSongPopularAdapter;

    private final Handler mHandlerBanner = new Handler();
    private final Runnable mRunnableBanner = new Runnable() {
        @Override
        public void run() {
            if (mListSongBanner == null || mListSongBanner.isEmpty()) {
                return;
            }
            if (mFragmentHomeBinding.viewpager2.getCurrentItem() == mListSongBanner.size() - 1) {
                mFragmentHomeBinding.viewpager2.setCurrentItem(0);
                return;
            }
            mFragmentHomeBinding.viewpager2.setCurrentItem(mFragmentHomeBinding.viewpager2.getCurrentItem() + 1);
        }
    };

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mFragmentHomeBinding = FragmentHomeBinding.inflate(inflater, container, false);

        getListCategoryFromFirebase();
        getListArtistFromFirebase();
        getListSongFromFirebase();
        initListener();

        return mFragmentHomeBinding.getRoot();
    }

    private void initListener() {
        mFragmentHomeBinding.layoutSearch.setOnClickListener(view -> searchSong());

        mFragmentHomeBinding.layoutViewAllCategory.setOnClickListener(v -> {
            MainActivity mainActivity = (MainActivity) getActivity();
            if (mainActivity != null) {
                mainActivity.clickSeeAllCategory();
            }
        });

        mFragmentHomeBinding.layoutViewAllArtist.setOnClickListener(v -> {
            MainActivity mainActivity = (MainActivity) getActivity();
            if (mainActivity != null) {
                mainActivity.clickSeeAllArtist();
            }
        });

        mFragmentHomeBinding.layoutViewAllPopular.setOnClickListener(v -> {
            MainActivity mainActivity = (MainActivity) getActivity();
            if (mainActivity != null) {
                mainActivity.clickSeeAllPopularSongs();
            }
        });

        mFragmentHomeBinding.layoutViewAllFavoriteSongs.setOnClickListener(v -> {
            MainActivity mainActivity = (MainActivity) getActivity();
            if (mainActivity != null) {
                mainActivity.clickSeeAllFavoriteSongs();
            }
        });
    }

    private void getListCategoryFromFirebase() {
        if (getActivity() == null) return;
        MyApplication.get(getActivity()).getCategoryDatabaseReference()
                .addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                mListCategory = new ArrayList<>();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Category category = dataSnapshot.getValue(Category.class);
                    if (category == null) return;
                    mListCategory.add(0, category);
                }
                displayListCategory();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                GlobalFunction.showToastMessage(getActivity(), getString(R.string.msg_get_date_error));
            }
        });
    }

    private void getListArtistFromFirebase() {
        if (getActivity() == null) return;
        MyApplication.get(getActivity()).getArtistDatabaseReference()
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        mListArtist = new ArrayList<>();
                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                            Artist artist = dataSnapshot.getValue(Artist.class);
                            if (artist == null) return;
                            mListArtist.add(0, artist);
                        }
                        displayListArtist();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        GlobalFunction.showToastMessage(getActivity(), getString(R.string.msg_get_date_error));
                    }
                });
    }

    private void getListSongFromFirebase() {
        if (getActivity() == null) return;
        MyApplication.get(getActivity()).getSongsDatabaseReference()
                .addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                mListSong = new ArrayList<>();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Song song = dataSnapshot.getValue(Song.class);
                    if (song == null) return;
                    mListSong.add(0, song);
                }
                displayListBannerSongs();
                displayListPopularSongs();
                displayListFavoriteSongs();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                GlobalFunction.showToastMessage(getActivity(), getString(R.string.msg_get_date_error));
            }
        });
    }

    private void displayListCategory() {
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(), 2);
        mFragmentHomeBinding.rcvCategory.setLayoutManager(gridLayoutManager);

        CategoryAdapter categoryAdapter = new CategoryAdapter(getListCategory(), category -> {
            MainActivity mainActivity = (MainActivity) getActivity();
            if (mainActivity != null) {
                mainActivity.clickOpenSongsByCategory(category);
            }
        });
        mFragmentHomeBinding.rcvCategory.setAdapter(categoryAdapter);
    }

    private void displayListArtist() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity(),
                LinearLayoutManager.HORIZONTAL, false);
        mFragmentHomeBinding.rcvArtist.setLayoutManager(linearLayoutManager);

        ArtistHorizontalAdapter artistHorizontalAdapter = new ArtistHorizontalAdapter(getListArtist(), artist -> {
            MainActivity mainActivity = (MainActivity) getActivity();
            if (mainActivity != null) {
                mainActivity.clickOpenSongsByArtist(artist);
            }
        });
        mFragmentHomeBinding.rcvArtist.setAdapter(artistHorizontalAdapter);
    }

    private void displayListBannerSongs() {
        BannerSongAdapter bannerSongAdapter = new BannerSongAdapter(getListBannerSongs(), new IOnClickSongItemListener() {
            @Override
            public void onClickItemSong(Song song) {
                goToSongDetail(song);
            }

            @Override
            public void onClickFavoriteSong(Song song, boolean favorite) {
            }

            @Override
            public void onClickMoreOptions(Song song) {
            }
        });
        mFragmentHomeBinding.viewpager2.setAdapter(bannerSongAdapter);
        mFragmentHomeBinding.indicator3.setViewPager(mFragmentHomeBinding.viewpager2);

        mFragmentHomeBinding.viewpager2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                mHandlerBanner.removeCallbacks(mRunnableBanner);
                mHandlerBanner.postDelayed(mRunnableBanner, 3000);
            }
        });
    }

    private List<Song> getListBannerSongs() {
        if (mListSongBanner != null) {
            mListSongBanner.clear();
        } else {
            mListSongBanner = new ArrayList<>();
        }
        if (mListSong == null || mListSong.isEmpty()) {
            return mListSongBanner;
        }
        for (Song song : mListSong) {
            if (song.isFeatured() && mListSongBanner.size() < Constant.MAX_COUNT_BANNER) {
                mListSongBanner.add(song);
            }
        }
        return mListSongBanner;
    }

    private void displayListPopularSongs() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        mFragmentHomeBinding.rcvPopularSongs.setLayoutManager(linearLayoutManager);

        mSongPopularAdapter = new SongPopularAdapter(getActivity(), getListPopularSongs(),
                new IOnClickSongItemListener() {
                    @Override
                    public void onClickItemSong(Song song) {
                        goToSongDetail(song);
                    }

                    @Override
                    public void onClickMoreOptions(Song song) {
                        GlobalFunction.handleClickMoreOptions(getActivity(), song);
                    }

                    @Override
                    public void onClickFavoriteSong(Song song, boolean favorite) {
                        GlobalFunction.onClickFavoriteSong(getActivity(), song, favorite);
                    }
                });
        mFragmentHomeBinding.rcvPopularSongs.setAdapter(mSongPopularAdapter);
    }

    private List<Song> getListPopularSongs() {
        List<Song> list = new ArrayList<>();
        if (mListSong == null || mListSong.isEmpty()) {
            return list;
        }
        List<Song> allSongs = new ArrayList<>(mListSong);
        Collections.sort(allSongs, (song1, song2) -> song2.getCount() - song1.getCount());
        for (Song song : allSongs) {
            if (list.size() < Constant.MAX_COUNT_POPULAR) {
                list.add(song);
            }
        }
        return list;
    }

    private void displayListFavoriteSongs() {
        if (getActivity() == null) return;
        List<Song> list = getListFavoriteSongs();
        if (list.isEmpty()) {
            mFragmentHomeBinding.layoutFavorite.setVisibility(View.GONE);
        } else {
            mFragmentHomeBinding.layoutFavorite.setVisibility(View.VISIBLE);

            LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
            mFragmentHomeBinding.rcvFavoriteSongs.setLayoutManager(linearLayoutManager);

            SongAdapter songAdapter = new SongAdapter(getListFavoriteSongs(), new IOnClickSongItemListener() {
                @Override
                public void onClickItemSong(Song song) {
                    goToSongDetail(song);
                }

                @Override
                public void onClickFavoriteSong(Song song, boolean favorite) {
                    GlobalFunction.onClickFavoriteSong(getActivity(), song, favorite);
                }

                @Override
                public void onClickMoreOptions(Song song) {
                    GlobalFunction.handleClickMoreOptions(getActivity(), song);
                }
            });
            mFragmentHomeBinding.rcvFavoriteSongs.setAdapter(songAdapter);
        }
    }

    private List<Category> getListCategory() {
        List<Category> list = new ArrayList<>();
        if (mListCategory == null || mListCategory.isEmpty()) return list;
        for (Category category : mListCategory) {
            if (list.size() < Constant.MAX_COUNT_CATEGORY) {
                list.add(category);
            }
        }
        return list;
    }

    private List<Artist> getListArtist() {
        List<Artist> list = new ArrayList<>();
        if (mListArtist == null || mListArtist.isEmpty()) return list;
        for (Artist artist : mListArtist) {
            if (list.size() < Constant.MAX_COUNT_ARTIST) {
                list.add(artist);
            }
        }
        return list;
    }

    private List<Song> getListFavoriteSongs() {
        List<Song> list = new ArrayList<>();
        if (mListSong == null || mListSong.isEmpty()) {
            return list;
        }
        for (Song song : mListSong) {
            if (GlobalFunction.isFavoriteSong(song) && list.size() < Constant.MAX_COUNT_FAVORITE) {
                list.add(song);
            }
        }
        return list;
    }

    private void searchSong() {
        MainActivity mainActivity = (MainActivity) getActivity();
        if (mainActivity != null) {
            mainActivity.clickSearchSongScreen();
        }
    }

    private void goToSongDetail(@NonNull Song song) {
        MusicService.clearListSongPlaying();
        MusicService.mListSongPlaying.add(song);
        MusicService.isPlaying = false;
        GlobalFunction.startMusicService(getActivity(), Constant.PLAY, 0);
        GlobalFunction.startActivity(getActivity(), PlayMusicActivity.class);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (mSongPopularAdapter != null) {
            mSongPopularAdapter.release();
        }
    }
}
