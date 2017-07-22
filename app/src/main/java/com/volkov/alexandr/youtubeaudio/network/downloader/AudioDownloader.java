package com.volkov.alexandr.youtubeaudio.network.downloader;

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static com.volkov.alexandr.youtubeaudio.utils.LogHelper.makeLogTag;


/**
 * Created by AlexandrVolkov on 09.06.2017.
 */
public class AudioDownloader {
    private static final String LOG_TAG = makeLogTag(AudioDownloader.class);

    private static final String DIR = "/youtubeaudio";
    public static final String FULL_PATH = Environment.getExternalStorageDirectory() + DIR;

    private static DownloadManager downloadManager;
    private Context context;
    private static Map<Long, DownloadListener> listeners = new HashMap<>();

    public AudioDownloader(Context context) {
        this.context = context;
        if (downloadManager == null) {
            downloadManager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
        }
        checkDir();

        BroadcastReceiver receiver = createReceiver();
        context.registerReceiver(receiver, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
    }

    private void checkDir() {
        File direct = new File(FULL_PATH);
        if (!direct.exists()) {
            if (direct.mkdirs()) {
                Log.i(LOG_TAG, "Directory " + FULL_PATH + " are created");
            }
        }
    }

    private BroadcastReceiver createReceiver() {
        return new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String action = intent.getAction();
                if (DownloadManager.ACTION_DOWNLOAD_COMPLETE.equals(action)) {
                    long downloadId = intent.getLongExtra(
                            DownloadManager.EXTRA_DOWNLOAD_ID, 0);
                    DownloadManager.Query query = new DownloadManager.Query();
                    query.setFilterById(downloadId);
                    Cursor c = downloadManager.query(query);
                    if (c.moveToFirst()) {
                        int columnIndex = c.getColumnIndex(DownloadManager.COLUMN_STATUS);
                        if (DownloadManager.STATUS_SUCCESSFUL == c.getInt(columnIndex)) {
                            DownloadListener listener = listeners.get(downloadId);
                            listener.onDownloadCompleted();
                        }
                    }
                    c.close();
                }
            }
        };
    }

    public boolean isDownloaded(String filename) {
        File file = new File(FULL_PATH + "/" + filename);
        return file.exists();
    }
    
    public void download(Uri url, String filename, DownloadListener listener) throws IOException {
        if (isDownloaded(filename)) {
            Toast.makeText(context, filename + " are already downloaded", Toast.LENGTH_SHORT).show();
            return;
        }

        DownloadManager.Request request = new DownloadManager.Request(url);
        request.setTitle(filename + " download");
        request.setDescription("File is being downloaded...");
        request.allowScanningByMediaScanner();
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        request.setDestinationInExternalPublicDir(DIR, filename);

        long id = downloadManager.enqueue(request);
        listeners.put(id, listener);
        Toast.makeText(context, "Start downloading " + filename, Toast.LENGTH_SHORT).show();
        Log.i(LOG_TAG, "File " + filename + " is being downloaded (id = " + id + ")");
    }

    public interface DownloadListener {
        void onDownloadCompleted();
    }
}
