package com.volkov.alexandr.youtubeaudio.music;

import android.app.Service;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;


/**
 * Created by AlexandrVolkov on 23.06.2017.
 */
public class MusicService extends Service {
    public static String BROADCAST_ACTION = "com.volkov.alexandr.youtubeaudio.music";

    public static final String CMD_NAME = "CMD_NAME";
    public static final String PLAY = "PLAY";
    public static final String PAUSE = "PAUSE";
    public static final String START_NEW = "START_NEW";
    public static final String CMD_STATUS = "CMD_STATUS";
    public static final String AUDIO = "AUDIO";
    public static final String AUDIO_COVER = "AUDIO_COVER";
    public static final String AUDIO_ID = "AUDIO_ID";
    public static final String REWIND = "REWIND";
    public static final String FASTFORWARD = "FASTFORWARD";

    private MusicPlayer player;

    @Override
    public void onCreate() {
        super.onCreate();
        player = new MusicPlayer(getApplicationContext());
        IntentFilter intFilt = new IntentFilter(BROADCAST_ACTION);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null) {
            String cmd = intent.getStringExtra(CMD_NAME);
            Intent broadcast = new Intent(BROADCAST_ACTION);
            if (START_NEW.equals(cmd)) {
                Audio audio = intent.getParcelableExtra(AUDIO);
                player.playFromURL(audio.getUrl());

                broadcast.putExtra(CMD_STATUS, START_NEW);
                broadcast.putExtra(AUDIO, audio);
            }
            if (PLAY.equals(cmd)) {
                player.play();
                broadcast.putExtra(CMD_STATUS, PLAY);
            }
            if (PAUSE.equals(cmd)) {
                player.pause();
                broadcast.putExtra(CMD_STATUS, PAUSE);
            }
            if (REWIND.equals(cmd)) {
                player.rewind();
                broadcast.putExtra(CMD_STATUS, PLAY);
            }
            if (FASTFORWARD.equals(cmd)) {
                player.fastforward();
                broadcast.putExtra(CMD_STATUS, PLAY);
            }
            sendBroadcast(broadcast);
        }
        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
