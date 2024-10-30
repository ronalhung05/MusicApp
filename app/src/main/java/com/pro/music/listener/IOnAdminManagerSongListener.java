package com.pro.music.listener;

import com.pro.music.model.Song;

public interface IOnAdminManagerSongListener {
    void onClickUpdateSong(Song song);
    void onClickDeleteSong(Song song);
    void onClickDetailSong(Song song);
}
