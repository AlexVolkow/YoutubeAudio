package com.volkov.alexandr.youtubeaudio.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.volkov.alexandr.youtubeaudio.music.Audio;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import static com.volkov.alexandr.youtubeaudio.db.AudioContract.AudioEntry;

/**
 * Created by AlexandrVolkov on 27.06.2017.
 */
public class DBServiceImpl implements DBService {
    private DBHelper dbHelper;

    public DBServiceImpl(Context context) {
        this.dbHelper = new DBHelper(context);
    }

    @Override
    public long addAudio(Audio audio) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(AudioEntry.COLUMN_HASH, audio.getHash());
        values.put(AudioEntry.COLUMN_TITLE, audio.getTitle());
        values.put(AudioEntry.COLUMN_DATE, audio.getDate().getTime());
        values.put(AudioEntry.COLUMN_LENGTH, audio.getLength());
        values.put(AudioEntry.COLUMN_SIZE, audio.getSize());
        values.put(AudioEntry.COLUMN_TYPE, audio.getType());
        values.put(AudioEntry.COLUMN_URL, audio.getUrl());

        return db.insert(AudioEntry.TABLE_NAME, null, values);
    }

    @Override
    public List<Audio> getAllAudio() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String sortOrder =
                AudioEntry.COLUMN_DATE + " DESC";

        Cursor c = db.query(AudioEntry.TABLE_NAME, null, null,
                null, null, null, sortOrder);

        List<Audio> res = new ArrayList<>();
        if (c.moveToFirst()) {
            int hashColIndex = c.getColumnIndex(AudioEntry.COLUMN_HASH);
            int titleColIndex = c.getColumnIndex(AudioEntry.COLUMN_TITLE);
            int dateColIndex = c.getColumnIndex(AudioEntry.COLUMN_DATE);
            int lengthColIndex = c.getColumnIndex(AudioEntry.COLUMN_LENGTH);
            int sizeColIndex = c.getColumnIndex(AudioEntry.COLUMN_SIZE);
            int typeColIndex = c.getColumnIndex(AudioEntry.COLUMN_TYPE);
            int urlColIndex = c.getColumnIndex(AudioEntry.COLUMN_URL);

            do {
                Date date = new Date(c.getLong(dateColIndex));
                res.add(new Audio(c.getString(hashColIndex), c.getString(titleColIndex),
                        date, c.getInt(lengthColIndex), c.getDouble(sizeColIndex), c.getString(typeColIndex),
                        c.getString(urlColIndex)));
            } while (c.moveToNext());
        }
        c.close();
        return res;
    }

    @Override
    public void deleteAudio(Audio audio) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        String selection = AudioEntry.COLUMN_HASH + " LIKE ?";
        String[] selectionArgs = { audio.getHash() };

        db.delete(AudioEntry.TABLE_NAME, selection, selectionArgs);
    }

    @Override
    public boolean isAlreadyAdded(Audio audio) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String selection = AudioEntry.COLUMN_HASH + " = ?";
        String[] selectionArgs = { audio.getHash() };

        Cursor c = db.query(AudioEntry.TABLE_NAME, null, selection, selectionArgs,
                null, null , null);

        if (c.getCount() == 0) {
            return false;
        }

        c.close();
        return true;
    }
}
