package com.volkov.alexandr.youtubeaudio.network.parser;

import okhttp3.ResponseBody;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.threeten.bp.Duration;
import retrofit2.Converter;
import retrofit2.Retrofit;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;

/**
 * Created by AlexandrVolkov on 20.07.2017.
 */
public class DurationConverter extends Converter.Factory {
    public static DurationConverter create() {
        return new DurationConverter();
    }

    @Override
    public Converter<ResponseBody, ?> responseBodyConverter(Type type, Annotation[] annotations, Retrofit retrofit) {
        return JsonConverter.INSTANCE;
    }

    final static class JsonConverter implements Converter<ResponseBody, Long> {
        private static final JsonConverter INSTANCE = new JsonConverter();

        @Override
        public Long convert(ResponseBody value) throws IOException {
            try {
                JSONObject json = new JSONObject(value.string());
                JSONArray items = json.getJSONArray("items");
                JSONObject contentDetails = items.getJSONObject(0).getJSONObject("contentDetails");
                String durationStr = contentDetails.getString("duration");
                Duration duration = Duration.parse(durationStr);
                return duration.getSeconds();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return 0L;
        }
    }
}
