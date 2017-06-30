package com.volkov.alexandr.youtubeaudio.music;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.extractor.ExtractorsFactory;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelection;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.ui.SimpleExoPlayerView;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;

import java.io.File;

import static com.volkov.alexandr.youtubeaudio.LogHelper.makeLogTag;
import static com.volkov.alexandr.youtubeaudio.downloader.AudioDownloader.BASE_DIR;

/**
 * Created by AlexandrVolkov on 20.06.2017.
 */
public class MusicPlayer {
    public static final String LOG_TAG = makeLogTag(MusicPlayer.class);
    private static final int FIVE_SEC = 5000;

    private static DefaultBandwidthMeter bandwidthMeter = new DefaultBandwidthMeter();
    private SimpleExoPlayer player;
    private Context context;
    private static DataSource.Factory dataSourceFactory;
    private static ExtractorsFactory extractorsFactory = new DefaultExtractorsFactory();

    public MusicPlayer(Context context) {
        this.context = context;
        TrackSelection.Factory audioTrackSelectionFactory =
                new AdaptiveTrackSelection.Factory(bandwidthMeter);
        TrackSelector trackSelector =
                new DefaultTrackSelector(audioTrackSelectionFactory);
        player = ExoPlayerFactory.newSimpleInstance(context, trackSelector);
        dataSourceFactory= new DefaultDataSourceFactory(context,
                Util.getUserAgent(context, "YoutubeAudio"), bandwidthMeter);
    }

    public void setPlayerView(SimpleExoPlayerView playerView) {
        playerView.setUseController(true);
        playerView.requestFocus();
        playerView.setControllerShowTimeoutMs(Integer.MAX_VALUE);
        playerView.setPlayer(player);
    }

    public void rewind() {
        player.seekTo(player.getCurrentPosition() - FIVE_SEC);
    }

    public void fastforward() {
        player.seekTo(player.getCurrentPosition() + FIVE_SEC);
    }

    public void play() {
        player.setPlayWhenReady(true);
    }

    public void pause() {
        player.setPlayWhenReady(false);
    }

    public boolean isPlaying() {
        return player.getPlayWhenReady();
    }

    public void playSong(Audio audio) {
        String path = BASE_DIR + "/" + audio.getFileName();
        File file = new File(path);

        Uri uri;
        if (file.exists()) {
            uri = Uri.parse(path);
        } else {
            if (hasConnection(context)) {
                uri = Uri.parse(audio.getUrl());
            } else {
                Toast.makeText(context, "No internet connection", Toast.LENGTH_SHORT).show();
                throw new IllegalStateException("No internet connection");
            }
        }

        MediaSource audioSource = new ExtractorMediaSource(uri,
                dataSourceFactory, extractorsFactory, null, null);

        player.prepare(audioSource);
        player.setPlayWhenReady(true);
        Log.i(LOG_TAG, "Start stream audio on uri " + uri);
    }

    public static boolean hasConnection(final Context context) {
        ConnectivityManager cm = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo wifiInfo = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        if (wifiInfo != null && wifiInfo.isConnected())
        {
            return true;
        }
        wifiInfo = cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        if (wifiInfo != null && wifiInfo.isConnected())
        {
            return true;
        }
        wifiInfo = cm.getActiveNetworkInfo();
        if (wifiInfo != null && wifiInfo.isConnected())
        {
            return true;
        }
        return false;
    }
}
