package com.volkov.alexandr.youtubeaudio.downloader;

import android.os.AsyncTask;

import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;

/**
 * Created by AlexandrVolkov on 11.06.2017.
 */
public class SizeOfAudioFile extends AsyncTask<URL, Void, Double> {
    private static final int MB = 1048576;
    @Override
    protected Double doInBackground(URL... params) {
        URL url = params[0];
        try {
            URLConnection con = url.openConnection();
            double len = con.getContentLength();
            return len / MB;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return 0.0;
    }
}
