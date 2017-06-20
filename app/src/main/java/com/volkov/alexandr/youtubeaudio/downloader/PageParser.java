package com.volkov.alexandr.youtubeaudio.downloader;

import android.net.Uri;
import android.net.UrlQuerySanitizer;
import android.util.Log;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.*;
import java.util.concurrent.ExecutionException;

/**
 * Created by AlexandrVolkov on 11.06.2017.
 */
public class PageParser {
    private static final String HOST = "http://www.youtubeinmp3.com/fetch/?format=JSON&video=";
    private static final int DELAY = 10;

    public static Audio getAudio(String yotubeLink) throws MalformedURLException, ExecutionException, InterruptedException, JSONException {
        URL url = new URL(HOST + yotubeLink);

        PageDownloader pg = new PageDownloader();
        SizeOfAudioFile sizeOfAudioFile = new SizeOfAudioFile();

        String page = pg.downloadPage(url);

        Uri uri = Uri.parse(yotubeLink);
        String id = uri.getQueryParameter("v");
        if (id == null) {
            id = uri.getPathSegments().get(0);
        }

        JSONObject jsonObject = new JSONObject(page);
        String title = jsonObject.getString("title");
        long length = Long.parseLong(jsonObject.getString("length"));
        String link = jsonObject.getString("link");

        double size = 0;
        for (int i = 0; i < DELAY; i++) {
            sizeOfAudioFile.execute(new URL(link));
            Double temp = sizeOfAudioFile.get();
            if (temp != null) {
                size = temp;
                break;
            }
        }
        return new Audio(id, title, new Date(), length, size, link);
    }
}
