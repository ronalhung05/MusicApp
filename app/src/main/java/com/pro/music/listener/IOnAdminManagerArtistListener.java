package com.pro.music.listener;

import com.pro.music.model.Artist;

public interface IOnAdminManagerArtistListener {
    void onClickUpdateArtist(Artist artist);
    void onClickDeleteArtist(Artist artist);
    void onClickDetailArtist(Artist artist);
}
