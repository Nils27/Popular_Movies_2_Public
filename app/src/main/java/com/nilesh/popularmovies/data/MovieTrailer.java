package com.nilesh.popularmovies.data;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Nilesh on 06/03/2018.
 */

public class MovieTrailer implements Parcelable {

    //    {"id":269149,"results":[
    // {"id":"571cb2c0c3a36843150006ed",
    // "iso_639_1":"en",
    // "iso_3166_1":"US",
    // "key":"zQ2XkyDTW34",
    // "name":"Have a Donut",
    // "site":"YouTube",
    // "size":1080,
    // "type":"Clip"},
    // {"id":"571cb2f5c3a36842aa00078c","iso_639_1":"en","iso_3166_1":"US","key":"g9lmhBYB11U","name":"Official US Teaser Trailer","site":"YouTube","size":1080,"type":"Trailer"},
    // {"id":"571cb34bc3a3684e98001257","iso_639_1":"en","iso_3166_1":"US","key":"b8hYj0ROMo4","name":"Elephant in the Room","site":"YouTube","size":1080,"type":"Clip"},
    // {"id":"5797586d9251410639002054","iso_639_1":"en","iso_3166_1":"US","key":"jWM0ct-OLsM","name":"Official US Trailer #2","site":"YouTube","size":1080,"type":"Trailer"},
    // {"id":"58f21fb6c3a3682e95009661","iso_639_1":"en","iso_3166_1":"US","key":"bY73vFGhSVk","name":"Official US Sloth Trailer","site":"YouTube","size":1080,"type":"Trailer"}
    // ]}



    private String mID, mIso_639_1, mIso_3166_1, mKey, mName, mSite, mType;
    private int mSize;


    public MovieTrailer(String id, String iso_639_1, String iso_3166_1, String key, String name, String site, int size, String type) {

        this.mID = id;
        this.mIso_639_1 = iso_639_1;
        this.mIso_3166_1 = iso_3166_1;
        this.mKey = key;
        this.mName = name;
        this.mSite = site;
        this.mSize = size;
        this.mType = type;

    }


    public String getID() {
        return mID;
    }
    public String getIso_639_1() {
        return mIso_639_1;
    }
    public String getIso_3166_1() {
        return mIso_3166_1;
    }
    public String getKey() {
        return mKey;
    }
    public String getName() {
        return mName;
    }
    public String getSite() {
        return mSite;
    }
    public int getSize() {
        return mSize;
    }
    public String getType() {
        return mType;
    }






    private MovieTrailer(Parcel in) {
        String[] data = new String[8];
        in.readStringArray(data);

        this.mID = data[0];
        this.mIso_639_1 = data[1];
        this.mIso_3166_1 = data[2];
        this.mKey = data[3];
        this.mName = data[4];
        this.mSite = data[5];
        this.mSize = Integer.valueOf(data[6]);
        this.mType = data[7];

    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeStringArray(new String[]{
                this.mID,
                this.mIso_639_1,
                this.mIso_3166_1,
                this.mKey,
                this.mName,
                this.mSite,
                String.valueOf(this.mSize),
                this.mType
        });

    }

    @Override
    public int describeContents() {
        return 0;
    }


    public static final Creator<MovieTrailer> CREATOR = new Creator<MovieTrailer>() {
        @Override
        public MovieTrailer createFromParcel(Parcel in) {
            return new MovieTrailer(in);
        }

        @Override
        public MovieTrailer[] newArray(int size) {
            return new MovieTrailer[size];
        }
    };
}
