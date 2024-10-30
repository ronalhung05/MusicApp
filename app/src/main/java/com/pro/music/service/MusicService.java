package com.pro.music.service;

import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ServiceInfo;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.widget.RemoteViews;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.pro.music.MyApplication;
import com.pro.music.R;
import com.pro.music.activity.MainActivity;
import com.pro.music.constant.Constant;
import com.pro.music.constant.GlobalFunction;
import com.pro.music.model.Song;
import com.pro.music.prefs.DataStoreManager;
import com.pro.music.utils.StringUtil;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class MusicService extends Service implements MediaPlayer.OnPreparedListener, MediaPlayer.OnCompletionListener {

    public static boolean isPlaying;
    public static List<Song> mListSongPlaying;
    public static int mSongPosition;
    public static MediaPlayer mPlayer;
    public static int mLengthSong;
    public static int mAction = -1;
    public static boolean isShuffle;
    public static boolean isRepeat;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        if (mPlayer == null) {
            mPlayer = new MediaPlayer();
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Bundle bundle = intent.getExtras();
        if (bundle != null) {
            if (bundle.containsKey(Constant.MUSIC_ACTION)) {
                mAction = bundle.getInt(Constant.MUSIC_ACTION);
            }
            if (bundle.containsKey(Constant.SONG_POSITION)) {
                mSongPosition = bundle.getInt(Constant.SONG_POSITION);
            }

            handleActionMusic(mAction);
        }

        return START_NOT_STICKY;
    }

    private void handleActionMusic(int action) {
        switch (action) {
            case Constant.PLAY:
                playSong();
                break;

            case Constant.PREVIOUS:
                prevSong();
                break;

            case Constant.NEXT:
                nextSong();
                break;

            case Constant.PAUSE:
                pauseSong();
                break;

            case Constant.RESUME:
                resumeSong();
                break;

            case Constant.CANNEL_NOTIFICATION:
                cancelNotification();
                break;

            default:
                break;
        }
    }

    private void playSong() {
        String songUrl = mListSongPlaying.get(mSongPosition).getUrl();
        if (!StringUtil.isEmpty(songUrl)) {
            playMediaPlayer(songUrl);
        }
        mListSongPlaying.get(mSongPosition).setPriority(false);
    }

    private void pauseSong() {
        if (mPlayer != null && mPlayer.isPlaying()) {
            mPlayer.pause();
            isPlaying = false;
            sendMusicNotification();
            sendBroadcastChangeListener();
        }
    }

    private void cancelNotification() {
        if (mPlayer != null && mPlayer.isPlaying()) {
            mPlayer.pause();
            isPlaying = false;
        }
        clearListSongPlaying();
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancelAll();
        sendBroadcastChangeListener();
        stopSelf();
    }

    private void resumeSong() {
        if (mPlayer != null) {
            mPlayer.start();
            isPlaying = true;
            sendMusicNotification();
            sendBroadcastChangeListener();
        }
    }

    public void prevSong() {
        int newPosition;
        if (getPositionPriority() > 0) {
            newPosition = getPositionPriority();
        } else {
            if (mListSongPlaying.size() > 1) {
                if (isShuffle) {
                    newPosition = new Random().nextInt(mListSongPlaying.size());
                } else {
                    if (isRepeat)
                        newPosition = mSongPosition;
                    else if (mSongPosition > 0) {
                        newPosition = mSongPosition - 1;
                    } else {
                        newPosition = mListSongPlaying.size() - 1;
                    }
                }
            } else {
                newPosition = 0;
            }
        }
        mSongPosition = newPosition;
        sendMusicNotification();
        sendBroadcastChangeListener();
        playSong();
    }

    private void nextSong() {
        int newPosition;
        if (getPositionPriority() > 0) {
            newPosition = getPositionPriority();
        } else {
            if (mListSongPlaying.size() > 1) {
                if (isShuffle) {
                    newPosition = new Random().nextInt(mListSongPlaying.size());
                } else {
                    if (isRepeat)
                        newPosition = mSongPosition;
                    else if (mSongPosition < mListSongPlaying.size() - 1) {
                        newPosition = mSongPosition + 1;
                    } else {
                        newPosition = 0;
                    }
                }
            } else {
                newPosition = 0;
            }
        }
        mSongPosition = newPosition;
        sendMusicNotification();
        sendBroadcastChangeListener();
        playSong();
    }

    public void playMediaPlayer(String songUrl) {
        try {
            if (mPlayer.isPlaying()) {
                mPlayer.stop();
            }
            mPlayer.reset();
            mPlayer.setDataSource(songUrl);
            mPlayer.prepareAsync();
            initControl();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void initControl() {
        mPlayer.setOnPreparedListener(this);
        mPlayer.setOnCompletionListener(this);
    }

    private void sendMusicNotification() {
        if (DataStoreManager.getUser().isAdmin()) return;

        Song song = mListSongPlaying.get(mSongPosition);

        int pendingFlag = PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_UPDATE_CURRENT;
        Intent intent = new Intent(this, MainActivity.class);
        @SuppressLint("UnspecifiedImmutableFlag")
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, pendingFlag);

        RemoteViews remoteViews = new RemoteViews(getPackageName(), R.layout.layout_push_notification_music);
        remoteViews.setTextViewText(R.id.tv_song_name, song.getTitle());

        // Set listener
        remoteViews.setOnClickPendingIntent(R.id.img_previous, GlobalFunction.openMusicReceiver(this, Constant.PREVIOUS));
        remoteViews.setOnClickPendingIntent(R.id.img_next, GlobalFunction.openMusicReceiver(this, Constant.NEXT));
        if (isPlaying) {
            remoteViews.setImageViewResource(R.id.img_play, R.drawable.ic_pause_gray);
            remoteViews.setOnClickPendingIntent(R.id.img_play, GlobalFunction.openMusicReceiver(this, Constant.PAUSE));
        } else {
            remoteViews.setImageViewResource(R.id.img_play, R.drawable.ic_play_gray);
            remoteViews.setOnClickPendingIntent(R.id.img_play, GlobalFunction.openMusicReceiver(this, Constant.RESUME));
        }
        remoteViews.setOnClickPendingIntent(R.id.img_close, GlobalFunction.openMusicReceiver(this, Constant.CANNEL_NOTIFICATION));

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, MyApplication.CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_small_push_notification)
                .setContentIntent(pendingIntent)
                .setSound(null);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            builder.setCustomBigContentView(remoteViews);
        } else {
            builder.setCustomContentView(remoteViews);
        }

        Notification notification = builder.build();
        if (Build.VERSION.SDK_INT >= 34) {
            startForeground(1, notification, ServiceInfo.FOREGROUND_SERVICE_TYPE_SPECIAL_USE);
        } else {
            startForeground(1, notification);
        }
    }

    public static void clearListSongPlaying() {
        if (mListSongPlaying != null) {
            mListSongPlaying.clear();
        } else {
            mListSongPlaying = new ArrayList<>();
        }
    }

    public static boolean isSongExist(long songId) {
        if (mListSongPlaying == null || mListSongPlaying.isEmpty()) return false;
        boolean isExist = false;
        for (Song song : mListSongPlaying) {
            if (songId == song.getId()) {
                isExist = true;
                break;
            }
        }
        return isExist;
    }

    public static boolean isSongPlaying(long songId) {
        if (mListSongPlaying == null || mListSongPlaying.isEmpty()) return false;
        Song currentSong = mListSongPlaying.get(mSongPosition);
        return songId == currentSong.getId();
    }

    public static int getPositionPriority() {
        if (mListSongPlaying == null || mListSongPlaying.isEmpty()) return 0;
        int position = 0;
        for (int i = 0; i < mListSongPlaying.size(); i++) {
            if (mListSongPlaying.get(i).isPriority()) {
                position = i;
                break;
            }
        }
        return position;
    }

    public static void deleteSongFromPlaylist(long songId) {
        if (mListSongPlaying == null || mListSongPlaying.isEmpty()) return;
        int songPosition = 0;
        for (int i = 0; i < mListSongPlaying.size(); i++) {
            if (songId == mListSongPlaying.get(i).getId()) {
                songPosition = i;
                break;
            }
        }
        for (Song song : mListSongPlaying) {
            if (songId == song.getId()) {
                mListSongPlaying.remove(song);
                if (mSongPosition > songPosition) {
                    mSongPosition = mSongPosition - 1;
                }
                break;
            }
        }
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        mAction = Constant.NEXT;
        nextSong();
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        mLengthSong = mPlayer.getDuration();
        mp.start();
        isPlaying = true;
        mAction = Constant.PLAY;
        sendMusicNotification();
        sendBroadcastChangeListener();
        changeCountViewSong();
    }

    private void sendBroadcastChangeListener() {
        Intent intent = new Intent(Constant.CHANGE_LISTENER);
        intent.putExtra(Constant.MUSIC_ACTION, mAction);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }

    private void changeCountViewSong() {
        if (DataStoreManager.getUser().isAdmin()) return;

        long songId = mListSongPlaying.get(mSongPosition).getId();
        MyApplication.get(this).getCountViewDatabaseReference(songId)
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        Integer currentCount = snapshot.getValue(Integer.class);
                        if (currentCount != null) {
                            int newCount = currentCount + 1;
                            MyApplication.get(MusicService.this).getCountViewDatabaseReference(songId).removeEventListener(this);
                            MyApplication.get(MusicService.this).getCountViewDatabaseReference(songId).setValue(newCount);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {}
                });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mPlayer != null) {
            mPlayer.release();
            mPlayer = null;
        }
    }
}
