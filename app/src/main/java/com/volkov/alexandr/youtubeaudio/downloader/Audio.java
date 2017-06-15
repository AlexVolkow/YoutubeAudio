package com.volkov.alexandr.youtubeaudio.downloader;

import java.util.Date;
import java.util.List;

/**
 * Created by AlexandrVolkov on 15.06.2017.
 */
public class Audio {
    private String title;
    private Date date;
    private long length;
    private List<AudioLink> qualitys;

    public Audio(String title, Date date, long length, List<AudioLink> qualitys) {
        this.title = title;
        this.date = date;
        this.length = length;
        this.qualitys = qualitys;
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

    public List<AudioLink> getQualitys() {
        return qualitys;
    }
}
