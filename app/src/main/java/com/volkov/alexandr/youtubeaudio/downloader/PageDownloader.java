package com.volkov.alexandr.youtubeaudio.downloader;

import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import javax.net.ssl.HttpsURLConnection;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by AlexandrVolkov on 10.06.2017.
 */
public class PageDownloader extends AsyncTask<URL, Void, String> {
    public String res;

    @Override
    protected String doInBackground(URL... params) {
        URL url = params[0];
        StringBuilder html = new StringBuilder();
        try {
            HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
            try {
                connection.setDoOutput(true);
                connection.setRequestMethod("GET");
                InputStream in = connection.getInputStream();
                InputStreamReader reader = new InputStreamReader(in);
                int data;
                while ((data = reader.read()) != -1) {
                    html.append((char) data);
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                connection.disconnect();
            }
        } catch (IOException e) {
            Log.e("IOException", "Failed to download page on url = " + url.toString());
            e.printStackTrace();
        }
        res = html.toString();
        return html.toString();
    }
}
