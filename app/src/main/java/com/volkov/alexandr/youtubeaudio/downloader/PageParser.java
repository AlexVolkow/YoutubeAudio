package com.volkov.alexandr.youtubeaudio.downloader;

import android.util.Log;

import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutionException;

/**
 * Created by AlexandrVolkov on 11.06.2017.
 */
public class PageParser {
    public static List<AudioLink> getAudioLinks(String html) {
        List<AudioLink> audio = new ArrayList<>();
        Set<String> links = new HashSet<>();
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
                        String typeContent = type.split("%2F")[0];
                        String typeFile = type.split("%2F")[1];
                        String itag = extractTag(url, "itag");
                        if (typeContent.equals("audio")) {
                            SizeOfAudioFile audioSize = new SizeOfAudioFile();
                            try {
                                audioSize.execute(new URL(url));
                                double size = audioSize.get();
                                audio.add(new AudioLink(url, typeFile, itag, size));
                            } catch (InterruptedException | ExecutionException | MalformedURLException e) {
                                e.printStackTrace();
                                Log.e("IOException", "Failed to download page on url = " + url);
                                continue;
                            }
                        }
                    }
                }
            }
        }
        return audio;
    }

    private static String extractTag(String url, String tag) {
        return (url.split(tag + "=")[1]).split("&")[0];
    }
}
