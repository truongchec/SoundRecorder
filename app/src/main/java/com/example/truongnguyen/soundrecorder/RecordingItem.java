package com.example.truongnguyen.soundrecorder;

import android.os.Parcel;
import android.os.Parcelable;

public class RecordingItem implements Parcelable {
    private String mName;
    private String mFilePath;
    private int mId;
    private int mLength;
    private long mTime;

    public RecordingItem() {
    }

    protected RecordingItem(Parcel in) {
        mName=in.readString();
        mFilePath=in.readString();
        mId=in.readInt();
        mLength=in.readInt();
        mTime=in.readLong();

    }

    public String getName() {
        return mName;
    }

    public void setName(String Name) {
        this.mName = Name;
    }

    public String getFilePath() {
        return mFilePath;
    }

    public void setFilePath(String FilePath) {
        this.mFilePath = FilePath;
    }

    public int getId() {
        return mId;
    }

    public void setId(int Id) {
        this.mId = Id;
    }

    public int getLength() {
        return mLength;
    }

    public void setLength(int Length) {
        this.mLength = Length;
    }

    public long getTime() {
        return mTime;
    }

    public void setTime(long Time) {
        this.mTime = Time;
    }

    public static final Parcelable.Creator<RecordingItem> CREATOR = new Parcelable.Creator<RecordingItem>() {
        @Override
        public RecordingItem createFromParcel(Parcel in) {
            return new RecordingItem(in);
        }

        @Override
        public RecordingItem[] newArray(int size) {
            return new RecordingItem[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(mId);
        dest.writeInt(mLength);
        dest.writeLong(mTime);
        dest.writeString(mFilePath);
        dest.writeString(mName);
    }
}
