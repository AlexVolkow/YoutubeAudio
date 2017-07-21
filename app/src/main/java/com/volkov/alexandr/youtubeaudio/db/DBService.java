package com.volkov.alexandr.youtubeaudio.db;

import com.volkov.alexandr.youtubeaudio.model.Audio;
import com.volkov.alexandr.youtubeaudio.model.AudioLink;

import java.util.List;

/**
 * Created by AlexandrVolkov on 27.06.2017.
 */
public interface DBService {
    long addAudio(Audio audio);

    void deleteAudio(Audio audio);

    Audio getAudioById(long id);

    long addLink(AudioLink audioLink);

    List<AudioLink> getAllLink();

    void deleteLink(AudioLink audioLink);

    boolean isAlreadyAdded(AudioLink audioLink);
}
