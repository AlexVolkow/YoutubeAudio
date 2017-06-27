package com.volkov.alexandr.youtubeaudio.db;

import com.volkov.alexandr.youtubeaudio.music.Audio;

import java.util.List;

/**
 * Created by AlexandrVolkov on 27.06.2017.
 */
public interface DBService {
    long addAudio(Audio audio);

    List<Audio> getAllAudio();

    void deleteAudio(Audio audio);

    boolean isAlreadyAdded(Audio audio);
}
