package com.nilesh.popularmovies.data;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Nilesh on 26/02/2018.
 */

public class Movies implements Parcelable {

    private String mPosterPath, mOverview, mReleaseDate, mOrigTitle, mLanguage, mTitle, mVideo;
    private long mID;
    private double mPopularity, mVoteAverage;
    private int mVoteCount;

/*    public Movies(String poster, String overview, String releaseDate, long id, String origTitle, String language,
            String title, double popularity, int voteCount, String video, double voteAverage) {

        this.mPosterPath = poster;
        this.mOverview = overview;
        this.mReleaseDate = releaseDate;
        this.mID = id;
        this.mOrigTitle = origTitle;
        this.mLanguage = language;
        this.mTitle = title;
        this.mPopularity = popularity;
        this.mVoteCount = voteCount;
        this.mVideo = video;
        this.mVoteAverage = voteAverage;

    }
*/

    public Movies(String poster, String overview, String releaseDate, long id, String title, double voteAverage) {

        this.mPosterPath = poster;
        this.mOverview = overview;
        this.mReleaseDate = releaseDate;
        this.mID = id;
        this.mTitle = title;
        this.mVoteAverage = voteAverage;

    }



    public String getPosterPath() {
        return mPosterPath;
    }

    public String getOverview() {
        return mOverview;
    }

    public String getReleaseDate() {
        return mReleaseDate;
    }

    public long getID() {
        return mID;
    }

    public String getOrigTitle() {
        return mOrigTitle;
    }

    public String getLanguage() {
        return mLanguage;
    }

    public String getTitle() {
        return mTitle;
    }

    public double getPopularity() {
        return mPopularity;
    }

    public int getVoteCount() {
        return mVoteCount;
    }


    public String getVideo() {
        return mVideo;
    }

    public double getVoteAverage() {
        return mVoteAverage;
    }




    //Parcelable methods
    private Movies(Parcel in){
        String[] data = new String[6];

        in.readStringArray(data);
        // the order needs to be the same as in writeToParcel() method
        this.mPosterPath = data[0];
        this.mOverview = data[1];
        this.mReleaseDate = data[2];
        this.mID = Long.valueOf(data[3]); //Long.parseLong(data[3], 0);    // String.valueOf();
        this.mTitle = data[4];
        this.mVoteAverage = Double.parseDouble(data[5]);

    }

    @Override
    public int describeContents() {
        //Not Needed
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeStringArray(new String[] {
                this.mPosterPath,
                this.mOverview,
                this.mReleaseDate,
                String.valueOf(this.mID),
                this.mTitle,
                String.valueOf(this.mVoteAverage)
            });
    }


    public static final Parcelable.Creator CREATOR = new Parcelable.Creator() {
        public Movies createFromParcel(Parcel in) {
            return new Movies(in);
        }

        public Movies[] newArray(int size) {
            return new Movies[size];
        }
    };

}
