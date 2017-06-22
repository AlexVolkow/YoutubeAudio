package com.volkov.alexandr.youtubeaudio.player;

import java.util.Date;

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

    @Override
    public boolean equals(Object o) {

        if (this == o) return true;
        if (!(o instanceof Audio)) return false;

        Audio audio = (Audio) o;

        if (length != audio.length) return false;
        if (Double.compare(audio.size, size) != 0) return false;
        if (id != null ? !id.equals(audio.id) : audio.id != null) return false;
        if (title != null ? !title.equals(audio.title) : audio.title != null) return false;
        if (date != null ? !date.equals(audio.date) : audio.date != null) return false;
        return url != null ? url.equals(audio.url) : audio.url == null;
    }

    @Override
    public int hashCode() {
        int result;
        long temp;
        result = id != null ? id.hashCode() : 0;
        result = 31 * result + (title != null ? title.hashCode() : 0);
        result = 31 * result + (date != null ? date.hashCode() : 0);
        result = 31 * result + (int) (length ^ (length >>> 32));
        temp = Double.doubleToLongBits(size);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        result = 31 * result + (url != null ? url.hashCode() : 0);
        return result;
    }
}
