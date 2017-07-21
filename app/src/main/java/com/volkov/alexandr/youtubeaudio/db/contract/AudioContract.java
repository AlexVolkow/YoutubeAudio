package com.volkov.alexandr.youtubeaudio.db.contract;

import android.provider.BaseColumns;

/**
 * Created by AlexandrVolkov on 17.07.2017.
 */
public class AudioContract {
    private AudioContract() {
    }

    public static class AudioEntry implements BaseColumns {
        public static final String TABLE_NAME = "audio_file";

        public static final String COLUMN_URL = "url";
        public static final String COLUMN_SIZE = "size";
        public static final String COLUMN_TYPE = "type";
        public static final String COLUMN_BITRATE = "bitrate";
    }

    public static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + AudioEntry.TABLE_NAME + " (" +
                    AudioEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    AudioEntry.COLUMN_URL + " TEXT, " +
                    AudioEntry.COLUMN_SIZE + " INTEGER, " +
                    AudioEntry.COLUMN_BITRATE + " INTEGER, " +
                    AudioEntry.COLUMN_TYPE + " TEXT)";

    public static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + AudioEntry.TABLE_NAME;
}
