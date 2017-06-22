package com.volkov.alexandr.youtubeaudio.downloader;

/**
 * Created by AlexandrVolkov on 11.06.2017.
 */
public class FailedDownloadException extends Exception {
    public FailedDownloadException(String url, Throwable cause) {
        super("Failed to download page on url = " + url, cause);
    }
    public FailedDownloadException(String url) {
        super("Failed to download page on url = " + url);
    }
}
