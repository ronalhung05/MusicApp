package com.pro.music.listener;

import com.pro.music.model.Song;

public interface IOnClickSongItemListener {
    void onClickItemSong(Song song);
    void onClickFavoriteSong(Song song, boolean favorite);
    void onClickMoreOptions(Song song);
}
