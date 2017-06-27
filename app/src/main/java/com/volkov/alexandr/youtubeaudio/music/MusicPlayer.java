package com.volkov.alexandr.youtubeaudio.music;

import android.content.Context;
import android.net.Uri;
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

/**
 * Created by AlexandrVolkov on 20.06.2017.
 */
public class MusicPlayer {
    private static final int FIVE_SEC = 5000;

    private static DefaultBandwidthMeter bandwidthMeter = new DefaultBandwidthMeter();
    private SimpleExoPlayer player;
    private static DataSource.Factory dataSourceFactory;
    private static ExtractorsFactory extractorsFactory = new DefaultExtractorsFactory();

    public MusicPlayer(Context context) {
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

    public void playFromURL(String url) {
        MediaSource audioSource = new ExtractorMediaSource(Uri.parse(url),
                dataSourceFactory, extractorsFactory, null, null);

        player.prepare(audioSource);
        player.setPlayWhenReady(true);
    }
}
