package com.volkov.alexandr.youtubeaudio.model;


import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by AlexandrVolkov on 17.07.2017.
 */

public class Audio implements Parcelable{
    private String url;
    private long size;
    private int bitrate;
    private String type;

    private Audio() {
    }

    private Audio(Parcel in) {
        url = in.readString();
        size = in.readLong();
        type = in.readString();
        bitrate = in.readInt();
    }

    public int getBitrate() {
        return bitrate;
    }

    public String getUrl() {
        return url;
    }

    public long getSize() {
        return size;
    }

    public String getType() {
        return type;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public static Builder newBuilder() {
        return new Audio().new Builder();
    }

    public class Builder {

        private Builder() {
        }

        public Builder setUrl(String url) {
            Audio.this.url = url;
            return this;
        }

       public Builder setSize(long size) {
            Audio.this.size = size;
            return this;
       }

       public Builder setType(String type) {
            Audio.this.type = type;
            return this;
       }
       public Builder setBitrate(int bitrate) {
            Audio.this.bitrate = bitrate;
            return this;
       }

       public Audio build() {
            return Audio.this;
       }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Audio)) return false;

        Audio audio = (Audio) o;

        if (size != audio.size) return false;
        if (bitrate != audio.bitrate) return false;
        if (url != null ? !url.equals(audio.url) : audio.url != null) return false;
        return type != null ? type.equals(audio.type) : audio.type == null;
    }

    @Override
    public int hashCode() {
        int result = url != null ? url.hashCode() : 0;
        result = 31 * result + (int) (size ^ (size >>> 32));
        result = 31 * result + bitrate;
        result = 31 * result + (type != null ? type.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "Audio{" +
                "size=" + size +
                ", bitrate=" + bitrate +
                ", type='" + type + '\'' +
                '}';
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Parcelable.Creator<Audio> CREATOR = new Parcelable.Creator<Audio>() {
        public Audio createFromParcel(Parcel in) {
            return new Audio(in);
        }

        public Audio[] newArray(int size) {
            return new Audio[size];
        }
    };

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(url);
        dest.writeLong(size);
        dest.writeString(type);
        dest.writeInt(bitrate);
    }

}
