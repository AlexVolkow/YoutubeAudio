package com.volkov.alexandr.youtubeaudio.downloader;

import android.net.Uri;
import android.util.Log;
import com.volkov.alexandr.youtubeaudio.player.Audio;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.*;

/**
 * Created by AlexandrVolkov on 11.06.2017.
 */
public class PageParser {
    private static final String HOST = "http://www.youtubeinmp3.com/fetch/?format=JSON&video=";
    private static final int DELAY = 10;
    private static final int MB = 1048576;


    private static double getSizeOfFile(URL url) throws FailedDownloadException {
        URLConnection con;
        try {
            con = url.openConnection();
        } catch (IOException e) {
            throw new FailedDownloadException(url.toString(), e);
        }
        double len = con.getContentLength();
        return len / MB;
    }

    private static String downloadPage(URL url) throws FailedDownloadException {
        StringBuilder html = new StringBuilder();
        try {
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
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
            throw new FailedDownloadException(url.toString(), e);
        }
        return html.toString();
    }

    public static Audio getAudio(String youtubeLink) throws FailedDownloadException {
        try {
            URL url = new URL(HOST + youtubeLink);

            String page = downloadPage(url);

            Uri uri = Uri.parse(youtubeLink);
            String id = uri.getQueryParameter("v");
            if (id == null) {
                id = uri.getPathSegments().get(0);
            }

            JSONObject jsonObject = new JSONObject(page);
            String title = jsonObject.getString("title");
            long length = Long.parseLong(jsonObject.getString("length"));
            String link = jsonObject.getString("link");
            URL audioLink = new URL(link);

            double size = 0;
            for (int i = 0; i < DELAY; i++) {
                Double temp = getSizeOfFile(audioLink);
                if (Math.abs(temp) > 0.0001) {
                    size = temp;
                    break;
                }
            }
            return new Audio(id, title, new Date(), length, size, link);
        } catch (Exception e) {
            throw new FailedDownloadException(youtubeLink, e);
        }
    }
}
