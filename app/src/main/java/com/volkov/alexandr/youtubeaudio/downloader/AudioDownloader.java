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
    private static final String BASE_DIR = Environment.getExternalStorageDirectory() + DIR;
    private AudioLink url;
    private DownloadManager downloadManager;

    public AudioDownloader(AudioLink url, DownloadManager downloadManager) {
        this.url = url;
        this.downloadManager = downloadManager;
        File direct = new File(BASE_DIR);
        if (!direct.exists()) {
            direct.mkdirs();
        }
    }

    public void download(String fname) throws IOException, FailedDownloadException {
        Uri downloadLink = Uri.parse(url.getUrl());
        String filename = fname + "." + url.getType();

        DownloadManager.Request request = new DownloadManager.Request(downloadLink);
        request.setTitle(fname + " download");
        request.setDescription("File is being downloaded...");
        request.allowScanningByMediaScanner();
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        request.setDestinationInExternalPublicDir(DIR, filename);

        downloadManager.enqueue(request);
    }
}
