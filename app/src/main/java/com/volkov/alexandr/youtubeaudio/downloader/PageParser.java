package com.volkov.alexandr.youtubeaudio.downloader;

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

    public static Audio getAudio(String yotubeLink) throws MalformedURLException, ExecutionException, InterruptedException, JSONException {
        URL url = new URL(HOST + yotubeLink);

        PageDownloader pg = new PageDownloader();
        SizeOfAudioFile sizeOfAudioFile = new SizeOfAudioFile();

        String page = pg.downloadPage(url);

        JSONObject jsonObject = new JSONObject(page);
        String title = jsonObject.getString("title");
        long length = Long.parseLong(jsonObject.getString("length"));
        String link = jsonObject.getString("link");

        sizeOfAudioFile.execute(new URL(link));
        double size = sizeOfAudioFile.get();
        return new Audio(title, new Date(), length, size, link);
    }
}
