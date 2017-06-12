package com.volkov.alexandr.youtubeaudio.downloader;

import android.os.Parcel;
import android.os.Parcelable;


/**
 * Created by AlexandrVolkov on 11.06.2017.
 */
public class AudioLink implements Comparable<AudioLink>, Parcelable{
    private String url;
    private String type;
    private String itag;
    private double size;

    public String getUrl() {
        return url;
    }

    public String getType() {
        return type;
    }

    public String getItag() {
        return itag;
    }

    public double getSize() {
        return size;
    }

    public AudioLink(String url, String type, String itag, double size) {
        this.url = url;
        this.type = type;
        this.itag = itag;
        this.size = size;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof AudioLink)) return false;

        AudioLink audioLink = (AudioLink) o;

        if (Double.compare(audioLink.size, size) != 0) return false;
        if (url != null ? !url.equals(audioLink.url) : audioLink.url != null) return false;
        if (type != null ? !type.equals(audioLink.type) : audioLink.type != null) return false;
        return itag != null ? itag.equals(audioLink.itag) : audioLink.itag == null;
    }

    @Override
    public int hashCode() {
        int result;
        long temp;
        result = url != null ? url.hashCode() : 0;
        result = 31 * result + (type != null ? type.hashCode() : 0);
        result = 31 * result + (itag != null ? itag.hashCode() : 0);
        temp = Double.doubleToLongBits(size);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        return result;
    }

    @Override
    public String toString() {
        return "AudioLink{" +
                "url='" + url + '\'' +
                ", type='" + type + '\'' +
                ", itag='" + itag + '\'' +
                ", size=" + size +
                '}';
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
        dest.writeString(itag);
        dest.writeDouble(size);
    }

    public AudioLink(Parcel in) {
        this.url = in.readString();
        this.type = in.readString();
        this.itag = in.readString();
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
