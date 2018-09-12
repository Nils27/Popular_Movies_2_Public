package com.nilesh.popularmovies.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.nilesh.popularmovies.data.MovieContract.MovieEntry;

/**
 * Created by Nilesh on 09/03/2018.
 */

public class MoviesDbHelper extends SQLiteOpenHelper {

    // The name of the database
    private static final String DATABASE_NAME = "moviesDb.db";
    private static final int VERSION = 1;

    // Constructor
    MoviesDbHelper(Context context) {
        super(context, DATABASE_NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        final String CREATE_TABLE = "CREATE TABLE "  +
                MovieEntry.TABLE_NAME + " (" +
                MovieEntry._ID + " INTEGER PRIMARY KEY, " +
                MovieEntry.COLUMN_MOVIE_ID + " TEXT NOT NULL UNIQUE, " + // this field should also be UNIQUE
                MovieEntry.COLUMN_MOVIE_TITLE + " TEXT NOT NULL, " +
                MovieEntry.COLUMN_MOVIE_OVERVIEW + " TEXT, " +
                MovieEntry.COLUMN_MOVIE_USER_RATING + " TEXT, " +
                MovieEntry.COLUMN_MOVIE_RELEASE_DATE + " TEXT, " +
                MovieEntry.COLUMN_MOVIE_POSTER_PATH + " TEXT);";

        db.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + MovieEntry.TABLE_NAME);
        onCreate(db);
    }
}
