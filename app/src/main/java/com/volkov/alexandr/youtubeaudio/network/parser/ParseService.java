package com.volkov.alexandr.youtubeaudio.network.parser;

import com.volkov.alexandr.youtubeaudio.model.AudioLink;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by AlexandrVolkov on 17.07.2017.
 */
public interface ParseService {
    @GET("process/?type=json")
    Call<AudioLink> parsePage(@Query("url") String url);
}
