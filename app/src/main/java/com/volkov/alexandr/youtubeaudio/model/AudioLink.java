package com.volkov.alexandr.youtubeaudio.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.Date;

import static com.volkov.alexandr.youtubeaudio.utils.AndroidHelper.MAX_PATH;

/**
 * Created by AlexandrVolkov on 15.06.2017.
 */
public class AudioLink implements Parcelable{
    private static final String COVER_URL = "https://img.youtube.com/vi/%s/0.jpg";

    private String hash;
    private Date date;
    private long duration;
    private String title;
    private Audio audio;

    private AudioLink() {
    }

    private AudioLink(Parcel in) {
        hash = in.readString();
        date = (Date) in.readSerializable();
        title = in.readString();
        duration = in.readInt();
        audio = in.readParcelable(Audio.class.getClassLoader());
    }

    public String getFileName() {
        String type = audio.getType();
        String name = title.substring(0, Math.min(title.length(), MAX_PATH - type.length() - 1));
        return name + "." + type;
    }

    public String getHash() {
        return hash;
    }

    public Date getDate() {
        return date;
    }

    public String getTitle() {
        return title;
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public Audio getAudio() {
        return audio;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

    public String getCoverUrl() { return String.format(COVER_URL, hash); }

    public static Builder newBuilder() {
        return new AudioLink().new Builder();
    }

    public class Builder {

        private Builder() {
        }

        public Builder setHash(String hash) {
            AudioLink.this.hash = hash;
            return this;
        }

        public Builder setDate(Date date) {
            AudioLink.this.date = date;
            return this;
        }

        public Builder setTitle(String title) {
            AudioLink.this.title = title;
            return this;
        }

        public Builder setAudio(Audio file) {
            AudioLink.this.audio = file;
            return this;
        }

        public Builder setDuration(long duration) {
            AudioLink.this.duration = duration;
            return this;
        }

        public AudioLink build() {
            return AudioLink.this;
        }
    }


    @Override
    public String toString() {
        return title + "(" + hash + ")";
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(hash);
        dest.writeSerializable(date);
        dest.writeString(title);
        dest.writeParcelable(audio, flags);
    }

    public static final Parcelable.Creator<AudioLink> CREATOR = new Parcelable.Creator<AudioLink>() {
        public AudioLink createFromParcel(Parcel in) {
            return new AudioLink(in);
        }

        public AudioLink[] newArray(int size) {
            return new AudioLink[size];
        }
    };

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof AudioLink)) return false;

        AudioLink audioLink = (AudioLink) o;

        if (duration != audioLink.duration) return false;
        if (hash != null ? !hash.equals(audioLink.hash) : audioLink.hash != null) return false;
        if (date != null ? !date.equals(audioLink.date) : audioLink.date != null) return false;
        if (title != null ? !title.equals(audioLink.title) : audioLink.title != null) return false;
        return audio != null ? audio.equals(audioLink.audio) : audioLink.audio == null;
    }

    @Override
    public int hashCode() {
        int result = hash != null ? hash.hashCode() : 0;
        result = 31 * result + (date != null ? date.hashCode() : 0);
        result = 31 * result + (int) (duration ^ (duration >>> 32));
        result = 31 * result + (title != null ? title.hashCode() : 0);
        result = 31 * result + (audio != null ? audio.hashCode() : 0);
        return result;
    }
}
