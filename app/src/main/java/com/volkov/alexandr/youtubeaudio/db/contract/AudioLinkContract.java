package com.volkov.alexandr.youtubeaudio.db.contract;

import android.provider.BaseColumns;

/**
 * Created by AlexandrVolkov on 27.06.2017.
 */
public class AudioLinkContract {
    private AudioLinkContract() {
    }

    public static class AudioLinkEntry implements BaseColumns {
        public static final String TABLE_NAME = "audio_link";

        public static final String COLUMN_HASH = "hash";
        public static final String COLUMN_DATE = "date";
        public static final String COLUMN_TITLE = "title";
        public static final String COLUMN_DURATION = "audio_duration";
        public static final String COLUMN_AUDIO_ID = "audio_id";
    }

    public static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + AudioLinkEntry.TABLE_NAME + " (" +
                    AudioLinkEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    AudioLinkEntry.COLUMN_HASH + " TEXT, " +
                    AudioLinkEntry.COLUMN_DATE + " INTEGER, " +
                    AudioLinkEntry.COLUMN_DURATION + " INTEGER, " +
                    AudioLinkEntry.COLUMN_AUDIO_ID + " INTEGER, " +
                    AudioLinkEntry.COLUMN_TITLE + " TEXT)";

    public static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + AudioLinkEntry.TABLE_NAME;
}
