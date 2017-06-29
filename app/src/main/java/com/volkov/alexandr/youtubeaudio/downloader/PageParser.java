package com.volkov.alexandr.youtubeaudio.downloader;

import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.util.Log;
import android.util.Pair;
import com.volkov.alexandr.youtubeaudio.music.Audio;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.*;

import static com.volkov.alexandr.youtubeaudio.LogHelper.makeLogTag;

/**
 * Created by AlexandrVolkov on 11.06.2017.
 */
public class PageParser {
    public static final String LOG_TAG = makeLogTag(PageParser.class);

    private static final String HOST = "https://www.saveitoffline.com/process/?url=%s&type=json";
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
                Log.e(LOG_TAG, "Failed to download page on url = " + url.toString());
                e.printStackTrace();
            } finally {
                connection.disconnect();
            }
        } catch (IOException e) {
            Log.e(LOG_TAG, "Failed to download page on url = " + url.toString());
            throw new FailedDownloadException(url.toString(), e);
        }
        return html.toString();
    }

    public static Audio getAudio(String youtubeLink) throws FailedDownloadException {
        try {
            URL url = new URL(String.format(HOST, youtubeLink));

            String page = downloadPage(url);

            Uri uri = Uri.parse(youtubeLink);
            String hash = uri.getQueryParameter("v");
            if (hash == null) {
                hash = uri.getPathSegments().get(0);
            }

            JSONObject jsonObject = new JSONObject(page);
            String title = jsonObject.getString("title");

            JSONArray audios = jsonObject.getJSONArray("urls");
            List<AudioLink> audioLinks = new ArrayList<>();
            for (int i = 0; i < audios.length(); i++) {
                JSONObject audio = (JSONObject) audios.get(i);
                String[] words = audio.get("label").toString().split(" ");
                if (audio.get("label").toString().contains("(audio - no video)")) {
                    String bit = words[5].substring(1);
                    audioLinks.add(new AudioLink(audio.getString("id"), words[4], Integer.valueOf(bit)));
                }
            }

            Collections.sort(audioLinks);
            AudioLink audiolink = audioLinks.get(audioLinks.size() / 2);

            String link = audiolink.url;
            URL audioLink = new URL(link);

            double size = 0;
            for (int i = 0; i < DELAY; i++) {
                Double temp = getSizeOfFile(audioLink);
                if (Math.abs(temp) > 0.0001) {
                    size = temp;
                    break;
                }
            }
            Audio res = new Audio(hash, title, new Date(), size, audiolink.type, link);
            Log.i(LOG_TAG, "Audio file " + res + " successfully parsed");
            return res;
        } catch (Exception e) {
            throw new FailedDownloadException(youtubeLink, e);
        }
    }

    private static class AudioLink implements Comparable<AudioLink>{
        String url;
        String type;
        Integer bitrate;

        public AudioLink(String url, String type, Integer bitrate) {
            this.url = url;
            this.type = type;
            this.bitrate = bitrate;
        }

        @Override
        public int compareTo(AudioLink o) {
            return bitrate - o.bitrate;
        }
    }
}
