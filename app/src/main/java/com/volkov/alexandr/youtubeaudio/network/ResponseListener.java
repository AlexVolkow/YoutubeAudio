package com.volkov.alexandr.youtubeaudio.network;


/**
 * Created by AlexandrVolkov on 07.07.2017.
 */
public interface ResponseListener<T> {
    void onResponse(T response);

    void onError(Throwable t);
}
