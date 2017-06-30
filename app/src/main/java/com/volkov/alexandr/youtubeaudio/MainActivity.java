package com.volkov.alexandr.youtubeaudio;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.*;
import android.graphics.drawable.Drawable;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import com.squareup.picasso.Picasso;
import com.volkov.alexandr.youtubeaudio.db.DBService;
import com.volkov.alexandr.youtubeaudio.db.DBServiceImpl;
import com.volkov.alexandr.youtubeaudio.downloader.*;
import com.volkov.alexandr.youtubeaudio.music.Audio;
import com.volkov.alexandr.youtubeaudio.music.MusicControls;

import java.util.List;

import static com.volkov.alexandr.youtubeaudio.LogHelper.makeLogTag;
import static com.volkov.alexandr.youtubeaudio.music.MusicService.*;

public class MainActivity extends AppCompatActivity {
    public static final String LOG_TAG = makeLogTag(MainActivity.class);
    private static final int GET_YOUTUBE_URL = 1;

    private Adapter adapter;
    private MusicControls musicControls;
    private DBService dbService;

    private ImageButton play;
    private ImageView cover;
    private Drawable pauseImg;
    private Drawable playImg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        RecyclerView listAudio = (RecyclerView) findViewById(R.id.list_audio);
        listAudio.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        listAudio.setLayoutManager(layoutManager);

        musicControls = new MusicControls(this);

        dbService = new DBServiceImpl(this);

        adapter = new Adapter(this, dbService, musicControls);
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

        play = (ImageButton) findViewById(R.id.play_button);
        cover = (ImageView) findViewById(R.id.cover_image);

        final LinearLayout playerLayout = (LinearLayout) findViewById(R.id.player_layout);

        pauseImg = getResources().getDrawable(R.drawable.exo_controls_pause);
        playImg = getResources().getDrawable(R.drawable.exo_controls_play);

        BroadcastReceiver playerReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent != null) {
                    String status = intent.getStringExtra(CMD_STATUS);
                    if (status != null) {
                        play.setImageDrawable(musicControls.isPlaying() ? pauseImg : playImg);
                        if (START_NEW.equals(status)) {
                            Audio audio = intent.getParcelableExtra(AUDIO);
                            if (audio != null) {
                                Picasso.with(MainActivity.this).load(audio.getCoverUrl()).into(cover);
                            }
                        }
                        if (playerLayout.getVisibility() == View.INVISIBLE) {
                            playerLayout.setVisibility(View.VISIBLE);
                        }
                    }
                }
            }
        };

        IntentFilter intFilt = new IntentFilter(BROADCAST_ACTION);
        registerReceiver(playerReceiver, intFilt);
    }

    public void rewind(View v) {
        musicControls.rewind();
    }

    public void fastforward(View v) {
        musicControls.fastforward();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
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

    public void playPauseListener(View v) {
        if (musicControls.isPlaying()) {
            musicControls.pause();
        } else {
            musicControls.play();
        }
    }

    private void addAudio(String url) {
        try {
            PageParserTask pageParser = new PageParserTask(this);
            pageParser.execute(url);
        } catch (Exception e) {
            Log.e(LOG_TAG, "Failed to download page on this url " + url);
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
        private String url;

        public PageParserTask(Context context) {
            pd = new ProgressDialog(context);
            pd.setMessage("Page downloading...");
            pd.setCancelable(false);
        }

        @Override
        protected Audio doInBackground(String... params) {
            try {
                this.url = params[0];
                Audio audio = PageParser.getAudio(params[0]);
                MediaPlayer mp = MediaPlayer.create(getApplicationContext(), Uri.parse(audio.getUrl()));
                int sec = mp.getDuration() / 1000; // from milisecond to second
                mp.release();
                audio.setLength(sec);
                return audio;
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
            if (audio == null) {
                Log.e(LOG_TAG, "Failed to download page on this url " + url);
                showAlert("Failed to download page on this url " + url);
                return;
            }

            if (!dbService.isAlreadyAdded(audio)) {
                adapter.addItem(audio);
                long id = dbService.addAudio(audio);
                Log.d(LOG_TAG, "Video " + audio.getTitle() + " are added with id = " + id);
            } else {
                Log.i(LOG_TAG, "This video are already added " + audio.getTitle());
                showAlert("This video are already added " + audio.getTitle());
            }
        }
    }
}
