package com.pro.music.service;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.pro.music.constant.Constant;
import com.pro.music.constant.GlobalFunction;

//use only on notification
public class MusicReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        int action = intent.getExtras().getInt(Constant.MUSIC_ACTION);//retrieve
        GlobalFunction.startMusicService(context, action, MusicService.mSongPosition);//send
    }
}