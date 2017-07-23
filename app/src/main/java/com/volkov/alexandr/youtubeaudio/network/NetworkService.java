package com.volkov.alexandr.youtubeaudio.network;

import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;
import com.volkov.alexandr.youtubeaudio.model.Audio;
import com.volkov.alexandr.youtubeaudio.model.AudioLink;
import com.volkov.alexandr.youtubeaudio.network.downloader.AudioDownloader;
import com.volkov.alexandr.youtubeaudio.network.parser.ApiFactory;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.util.concurrent.ExecutionException;

import static com.volkov.alexandr.youtubeaudio.network.downloader.AudioDownloader.FULL_PATH;
import static com.volkov.alexandr.youtubeaudio.utils.AndroidHelper.fromByteToMb;
import static com.volkov.alexandr.youtubeaudio.utils.AndroidHelper.showAlert;
import static com.volkov.alexandr.youtubeaudio.utils.LogHelper.makeLogTag;


/**
 * Created by AlexandrVolkov on 18.07.2017.
 */
public class NetworkService {
    private static final String LOG_TAG = makeLogTag(NetworkService.class);

    private static final String YOUTUBE_API_KEY = "AIzaSyAo8DD2w2qrEw2Pkm7pYB7Mhd-N5yAiCvU";
    private static final int DELAY = 5;

    private Context context;
    private AudioDownloader audioDownloader;

    public NetworkService(Context context) {
        this.context = context;
        this.audioDownloader = new AudioDownloader(context);
    }

    public void parsePage(String url, ResponseListener<AudioLink> listener) {
        final String hash = parseHash(url);

        ApiFactory.getParseService().parsePage(url).enqueue(new Callback<AudioLink>() {
            @Override
            public void onResponse(Call<AudioLink> call, Response<AudioLink> response) {
                AudioLink audioLink = response.body();
                audioLink.setHash(hash);

                try {
                    Long duration = getDuration(hash);
                    audioLink.setDuration(duration);

                    Long size = getFileSize(audioLink.getAudio());
                    audioLink.getAudio().setSize(size);
                } catch (InterruptedException | ExecutionException e) {
                    Log.e(LOG_TAG, Log.getStackTraceString(e));
                    showAlert(context, "Failed to parse page on this url " + url);
                }

                Log.i(LOG_TAG, "Parsed page on url = " + url);
                listener.onResponse(audioLink);
            }

            @Override
            public void onFailure(Call<AudioLink> call, Throwable t) {
                listener.onError(t);
            }
        });
    }

    private long getDuration(String hash) throws ExecutionException, InterruptedException {
        return new DurationTask().execute(hash).get();
    }

    public void download(AudioLink audioLink, AudioDownloader.DownloadListener listener) {
        String fileName = audioLink.getFileName();
        Uri url = Uri.parse(audioLink.getAudio().getUrl());
        try {
            audioDownloader.download(url, fileName, listener);
        } catch (IOException e) {
            Log.e(LOG_TAG, Log.getStackTraceString(e));
        }
    }

    public boolean isDownloaded(AudioLink audioLink) {
        return audioDownloader.isDownloaded(audioLink.getFileName());
    }

    private static String parseHash(String url) {
        Uri uri = Uri.parse(url);
        if (uri.getQueryParameter("v") == null) {
            return uri.getPathSegments().get(0);
        } else {
            return uri.getQueryParameter("v");
        }
    }

    private long getFileSize(Audio audio) throws ExecutionException, InterruptedException {
        return new FileSizeTask().execute(audio).get();
    }


    private class FileSizeTask extends AsyncTask<Audio, Void, Long> {
        @Override
        protected Long doInBackground(Audio... params) {
            try {
                URL url = new URL(params[0].getUrl());
                long res = 0;
                for (int i = 0; i < DELAY; i++) {
                    URLConnection urlConnection = url.openConnection();
                    urlConnection.connect();
                    res = urlConnection.getContentLength();
                    if (Math.abs(fromByteToMb(res)) > 0.0001) {
                        break;
                    }
                }
                return res;
            } catch (IOException e) {
                Log.e(LOG_TAG, Log.getStackTraceString(e));
            }
            return 0L;
        }
    }

    public static String getFullPath(AudioLink audioLink) {
        return FULL_PATH + "/" + audioLink.getFileName();
    }

    private class DurationTask extends AsyncTask<String, Void, Long> {
        @Override
        protected Long doInBackground(String... params) {
            try {
                return ApiFactory.getDurationService().getDuration(YOUTUBE_API_KEY, params[0]).execute().body();
            } catch (IOException e) {
                Log.e(LOG_TAG, Log.getStackTraceString(e));
            }
            return 0L;
        }
    }
}
