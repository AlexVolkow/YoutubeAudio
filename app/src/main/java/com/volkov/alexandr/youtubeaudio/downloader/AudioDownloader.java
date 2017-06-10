package com.volkov.alexandr.youtubeaudio.downloader;

import android.app.DownloadManager;
import android.net.Uri;
import android.os.Environment;

import java.io.*;
import java.net.URL;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutionException;

/**
 * Created by AlexandrVolkov on 09.06.2017.
 */
public class AudioDownloader {
    private URL url;
    private DownloadManager downloadManager;
    private List<AudioLink> audio = new ArrayList<>();

    private static final String BASE_DIR = Environment.getExternalStorageDirectory() + "/YouTubeAudio_downloads";
    private static final String SLASH = "%2F";

    public AudioDownloader(URL url, DownloadManager downloadManager) {
        this.url = url;
        this.downloadManager = downloadManager;
        File direct = new File(BASE_DIR);
        if (!direct.exists()) {
            direct.mkdirs();
        }
    }

    public void download(String fname) throws IOException, FailedToDownloadException {
        parseUrl();
        if (audio.isEmpty()) {
            throw new IllegalArgumentException("Can not download the audio track for this video (" + url + ")");
        }
        AudioLink audioLink = audio.get(audio.size() - 1);
        Uri downloadLink = Uri.parse(audioLink.url);
        String filename = fname + "." + audioLink.type;

        DownloadManager.Request request = new DownloadManager.Request(downloadLink);
        request.setTitle(fname + " download");
        request.setDescription("File is being downloaded...");
        request.allowScanningByMediaScanner();
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        request.setDestinationInExternalPublicDir(BASE_DIR, filename);

        downloadManager.enqueue(request);
    }

    private void parseUrl() throws FailedToDownloadException {
        Set<String> links = new HashSet<>();
        String html = null;
        try {
            html = downloadPage();
        } catch (Exception e) {
            throw new FailedToDownloadException(url, e);
        }

        for (String st : html.split("url=")) {
            if (st.startsWith("https")) {
                links.add(st.split(",")[0]);
            }
        }

        for (String link : links) {
            String temp = link.replace("\\", "\\\\");
            for (String sdata : temp.split("\\\\")) {
                if (sdata.startsWith("https")) {
                    String url = sdata.split(" ")[0];
                    try {
                        url = URLDecoder.decode(url, "UTF-8");
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                    if (url.contains("&gir=yes")) {
                        String type = extractTag(url, "mime");
                        String itag = extractTag(url, "itag");
                        if (type.startsWith("audio")) {
                            audio.add(new AudioLink(url, type, itag));
                        }
                    }
                }
            }
        }
    }

    private String extractTag(String url, String tag) {
        return (url.split(tag + "=")[1]).split("&")[0];
    }

    private String downloadPage() throws InterruptedException, ExecutionException {
        PageDownloader pd = new PageDownloader();
        pd.execute(url);
        return pd.get();
    }

    private static class AudioLink {
        String url;
        String type;
        String itag;

        public AudioLink(String url, String type, String itag) {
            this.url = url;
            this.type = type.split(SLASH)[1];
            this.itag = itag;
        }
    }
}
