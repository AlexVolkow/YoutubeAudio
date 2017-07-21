package com.volkov.alexandr.youtubeaudio.network.parser;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by AlexandrVolkov on 20.07.2017.
 */
public interface DurationService {
    @GET("videos?part=contentDetails")
    Call<Long> getDuration(@Query("key") String apiKey, @Query("id") String id);
}
