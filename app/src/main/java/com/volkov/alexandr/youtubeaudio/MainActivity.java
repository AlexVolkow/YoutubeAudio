package com.volkov.alexandr.youtubeaudio;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;
import com.google.android.exoplayer2.ui.SimpleExoPlayerView;
import com.volkov.alexandr.youtubeaudio.downloader.*;
import com.volkov.alexandr.youtubeaudio.player.Audio;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private static final int GET_YOUTUBE_URL = 1;

    private Adapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        RecyclerView listAudio = (RecyclerView) findViewById(R.id.list_audio);
        listAudio.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        listAudio.setLayoutManager(layoutManager);
        adapter = new Adapter(this, new ArrayList<Audio>(),
                (SimpleExoPlayerView) findViewById(R.id.player_view));
        listAudio.setAdapter(adapter);

        Toolbar toolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(toolbar);

        Intent intent = getIntent();
        String action = intent.getAction();
        String type = intent.getType();

        if (Intent.ACTION_SEND.equals(action) && type != null) {
            if ("text/plain".equals(type)) {
                handleSendText(intent);
            }
        }
    }

    void handleSendText(Intent intent) {
        String sharedText = intent.getStringExtra(Intent.EXTRA_TEXT);
        if (sharedText != null) {
            addAudio(sharedText);
        }
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
                Intent intent = new Intent();
                intent.setClass(this, YoutubeLinkActivity.class);
                startActivityForResult(intent, GET_YOUTUBE_URL);
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == GET_YOUTUBE_URL) {
            if (resultCode == RESULT_OK) {
                String url = data.getStringExtra("url");
                addAudio(url);
            }
        }
    }

    private void addAudio(String url) {
        try {
            PageParserTask pageParser = new PageParserTask(this);
            pageParser.execute(url);
        } catch (Exception e) {
            Log.e("FailedDownloading", "Failed to download page on this url " + url);
            showAlert("Failed to download page on this url " + url);
            e.printStackTrace();
        }
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

    private class PageParserTask extends AsyncTask<String, Void, Audio> {
        private ProgressDialog pd;

        public PageParserTask(Context context) {
            pd = new ProgressDialog(context);
            pd.setMessage("Page downloading...");
            pd.setCancelable(false);
        }

        @Override
        protected Audio doInBackground(String... params) {
            try {
                return PageParser.getAudio(params[0]);
            } catch (FailedDownloadException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pd.show();
        }

        @Override
        protected void onPostExecute(Audio audio) {
            super.onPostExecute(audio);
            pd.hide();
            adapter.addItem(audio);
        }
    }
}
