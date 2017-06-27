package com.volkov.alexandr.youtubeaudio.music;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Date;

/**
 * Created by AlexandrVolkov on 15.06.2017.
 */
public class Audio implements Parcelable{
    private long id;
    private String hash;
    private String title;
    private Date date;
    private long length;
    private double size;
    private String url;

    public Audio(long id, String hash, String title, Date date, long length, double size, String url) {
        this.hash = hash;
        this.title = title;
        this.date = date;
        this.length = length;
        this.size = size;
        this.url = url;
        this.id = id;
    }

    public Audio(String hash, String title, Date date, long length, double size, String url) {
        this.hash = hash;
        this.title = title;
        this.date = date;
        this.length = length;
        this.size = size;
        this.url = url;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getHash() {
        return hash;
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

    public String getCoverUrl() { return "https://img.youtube.com/vi/" + hash + "/0.jpg"; }
    @Override
    public boolean equals(Object o) {

        if (this == o) return true;
        if (!(o instanceof Audio)) return false;

        Audio audio = (Audio) o;

        if (length != audio.length) return false;
        if (Double.compare(audio.size, size) != 0) return false;
        if (hash != null ? !hash.equals(audio.hash) : audio.hash != null) return false;
        if (title != null ? !title.equals(audio.title) : audio.title != null) return false;
        if (date != null ? !date.equals(audio.date) : audio.date != null) return false;
        return url != null ? url.equals(audio.url) : audio.url == null;
    }

    @Override
    public int hashCode() {
        int result;
        long temp;
        result = hash != null ? hash.hashCode() : 0;
        result = 31 * result + (title != null ? title.hashCode() : 0);
        result = 31 * result + (date != null ? date.hashCode() : 0);
        result = 31 * result + (int) (length ^ (length >>> 32));
        temp = Double.doubleToLongBits(size);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        result = 31 * result + (url != null ? url.hashCode() : 0);
        return result;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(hash);
        dest.writeString(title);
        dest.writeSerializable(date);
        dest.writeLong(length);
        dest.writeDouble(size);
        dest.writeString(url);
    }

    public static final Parcelable.Creator<Audio> CREATOR = new Parcelable.Creator<Audio>() {
        public Audio createFromParcel(Parcel in) {
            return new Audio(in);
        }

        public Audio[] newArray(int size) {
            return new Audio[size];
        }
    };

    private Audio(Parcel parcel) {
        hash = parcel.readString();
        title = parcel.readString();
        date = (Date) parcel.readSerializable();
        length = parcel.readLong();
        size = parcel.readDouble();
        url = parcel.readString();
    }

    @Override
    public String toString() {
        return "Audio{" +
                "hash='" + hash + '\'' +
                ", title='" + title + '\'' +
                '}';
    }
}
