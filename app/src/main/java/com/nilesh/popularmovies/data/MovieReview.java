package com.nilesh.popularmovies.data;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Nilesh on 08/03/2018.
 */

public class MovieReview implements Parcelable {

    private String mID, mAuthor, mContent, mUrl;

    public MovieReview(String id, String author, String content, String url) {
        this.mID = id;
        this.mAuthor = author;
        this.mContent = content;
        this.mUrl = url;
    }

    public String getID() {
        return mID;
    }
    public String getAuthor() {
        return mAuthor;
    }
    public String getContent() {
        return mContent;
    }
    public String getUrl() {
        return mUrl;
    }


    //Implement Parcelable - not actually needed as not passing data through to another class (at this stage)
    protected MovieReview(Parcel in) {
        mID = in.readString();
        mAuthor = in.readString();
        mContent = in.readString();
        mUrl = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mID);
        dest.writeString(mAuthor);
        dest.writeString(mContent);
        dest.writeString(mUrl);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<MovieReview> CREATOR = new Creator<MovieReview>() {
        @Override
        public MovieReview createFromParcel(Parcel in) {
            return new MovieReview(in);
        }

        @Override
        public MovieReview[] newArray(int size) {
            return new MovieReview[size];
        }
    };



}
