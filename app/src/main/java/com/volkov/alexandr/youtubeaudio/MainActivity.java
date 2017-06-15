package com.volkov.alexandr.youtubeaudio;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
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
import com.google.android.exoplayer2.upstream.*;
import com.google.android.exoplayer2.util.Util;
import com.volkov.alexandr.youtubeaudio.downloader.*;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class MainActivity extends AppCompatActivity {
    private RecyclerView listAudio;
    private Adapter adapter;
    private RecyclerView.LayoutManager layoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        listAudio = (RecyclerView) findViewById(R.id.list_audio);
        listAudio.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        listAudio.setLayoutManager(layoutManager);
        adapter = new Adapter(new ArrayList<Audio>(), this);
        listAudio.setAdapter(adapter);

        Toolbar toolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(toolbar);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings:
                return true;

            case R.id.action_add:
                List<AudioLink> links;
                try {
                    links = getAudioLinksFromUrl("https://www.youtube.com/watch?v=fOJu-fgvhgg");
                    if (links.isEmpty()) {
                        showAlert("Failed to download audio from this video");
                        return false;
                    }
                    Audio audio = new Audio("Some video", new Date(), 240, links);
                    adapter.addItem(audio);
                } catch (MalformedURLException | FailedDownloadException e) {
                    e.printStackTrace();
                }
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private List<AudioLink> getAudioLinksFromUrl(String link) throws MalformedURLException, FailedDownloadException {
        URL url = new URL(link);

        PageDownloader pageDownloader = new PageDownloader();

        String html;
        try {
            html = pageDownloader.downloadPage(url);
        }catch (InterruptedException| ExecutionException e) {
            showAlert("Failed to download page on this url");
            throw new FailedDownloadException(url);
        }
        return PageParser.getAudioLinks(html);
    }

    public void showAlert(String msg) {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("Failed downloading")
                .setMessage(msg)
                .setCancelable(false)
                .setNegativeButton("OK",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });
        AlertDialog alert = builder.create();
        alert.show();
    }
}
