package com.volkov.alexandr.youtubeaudio.network.downloader;

import android.app.DownloadManager;
import android.content.Context;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;

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

    public AudioDownloader(Context context) {
        this.context = context;
        if (downloadManager == null) {
            downloadManager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
        }
        checkDir();
    }

    private void checkDir() {
        File direct = new File(FULL_PATH);
        if (!direct.exists()) {
            if (direct.mkdirs()) {
                Log.i(LOG_TAG, "Directory " + FULL_PATH + " are created");
            }
        }
    }

    public void download(Uri url, String filename) throws IOException {
        File file = new File(FULL_PATH + "/" + filename);
        if (file.exists()) {
            Toast.makeText(context, filename + " are already downloaded", Toast.LENGTH_SHORT).show();
            return;
        }

        DownloadManager.Request request = new DownloadManager.Request(url);
        request.setTitle(filename + " download");
        request.setDescription("File is being downloaded...");
        request.allowScanningByMediaScanner();
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        request.setDestinationInExternalPublicDir(DIR, filename);

        downloadManager.enqueue(request);
        Toast.makeText(context, "Start downloading " + filename, Toast.LENGTH_SHORT).show();
        Log.i(LOG_TAG, "File " + filename + " is being downloaded");
    }
}
