package com.pro.music.fragment;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.widget.SeekBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.pro.music.MyApplication;
import com.pro.music.R;
import com.pro.music.activity.PlayMusicActivity;
import com.pro.music.constant.Constant;
import com.pro.music.constant.GlobalFunction;
import com.pro.music.databinding.FragmentPlaySongBinding;
import com.pro.music.model.Song;
import com.pro.music.prefs.DataStoreManager;
import com.pro.music.service.MusicService;
import com.pro.music.utils.AppUtil;
import com.pro.music.utils.GlideUtils;

import java.util.Timer;
import java.util.TimerTask;

@SuppressLint("NonConstantResourceId")
public class PlaySongFragment extends Fragment implements View.OnClickListener {

    private FragmentPlaySongBinding mFragmentPlaySongBinding;
    private Timer mTimer;
    private int mAction;
    private final BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            mAction = intent.getIntExtra(Constant.MUSIC_ACTION, 0);
            handleMusicAction();
        }
    };

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mFragmentPlaySongBinding = FragmentPlaySongBinding.inflate(inflater, container, false);

        if (getActivity() != null) {
            LocalBroadcastManager.getInstance(getActivity()).registerReceiver(mBroadcastReceiver,
                    new IntentFilter(Constant.CHANGE_LISTENER));
        }
        initControl();
        showInforSong();
        updateStatusShuffleButton();
        updateStatusRepeatButton();
        mAction = MusicService.mAction;
        handleMusicAction();

        return mFragmentPlaySongBinding.getRoot();
    }

    private void initControl() {
        mTimer = new Timer();

        mFragmentPlaySongBinding.imgShuffle.setOnClickListener(this);
        mFragmentPlaySongBinding.imgRepeat.setOnClickListener(this);
        mFragmentPlaySongBinding.imgPrevious.setOnClickListener(this);
        mFragmentPlaySongBinding.imgPlay.setOnClickListener(this);
        mFragmentPlaySongBinding.imgNext.setOnClickListener(this);
        mFragmentPlaySongBinding.imgDownload.setOnClickListener(this);

        mFragmentPlaySongBinding.seekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                MusicService.mPlayer.seekTo(seekBar.getProgress());
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            }
        });
    }

    private void showInforSong() {
        if (MusicService.mListSongPlaying == null || MusicService.mListSongPlaying.isEmpty()) {
            return;
        }
        Song currentSong = MusicService.mListSongPlaying.get(MusicService.mSongPosition);
        mFragmentPlaySongBinding.tvSongName.setText(currentSong.getTitle());
        mFragmentPlaySongBinding.tvArtist.setText(currentSong.getArtist());
        GlideUtils.loadUrl(currentSong.getImage(), mFragmentPlaySongBinding.imgSong);
        if (DataStoreManager.getUser().isAdmin()) {
            mFragmentPlaySongBinding.layoutCountView.setVisibility(View.GONE);
            mFragmentPlaySongBinding.imgDownload.setVisibility(View.GONE);
            mFragmentPlaySongBinding.imgFavorite.setVisibility(View.GONE);
            mFragmentPlaySongBinding.imgRepeat.setVisibility(View.GONE);
            mFragmentPlaySongBinding.imgShuffle.setVisibility(View.GONE);
        } else {
            mFragmentPlaySongBinding.layoutCountView.setVisibility(View.VISIBLE);
            mFragmentPlaySongBinding.imgDownload.setVisibility(View.VISIBLE);
            mFragmentPlaySongBinding.imgFavorite.setVisibility(View.VISIBLE);
            mFragmentPlaySongBinding.imgRepeat.setVisibility(View.VISIBLE);
            mFragmentPlaySongBinding.imgShuffle.setVisibility(View.VISIBLE);
            listenerCountViewSong(currentSong.getId());
            listenerFavoriteSong(currentSong.getId());
        }
    }

    private void handleMusicAction() {
        if (Constant.CANNEL_NOTIFICATION == mAction) {
            if (getActivity() != null) {
                getActivity().onBackPressed();
            }
            return;
        }
        switch (mAction) {
            case Constant.PREVIOUS:
            case Constant.NEXT:
                stopAnimationPlayMusic();
                showInforSong();
                break;

            case Constant.PLAY:
                showInforSong();
                if (MusicService.isPlaying) {
                    startAnimationPlayMusic();
                }
                showSeekBar();
                showStatusButtonPlay();
                break;

            case Constant.PAUSE:
                stopAnimationPlayMusic();
                showSeekBar();
                showStatusButtonPlay();
                break;

            case Constant.RESUME:
                startAnimationPlayMusic();
                showSeekBar();
                showStatusButtonPlay();
                break;
        }
    }

    private void startAnimationPlayMusic() {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                mFragmentPlaySongBinding.imgSong.animate().rotationBy(360).withEndAction(this).setDuration(15000)
                        .setInterpolator(new LinearInterpolator()).start();
            }
        };
        mFragmentPlaySongBinding.imgSong.animate().rotationBy(360).withEndAction(runnable).setDuration(15000)
                .setInterpolator(new LinearInterpolator()).start();
    }

    private void stopAnimationPlayMusic() {
        mFragmentPlaySongBinding.imgSong.animate().cancel();
    }

    public void showSeekBar() {
        mTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                if (getActivity() == null) {
                    return;
                }
                getActivity().runOnUiThread(() -> {
                    if (MusicService.mPlayer == null) {
                        return;
                    }
                    mFragmentPlaySongBinding.tvTimeCurrent.setText(AppUtil.getTime(MusicService.mPlayer.getCurrentPosition()));
                    mFragmentPlaySongBinding.tvTimeMax.setText(AppUtil.getTime(MusicService.mLengthSong));
                    mFragmentPlaySongBinding.seekbar.setMax(MusicService.mLengthSong);
                    mFragmentPlaySongBinding.seekbar.setProgress(MusicService.mPlayer.getCurrentPosition());
                });
            }
        }, 0, 1000);
    }

    private void showStatusButtonPlay() {
        if (MusicService.isPlaying) {
            mFragmentPlaySongBinding.imgPlay.setImageResource(R.drawable.ic_pause_black);
        } else {
            mFragmentPlaySongBinding.imgPlay.setImageResource(R.drawable.ic_play_black);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mTimer != null) {
            mTimer.cancel();
            mTimer = null;
        }
        if (getActivity() != null) {
            LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(mBroadcastReceiver);
        }
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.img_previous) {
            clickOnPrevButton();
        } else if (id == R.id.img_play) {
            clickOnPlayButton();
        } else if (id == R.id.img_next) {
            clickOnNextButton();
        } else if (id == R.id.img_shuffle) {
            clickOnShuffleButton();
        } else if (id == R.id.img_repeat) {
            clickOnRepeatButton();
        } else if (id == R.id.img_download) {
            clickOnDownloadSong();
        }
    }

    private void clickOnShuffleButton() {
        if (!MusicService.isShuffle) {
            MusicService.isShuffle = true;
            MusicService.isRepeat = false;
        } else {
            MusicService.isShuffle = false;
        }
        updateStatusShuffleButton();
        updateStatusRepeatButton();
    }

    private void clickOnRepeatButton() {
        if (!MusicService.isRepeat) {
            MusicService.isRepeat = true;
            MusicService.isShuffle = false;
        } else {
            MusicService.isRepeat = false;
        }
        updateStatusShuffleButton();
        updateStatusRepeatButton();
    }

    private void updateStatusShuffleButton() {
        if (MusicService.isShuffle) {
            mFragmentPlaySongBinding.imgShuffle.setImageResource(R.drawable.ic_shuffle_enable);
        } else {
            mFragmentPlaySongBinding.imgShuffle.setImageResource(R.drawable.ic_shuffle_disable);
        }
    }

    private void updateStatusRepeatButton() {
        if (MusicService.isRepeat) {
            mFragmentPlaySongBinding.imgRepeat.setImageResource(R.drawable.ic_repeat_one_enable);
        } else {
            mFragmentPlaySongBinding.imgRepeat.setImageResource(R.drawable.ic_repeat_disable);
        }
    }

    private void clickOnPrevButton() {
        GlobalFunction.startMusicService(getActivity(), Constant.PREVIOUS, MusicService.mSongPosition);
    }

    private void clickOnNextButton() {
        GlobalFunction.startMusicService(getActivity(), Constant.NEXT, MusicService.mSongPosition);
    }

    private void clickOnPlayButton() {
        if (MusicService.isPlaying) {
            GlobalFunction.startMusicService(getActivity(), Constant.PAUSE, MusicService.mSongPosition);
        } else {
            GlobalFunction.startMusicService(getActivity(), Constant.RESUME, MusicService.mSongPosition);
        }
    }

    private void listenerCountViewSong(long songId) {
        if (getActivity() == null) return;
        ValueEventListener countViewSongEventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Integer currentCount = snapshot.getValue(Integer.class);
                if (currentCount != null) {
                    mFragmentPlaySongBinding.tvCountView.setText(String.valueOf(currentCount));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        };
        MyApplication.get(getActivity()).getCountViewDatabaseReference(songId)
                .addValueEventListener(countViewSongEventListener);
    }

    private void listenerFavoriteSong(long songId) {
        if (getActivity() == null) return;
        MyApplication.get(getActivity()).getSongDetailDatabaseReference(songId)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        Song song = snapshot.getValue(Song.class);
                        if (song == null) return;
                        boolean isFavorite = GlobalFunction.isFavoriteSong(song);
                        if (isFavorite) {
                            mFragmentPlaySongBinding.imgFavorite.setImageResource(R.drawable.ic_favorite);
                        } else {
                            mFragmentPlaySongBinding.imgFavorite.setImageResource(R.drawable.ic_unfavorite);
                        }
                        mFragmentPlaySongBinding.imgFavorite.setOnClickListener(
                                view -> GlobalFunction.onClickFavoriteSong(getActivity(), song, !isFavorite)
                        );
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                    }
                });
    }

    private void clickOnDownloadSong() {
        Song currentSong = MusicService.mListSongPlaying.get(MusicService.mSongPosition);
        PlayMusicActivity activity = (PlayMusicActivity) getActivity();
        if (activity == null) return;
        activity.downloadSong(currentSong);
    }
}
