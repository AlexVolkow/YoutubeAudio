package com.volkov.alexandr.youtubeaudio.downloader;

import android.app.DownloadManager;
import android.net.Uri;
import android.os.Environment;

import java.io.*;

/**
 * Created by AlexandrVolkov on 09.06.2017.
 */
public class AudioDownloader {
    private static final String DIR = "/YouTubeAudio_downloads";
    private static final String TYPE = "mp3";
    private static final String BASE_DIR = Environment.getExternalStorageDirectory() + DIR;
    private Uri url;
    private DownloadManager downloadManager;

    public AudioDownloader(DownloadManager downloadManager, String url) {
        this.url = Uri.parse(url);
        this.downloadManager = downloadManager;
        File direct = new File(BASE_DIR);
        if (!direct.exists()) {
            direct.mkdirs();
        }
    }

    public void download(String fname) throws IOException, FailedDownloadException {
        String filename = fname + "." + TYPE;

        DownloadManager.Request request = new DownloadManager.Request(url);
        request.setTitle(fname + " download");
        request.setDescription("File is being downloaded...");
        request.allowScanningByMediaScanner();
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        request.setDestinationInExternalPublicDir(DIR, filename);

        downloadManager.enqueue(request);
    }
}
