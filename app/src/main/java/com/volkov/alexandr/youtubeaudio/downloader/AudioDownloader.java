package com.volkov.alexandr.youtubeaudio.downloader;

import android.app.DownloadManager;
import android.content.ContentResolver;
import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;
import android.webkit.MimeTypeMap;
import android.widget.Toast;

import javax.net.ssl.HttpsURLConnection;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.concurrent.ExecutionException;

import static com.volkov.alexandr.youtubeaudio.LogHelper.makeLogTag;

/**
 * Created by AlexandrVolkov on 09.06.2017.
 */
public class AudioDownloader {
    public static final String LOG_TAG = makeLogTag(AudioDownloader.class);

    private static final String DIR = "/youtubeaudio";
    public static final String BASE_DIR = Environment.getExternalStorageDirectory() + DIR;
    private Uri url;
    private DownloadManager downloadManager;
    private Context context;

    public AudioDownloader(Context context, String url) {
        this.url = Uri.parse(url);
        this.context = context;
        this.downloadManager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
        File direct = new File(BASE_DIR);
        if (!direct.exists()) {
            if (direct.mkdirs()) {
                Log.i(LOG_TAG, "Directory " + BASE_DIR + " are created");
            }
        }
    }

    public void download(String fname, String type) throws IOException, FailedDownloadException {
        String filename = fname + "." + type;

        File file = new File(BASE_DIR + "/" + filename);
        if (file.exists()) {
            Toast.makeText(context, "This file are already downloaded", Toast.LENGTH_SHORT).show();
            return;
        }

        DownloadManager.Request request = new DownloadManager.Request(url);
        request.setTitle(fname + " download");
        request.setDescription("File is being downloaded...");
        request.allowScanningByMediaScanner();
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        request.setDestinationInExternalPublicDir(DIR, filename);

        downloadManager.enqueue(request);
        Toast.makeText(context, "Start downloading...", Toast.LENGTH_SHORT).show();
        Log.i(LOG_TAG, "File " + fname + " is being downloaded");
    }
}
