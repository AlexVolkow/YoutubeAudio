package com.volkov.alexandr.youtubeaudio.network.parser;

import android.util.Log;
import com.volkov.alexandr.youtubeaudio.model.Audio;
import com.volkov.alexandr.youtubeaudio.model.AudioLink;
import okhttp3.ResponseBody;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import retrofit2.Converter;
import retrofit2.Retrofit;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import static com.volkov.alexandr.youtubeaudio.utils.LogHelper.makeLogTag;

/**
 * Created by AlexandrVolkov on 17.07.2017.
 */
public class AudioLinkConverter extends Converter.Factory {
    private static final String LOG_TAG = makeLogTag(AudioLinkConverter.class);

    public static AudioLinkConverter create() {
        return new AudioLinkConverter();
    }

    @Override
    public Converter<ResponseBody, ?> responseBodyConverter(Type type, Annotation[] annotations, Retrofit retrofit) {
        return JsonConverter.INSTANCE;
    }

    final static class JsonConverter implements Converter<ResponseBody, AudioLink> {
        public static final JsonConverter INSTANCE = new JsonConverter();

        private static final String AUDIO_NO_VIDEO = "audio - no video";

        @Override
        public AudioLink convert(ResponseBody value) throws IOException {
            AudioLink res = null;
            try {
                res = parse(value.string());
            } catch (JSONException e) {
                Log.e(LOG_TAG,Log.getStackTraceString(e));
            }
            return res;
        }

        private AudioLink parse(String page) throws JSONException{
            JSONObject jsonObject = new JSONObject(page);
            String title = jsonObject.getString("title");

            JSONArray audiosArray = jsonObject.getJSONArray("urls");
            List<Audio> audios = new ArrayList<>();
            for (int i = 0; i < audiosArray.length(); i++) {
                JSONObject audioObject = audiosArray.getJSONObject(i);

                String label = audioObject.getString("label");

                if (label.contains(AUDIO_NO_VIDEO)) {
                    String[] words = label.split(" ");

                    String bit = words[5].substring(1);

                    Audio audio = Audio.newBuilder()
                            .setUrl(audioObject.getString("id"))
                            .setType(words[4])
                            .setBitrate(Integer.valueOf(bit)).build();

                    audios.add(audio);
                }
            }

            Audio audio = mediumQuality(audios);

            return AudioLink.newBuilder()
                    .setTitle(title)
                    .setDate(new Date())
                    .setAudio(audio).build();
        }

        private Audio mediumQuality(List<Audio> audios) {
            List<Audio> files = filter("m4a", audios);
            if (files.isEmpty()) {
                files = audios;
            }

            Collections.sort(files, (o1,o2) -> o1.getBitrate() - o2.getBitrate());
            return files.get(files.size() / 2);
        }

        private static List<Audio> filter(String type, List<Audio> links) {
            List<Audio> res = new ArrayList<>();
            for (Audio link : links) {
                if (link.getType().contains(type)) {
                    res.add(link);
                }
            }
            return res;
        }
    }
}
