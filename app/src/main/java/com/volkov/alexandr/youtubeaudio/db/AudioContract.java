package com.volkov.alexandr.youtubeaudio.db;

import android.provider.BaseColumns;

/**
 * Created by AlexandrVolkov on 27.06.2017.
 */
public class AudioContract {
    private AudioContract() {}

    public static class AudioEntry implements BaseColumns {
        public static final String TABLE_NAME = "audio";
        public static final String COLUMN_HASH = "hash";
        public static final String COLUMN_TITLE = "title";
        public static final String COLUMN_DATE = "date";
        public static final String COLUMN_LENGTH = "audio_length";
        public static final String COLUMN_SIZE = "size";
        public static final String COLUMN_URL = "url";
    }

    public static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + AudioEntry.TABLE_NAME + " (" +
                    AudioEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    AudioEntry.COLUMN_HASH + " TEXT, " +
                    AudioEntry.COLUMN_TITLE + " TEXT, " +
                    AudioEntry.COLUMN_DATE + " INTEGER, " +
                    AudioEntry.COLUMN_LENGTH + " INTEGER, " +
                    AudioEntry.COLUMN_SIZE + " DOUBLE, " +
                    AudioEntry.COLUMN_URL + " TEXT)";

    public static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + AudioEntry.TABLE_NAME;
}
