package com.volkov.alexandr.youtubeaudio.downloader;

import java.util.Date;
import java.util.List;

/**
 * Created by AlexandrVolkov on 15.06.2017.
 */
public class Audio{
    private String id;
    private String title;
    private Date date;
    private long length;
    private double size;
    private String url;

    public Audio(String id, String title, Date date, long length, double size, String url) {
        this.id = id;
        this.title = title;
        this.date = date;
        this.length = length;
        this.size = size;
        this.url = url;
    }

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public Date getDate() {
        return date;
    }

    public long getLength() {
        return length;
    }

    public double getSize() {
        return size;
    }

    public String getUrl() {
        return url;
    }
}
