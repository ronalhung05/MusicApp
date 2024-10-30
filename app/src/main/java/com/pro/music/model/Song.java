package com.pro.music.model;

import java.io.Serializable;
import java.util.HashMap;

public class Song implements Serializable {

    private long id;
    private String title;
    private String image;
    private String url;
    private long artistId;
    private String artist;
    private long categoryId;
    private String category;
    private boolean featured;
    private int count;
    private boolean isPlaying;
    private boolean isPriority;

    private HashMap<String, UserInfor> favorite;

    public Song() {
    }

    public Song(long id, String title, String image, String url, long artistId, String artist,
                long categoryId, String category, boolean featured) {
        this.id = id;
        this.title = title;
        this.image = image;
        this.url = url;
        this.artistId = artistId;
        this.artist = artist;
        this.categoryId = categoryId;
        this.category = category;
        this.featured = featured;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public long getArtistId() {
        return artistId;
    }

    public void setArtistId(long artistId) {
        this.artistId = artistId;
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(long categoryId) {
        this.categoryId = categoryId;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public boolean isFeatured() {
        return featured;
    }

    public void setFeatured(boolean featured) {
        this.featured = featured;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public boolean isPlaying() {
        return isPlaying;
    }

    public void setPlaying(boolean playing) {
        isPlaying = playing;
    }

    public boolean isPriority() {
        return isPriority;
    }

    public void setPriority(boolean priority) {
        isPriority = priority;
    }

    public HashMap<String, UserInfor> getFavorite() {
        return favorite;
    }

    public void setFavorite(HashMap<String, UserInfor> favorite) {
        this.favorite = favorite;
    }
}
