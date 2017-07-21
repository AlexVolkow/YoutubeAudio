package com.volkov.alexandr.youtubeaudio.network.parser;

import retrofit2.Retrofit;


/**
 * Created by AlexandrVolkov on 17.07.2017.
 */
public class ApiFactory {
    private static final String AUDIO_BASE_URL = "https://www.saveitoffline.com/";
    private static final String DURATION_BASE_URL = "https://www.googleapis.com/youtube/v3/";

    private static volatile ParseService sParse;
    private static volatile  DurationService sDuration;

    private ApiFactory() {
    }

    public static ParseService getParseService() {
        ParseService service = sParse;
        if (service == null) {
            synchronized (ApiFactory.class) {
                service = sParse;
                if (service == null) {
                    service = sParse = buildRetrofitParse().create(ParseService.class);
                }
            }
        }
        return service;
    }

    public static DurationService getDurationService() {
        DurationService service = sDuration;
        if (service == null) {
            synchronized (ApiFactory.class) {
                service = sDuration;
                if (service == null) {
                    service = sDuration = buildRetrofitDuration().create(DurationService.class);
                }
            }
        }
        return service;
    }

    private static Retrofit buildRetrofitParse() {
        return new Retrofit.Builder()
                .baseUrl(AUDIO_BASE_URL)
                .addConverterFactory(AudioLinkConverter.create())
                .build();
    }

    private static Retrofit buildRetrofitDuration() {
        return new Retrofit.Builder()
                .baseUrl(DURATION_BASE_URL)
                .addConverterFactory(DurationConverter.create())
                .build();
    }
}
