package com.pro.music.constant;

public class Constant {
    // Max count
    public static final int MAX_COUNT_BANNER = 5;
    public static final int MAX_COUNT_POPULAR = 5;
    public static final int MAX_COUNT_FAVORITE = 5;
    public static final int MAX_COUNT_CATEGORY = 4;
    public static final int MAX_COUNT_ARTIST = 6;

    // Music actions
    public static final int PLAY = 0;
    public static final int PAUSE = 1;
    public static final int NEXT = 2;
    public static final int PREVIOUS = 3;
    public static final int RESUME = 4;
    public static final int CANNEL_NOTIFICATION = 5;
    public static final String MUSIC_ACTION = "musicAction";
    public static final String SONG_POSITION = "songPosition";
    public static final String CHANGE_LISTENER = "change_listener";

    // Key intent
    public static final String CATEGORY_ID = "category_id";
    public static final String ARTIST_ID = "artist_id";
    public static final String IS_FROM_MENU_LEFT = "is_from_menu_left";
    public static final String KEY_INTENT_CATEGORY_OBJECT = "category_object";
    public static final String KEY_INTENT_ARTIST_OBJECT = "artist_object";
    public static final String KEY_INTENT_SONG_OBJECT = "song_object";

    public static final String ADMIN_EMAIL_FORMAT = "@admin.com";
}
