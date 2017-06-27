package com.volkov.alexandr.youtubeaudio.music;

import android.content.Context;
import android.content.Intent;

import static com.volkov.alexandr.youtubeaudio.music.MusicService.*;

/**
 * Created by AlexandrVolkov on 26.06.2017.
 */
public class MusicControls {
    private boolean isPlaying = false;
    private Context context;

    public MusicControls(Context context) {
        this.context = context;
    }

    public void play() {
        Intent service = new Intent(context, MusicService.class);
        service.putExtra(CMD_NAME, PLAY);
        context.startService(service);
        isPlaying = true;
    }

    public void pause() {
        Intent service = new Intent(context, MusicService.class);
        service.putExtra(CMD_NAME, PAUSE);
        context.startService(service);
        isPlaying = false;
    }

    public void playSong(Audio audio) {
        Intent service = new Intent(context, MusicService.class);
        service.putExtra(CMD_NAME, START_NEW);
        service.putExtra(AUDIO, audio);
        context.startService(service);
        isPlaying = true;
    }

    public void fastforward() {
        Intent service = new Intent(context, MusicService.class);
        service.putExtra(CMD_NAME, FASTFORWARD);
        context.startService(service);
        isPlaying = true;
    }

    public void rewind() {
        Intent service = new Intent(context, MusicService.class);
        service.putExtra(CMD_NAME, REWIND);
        context.startService(service);
        isPlaying = true;
    }

    public boolean isPlaying() {
        return isPlaying;
    }
}
