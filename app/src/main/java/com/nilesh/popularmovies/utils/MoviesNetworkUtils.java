package com.nilesh.popularmovies.utils;

import android.net.Uri;
import android.util.Log;

import com.nilesh.popularmovies.BuildConfig;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

/**
 * Created by Nilesh on 23/02/2018.
 */

public class MoviesNetworkUtils {

    private static final String TAG = MoviesNetworkUtils.class.getSimpleName();

    private static final String MOVIES_DB_URL_BASE = "http://api.themoviedb.org/3/movie/";
    private static final String MOVIES_TRAILERS_VIDEO = "videos";
    private static final String MOVIES_COMMENTS = "reviews";
    private static final String API_KEY = "api_key";
    private static final String API_KEY_VALUE = BuildConfig.THE_MOVIE_DB_API_TOKEN;
    private static final String myKey = "?" + API_KEY + "=" + API_KEY_VALUE;

    /* The format we want our API to return */
    private static final String format = "json";
    final static String FORMAT_PARAM = "mode";

    /**
     * Builds the URL used to talk to the weather server using a location. This location is based
     * on the query capabilities of the weather provider that we are using.
     *
     * @param sortType Sort type: if true "popular" else "top_rated".
     * @return The URL to use to query the weather server.
     */
    public static URL buildUrl(String sortType) {
        Uri builtUri = Uri.parse(MOVIES_DB_URL_BASE).buildUpon()
                .appendEncodedPath(sortType)
                .appendEncodedPath(myKey)
                .build();

        URL url = null;
        try {
            url = new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        Log.v(TAG, "Built URI " + url);

        return url;
    }



    public static URL buildTrailersUrl(String movieId) {
        Uri builtUri = Uri.parse(MOVIES_DB_URL_BASE).buildUpon()
                .appendEncodedPath(movieId)
                .appendPath(MOVIES_TRAILERS_VIDEO)
                .appendQueryParameter(API_KEY, API_KEY_VALUE)
                .build();
        URL url = null;
        try {
            url = new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        Log.v(TAG, "Built Trailers URL " + url);

        return url;
    }



    public static URL buildReviewsUrl(String movieId) {
        Uri builtUri = Uri.parse(MOVIES_DB_URL_BASE).buildUpon()
                .appendEncodedPath(movieId)
                .appendPath(MOVIES_COMMENTS)
                .appendQueryParameter(API_KEY, API_KEY_VALUE)
                .build();
        URL url = null;
        try {
            url = new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        Log.v(TAG, "Built Trailers URL " + url);

        return url;
    }


    /**
     * This method returns the entire result from the HTTP response.
     *
     * @param url The URL to fetch the HTTP response from.
     * @return The contents of the HTTP response, null if no response
     * @throws IOException Related to network and stream reading
     */

    public static String getResponseFromHttpUrl(URL url) throws IOException {
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        try {
            InputStream in = urlConnection.getInputStream();

            Scanner scanner = new Scanner(in);
            scanner.useDelimiter("\\A");

            Log.i(TAG, "getResponseFromHttpUrl: response code - " + urlConnection.getResponseCode());

            boolean hasInput = scanner.hasNext();
            String response = null;

            switch(urlConnection.getResponseCode()) {
                case 401:
                    Log.i(TAG, "getResponseFromHttpUrl: The API Key used is invalid");
                    //notify user?
                    break;
                case 404:
                    Log.i(TAG, "getResponseFromHttpUrl: The resource requested could not be found");
                    //notify user via a toast?
                    break;
                default:
                    if (hasInput) {
                        response = scanner.next();
                    }
                    break;
            }

            scanner.close();
            return response;
        } finally {
            urlConnection.disconnect();
        }
    }

}
