package com.volkov.alexandr.youtubeaudio.downloader;

import java.net.URL;

/**
 * Created by AlexandrVolkov on 11.06.2017.
 */
public class FailedToDownloadException extends Exception {
    public FailedToDownloadException(URL url, Throwable cause) {
        super("Failed to download page on url = " + url.toString(), cause);
    }
}
