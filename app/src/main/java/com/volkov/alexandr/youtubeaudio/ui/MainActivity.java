package com.volkov.alexandr.youtubeaudio.ui;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import butterknife.BindView;
import butterknife.ButterKnife;
import com.jakewharton.threetenabp.AndroidThreeTen;
import com.volkov.alexandr.youtubeaudio.R;
import com.volkov.alexandr.youtubeaudio.db.DBService;
import com.volkov.alexandr.youtubeaudio.db.DBServiceImpl;
import com.volkov.alexandr.youtubeaudio.model.AudioLink;
import com.volkov.alexandr.youtubeaudio.model.AudioManager;
import com.volkov.alexandr.youtubeaudio.network.NetworkService;
import com.volkov.alexandr.youtubeaudio.network.ResponseListener;

import java.io.File;

import static com.volkov.alexandr.youtubeaudio.network.NetworkService.getFullPath;
import static com.volkov.alexandr.youtubeaudio.utils.AndroidHelper.showAlert;
import static com.volkov.alexandr.youtubeaudio.utils.LogHelper.makeLogTag;

public class MainActivity extends AppCompatActivity implements AudioManager {
    private static final String LOG_TAG = makeLogTag(MainActivity.class);

    private static final int GET_YOUTUBE_URL = 1;

    @BindView(R.id.my_toolbar)
    Toolbar toolbar;
    @BindView(R.id.list_audio)
    RecyclerView listAudio;

    private AudioAdapter adapter;
    private DBService dbService;
    private ProgressDialog pd;
    private NetworkService networkService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        AndroidThreeTen.init(this);

        pd = createProgressDialog();
        dbService = new DBServiceImpl(this);
        networkService = new NetworkService(this);

        setSupportActionBar(toolbar);
        initRecyclerView();

        Intent intent = getIntent();
        if (intent != null) {
            String action = intent.getAction();
            String type = intent.getType();

            if (Intent.ACTION_SEND.equals(action) && type != null) {
                if ("text/plain".equals(type)) {
                    handleSendText(intent);
                }
            }
        }
    }

    private void initRecyclerView() {
        listAudio.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        listAudio.setLayoutManager(layoutManager);
        adapter = new AudioAdapter(this, this);
        listAudio.setAdapter(adapter);
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
                intent.setClass(this, AddLinkActivity.class);
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

    @Override
    public void addAudio(String url) {
        if (!pd.isShowing()) {
            pd.show();
        }
        networkService.parsePage(url, new ResponseListener<AudioLink>() {
            @Override
            public void onResponse(AudioLink response) {
                if (pd.isShowing()) {
                    pd.hide();
                }
                if (!dbService.isAlreadyAdded(response)) {
                    adapter.addItem(response);
                    long id = dbService.addLink(response);
                    Log.i(LOG_TAG, "Video " + response.getTitle() + " are added with id = " + id);
                } else {
                    Log.i(LOG_TAG, "This video are already added " + response.getTitle());
                    showAlert(MainActivity.this, "This video are already added " + response.getTitle());
                }
            }

            @Override
            public void onError(Throwable t) {
                Log.e(LOG_TAG, "Failed to download page on this url " + url);
                Log.e(LOG_TAG, Log.getStackTraceString(t));
                showAlert(MainActivity.this, "Failed to download page on this url " + url);
            }
        });
    }

    @Override
    public void deleteAudio(AudioLink link) {
        dbService.deleteLink(link);
        dbService.deleteAudio(link.getAudio());
        File audio = new File(getFullPath(link));
        if (audio.exists()) {
            audio.delete();
            Log.i(LOG_TAG, "Deleted file " + link.getFileName());
        }
    }

    private ProgressDialog createProgressDialog() {
        ProgressDialog pd = new ProgressDialog(this);
        pd.setMessage("Page downloading...");
        pd.setCancelable(false);
        return pd;
    }
}
