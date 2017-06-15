package com.volkov.alexandr.youtubeaudio.downloader;

import android.os.Parcel;
import android.os.Parcelable;


/**
 * Created by AlexandrVolkov on 11.06.2017.
 */
public class AudioLink implements Comparable<AudioLink>, Parcelable{
    private String url;
    private String type;
    private double size;

    public String getUrl() {
        return url;
    }

    public String getType() {
        return type;
    }

    public double getSize() {
        return size;
    }

    public AudioLink(String url, String type, double size) {
        this.url = url;
        this.type = type;
        this.size = size;
    }

    @Override
    public int compareTo(AudioLink o) {
        if (size > o.getSize()) {
            return 1;
        }
        if (size < o.getSize()) {
            return -1;
        }
        return 0;
     }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(url);
        dest.writeString(type);
        dest.writeDouble(size);
    }

    public AudioLink(Parcel in) {
        this.url = in.readString();
        this.type = in.readString();
        this.size = in.readDouble();
    }

    public static final Parcelable.Creator<AudioLink> CREATOR = new Parcelable.Creator<AudioLink>() {

        @Override
        public AudioLink createFromParcel(Parcel source) {
            return new AudioLink(source);
        }

        @Override
        public AudioLink[] newArray(int size) {
            return new AudioLink[size];
        }
    };
}
