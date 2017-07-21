package com.volkov.alexandr.youtubeaudio.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.volkov.alexandr.youtubeaudio.model.Audio;
import com.volkov.alexandr.youtubeaudio.model.AudioLink;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.volkov.alexandr.youtubeaudio.db.contract.AudioContract.AudioEntry;
import static com.volkov.alexandr.youtubeaudio.db.contract.AudioLinkContract.AudioLinkEntry;

/**
 * Created by AlexandrVolkov on 27.06.2017.
 */
public class DBServiceImpl implements DBService {
    private DBHelper dbHelper;

    public DBServiceImpl(Context context) {
        this.dbHelper = new DBHelper(context);
    }

    @Override
    public long addLink(AudioLink audioLink) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        long audioId = addAudio(audioLink.getAudio());

        ContentValues values = new ContentValues();
        values.put(AudioLinkEntry.COLUMN_HASH, audioLink.getHash());
        values.put(AudioLinkEntry.COLUMN_DATE, audioLink.getDate().getTime());
        values.put(AudioLinkEntry.COLUMN_TITLE, audioLink.getTitle());
        values.put(AudioLinkEntry.COLUMN_DURATION, audioLink.getDuration());
        values.put(AudioLinkEntry.COLUMN_AUDIO_ID, audioId);

        return db.insert(AudioLinkEntry.TABLE_NAME, null, values);
    }

    @Override
    public List<AudioLink> getAllLink() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String sortOrder =
                AudioLinkEntry.COLUMN_DATE + " DESC";

        Cursor c = db.query(AudioLinkEntry.TABLE_NAME, null, null,
                null, null, null, sortOrder);

        List<AudioLink> res = new ArrayList<>();
        if (c.moveToFirst()) {
            int idCoLIndex = c.getColumnIndex(AudioLinkEntry._ID);
            int hashColIndex = c.getColumnIndex(AudioLinkEntry.COLUMN_HASH);
            int dateColIndex = c.getColumnIndex(AudioLinkEntry.COLUMN_DATE);
            int titleColIndex = c.getColumnIndex(AudioLinkEntry.COLUMN_TITLE);
            int durationColIndex = c.getColumnIndex(AudioLinkEntry.COLUMN_DURATION);
            int audioIdColIndex = c.getColumnIndex(AudioLinkEntry.COLUMN_AUDIO_ID);

            do {
                Date date = new Date(c.getLong(dateColIndex));
                Audio audio = getAudioById(c.getLong(audioIdColIndex));

                AudioLink audioLink = AudioLink.newBuilder()
                        .setTitle(c.getString(titleColIndex))
                        .setDate(date)
                        .setHash(c.getString(hashColIndex))
                        .setDuration(c.getLong(durationColIndex))
                        .setAudio(audio).build();

                res.add(audioLink);
            } while (c.moveToNext());
        }
        c.close();
        return res;
    }

    @Override
    public void deleteLink(AudioLink audioLink) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        String selection = AudioLinkEntry.COLUMN_HASH + " LIKE ?";
        String[] selectionArgs = {audioLink.getHash()};

        db.delete(AudioLinkEntry.TABLE_NAME, selection, selectionArgs);
    }

    @Override
    public boolean isAlreadyAdded(AudioLink audioLink) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String selection = AudioLinkEntry.COLUMN_HASH + " = ?";
        String[] selectionArgs = {audioLink.getHash()};

        Cursor c = db.query(AudioLinkEntry.TABLE_NAME, null, selection, selectionArgs,
                null, null, null);

        boolean res = true;
        if (c.getCount() == 0) {
            res = false;
        }
        c.close();
        return res;
    }

    @Override
    public long addAudio(Audio audio) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(AudioEntry.COLUMN_URL, audio.getUrl());
        values.put(AudioEntry.COLUMN_SIZE, audio.getSize());
        values.put(AudioEntry.COLUMN_TYPE, audio.getType());
        values.put(AudioEntry.COLUMN_BITRATE, audio.getBitrate());

        return db.insert(AudioEntry.TABLE_NAME, null, values);
    }

    @Override
    public void deleteAudio(Audio audio) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        String selection = AudioEntry.COLUMN_URL + " = ?";
        String[] selectionArgs = {audio.getUrl()};

        db.delete(AudioEntry.TABLE_NAME, selection, selectionArgs);
    }

    @Override
    public Audio getAudioById(long id) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String selection = AudioEntry._ID + " = ?";
        String[] selectionArgs = {String.valueOf(id)};

        Cursor c = db.query(AudioEntry.TABLE_NAME, null, selection, selectionArgs,
                null, null, null);

        Audio res = null;
        if (c.moveToFirst()) {
            int urlColIndex = c.getColumnIndex(AudioEntry.COLUMN_URL);
            int sizeColIndex = c.getColumnIndex(AudioEntry.COLUMN_SIZE);
            int typeColIndex = c.getColumnIndex(AudioEntry.COLUMN_TYPE);
            int bitrateColIndex = c.getColumnIndex(AudioEntry.COLUMN_BITRATE);

            res = Audio.newBuilder()
                    .setUrl(c.getString(urlColIndex))
                    .setSize(c.getLong(sizeColIndex))
                    .setBitrate(c.getInt(bitrateColIndex))
                    .setType(c.getString(typeColIndex)).build();
        }
        c.close();
        return res;
    }
}
