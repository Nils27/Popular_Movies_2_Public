package com.nilesh.popularmovies.utils;

import android.content.Context;
import android.util.Log;

import com.nilesh.popularmovies.data.MovieReview;
import com.nilesh.popularmovies.data.MovieTrailer;
import com.nilesh.popularmovies.data.Movies;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Nilesh on 23/02/2018.
 */

public class MoviesJsonUtils {

    private static final String TAG = MoviesJsonUtils.class.getSimpleName();

    /**
     * This method parses JSON from a web response and returns an array of Strings
     * describing the weather over various days from the forecast.
     * <p/>
     * Later on, we'll be parsing the JSON into structured data within the
     * getFullWeatherDataFromJson function, leveraging the data we have stored in the JSON. For
     * now, we just convert the JSON into human-readable strings.
     *
     * @param moviesJsonStr JSON response from server
     *
     * @return Array of Strings describing weather data
     *
     * @throws JSONException If JSON data cannot be properly parsed
     */

    private static final String JSON_RESULTS_KEY = "results";
    private static final String JSON_POSTER_PATH_KEY = "poster_path";
    private static final String JSON_OVERVIEW_KEY = "overview";
    private static final String JSON_RELEASE_DATE_KEY = "release_date";
    private static final String JSON_ID_KEY = "id";
    private static final String JSON_ORIG_TITLE_KEY = "original_title";
    private static final String JSON_ORIG_LANG_KEY = "original_language";
    private static final String JSON_TITLE_KEY = "title";
    private static final String JSON_POPULARITY_KEY = "popularity";
    private static final String JSON_VOTE_COUNT_KEY = "vote_count";
    private static final String JSON_VIDEO_KEY = "video";
    private static final String JSON_VOTE_AV_KEY = "vote_average";

    public static List<Movies> getSimpleWeatherStringsFromJson(Context context, String moviesJsonStr) throws JSONException {

        List<Movies> movieList = new ArrayList<>();

        try {
            JSONObject movieJSONObject = new JSONObject(moviesJsonStr);
            JSONArray movieJSONResults = movieJSONObject.getJSONArray(JSON_RESULTS_KEY);
            //Loop over results, create movie objects and add to a list of movies
            for (int i = 0; i < movieJSONResults.length(); i++) {
                JSONObject currentMovie = movieJSONResults.getJSONObject(i);
                String moviePosterPath = currentMovie.optString(JSON_POSTER_PATH_KEY);
                String movieOverview = currentMovie.optString(JSON_OVERVIEW_KEY);
                String movieReleaseDate = currentMovie.optString(JSON_RELEASE_DATE_KEY);
                long movieID = currentMovie.getLong(JSON_ID_KEY);
                String movieOrigTitle = currentMovie.optString(JSON_ORIG_TITLE_KEY);
                String movieLanguage = currentMovie.optString(JSON_ORIG_LANG_KEY);
                String movieTitle = currentMovie.optString(JSON_TITLE_KEY);
                double moviePopularity = currentMovie.getDouble(JSON_POPULARITY_KEY);
                int movieVoteCount = currentMovie.getInt(JSON_VOTE_COUNT_KEY);
                String movieVideo = currentMovie.optString(JSON_VIDEO_KEY);
                double movieVoteAverage = currentMovie.getInt(JSON_VOTE_AV_KEY);

                Movies movie = new Movies(moviePosterPath, movieOverview, movieReleaseDate, movieID, movieTitle, movieVoteAverage);
                movieList.add(movie);

            }

            return movieList;
        } catch (NullPointerException e) {
            Log.i(TAG, "getSimpleWeatherStringsFromJson: There was a null String passed in for parsing");
            return null;
        }
    }


    private static final String JSON_TRAILER_MOVIE_ID = "id";
    private static final String JSON_TRAILER_RESULTS_KEY = "results";
    private static final String JSON_TRAILER_RESULTS_ID = "id";
    private static final String JSON_TRAILER_ISO_639_1 = "iso_639_1";
    private static final String JSON_TRAILER_ISO_3166_1 = "iso_3166_1";
    private static final String JSON_TRAILER_KEY = "key";
    private static final String JSON_TRAILER_NAME = "name";
    private static final String JSON_TRAILER_SITE = "site";
    private static final String JSON_TRAILER_SIZE = "size";
    private static final String JSON_TRAILER_TYPE = "type";

    public static List<MovieTrailer> getMovieTrailers(Context context, String moviesTrailerJsonStr) throws JSONException {

        List<MovieTrailer> movieTrailers = new ArrayList<>();

        JSONObject trailerJSONObject = new JSONObject(moviesTrailerJsonStr);
        String movieID = trailerJSONObject.optString(JSON_TRAILER_MOVIE_ID);
        JSONArray movieTrailerJSONResults = trailerJSONObject.getJSONArray(JSON_TRAILER_RESULTS_KEY);
        //Loop over results, create movie objects and add to a list of movies
        for (int i = 0; i < movieTrailerJSONResults.length(); i++) {
            JSONObject currentTrailer = movieTrailerJSONResults.getJSONObject(i);
            String movieTrailerID = currentTrailer.optString(JSON_TRAILER_RESULTS_ID);
            String movieTrailerISO_639_1 = currentTrailer.optString(JSON_TRAILER_ISO_639_1);
            String movieTrailerISO_3166_1 = currentTrailer.optString(JSON_TRAILER_ISO_3166_1);
            String movieTrailerKey = currentTrailer.optString(JSON_TRAILER_KEY);
            String movieTrailerName = currentTrailer.optString(JSON_TRAILER_NAME);
            String movieTrailerSite = currentTrailer.optString(JSON_TRAILER_SITE);
            int movieTrailerSize = currentTrailer.getInt(JSON_TRAILER_SIZE);
            String movieTrailerType = currentTrailer.optString(JSON_TRAILER_TYPE);


            MovieTrailer trailer = new MovieTrailer(movieTrailerID, movieTrailerISO_639_1, movieTrailerISO_3166_1, movieTrailerKey,
                    movieTrailerName, movieTrailerSite, movieTrailerSize, movieTrailerType);
            movieTrailers.add(trailer);

        }

        return movieTrailers;
    }

//Reviews data example
//    {"id":269149,"page":1,
//      "results":[{
//      "id":"56e4290b92514172c7001002",
//      "author":"Andres Gomez",
//      "content":"One of the best movies Disney has created in the last years. Smart plot with a great background topic talking about the differences, stereotypes, prejudices and joining the tendency of giving women more important roles.\r\n\r\nIt has still several gaps to fill and enhance on the latest point but it is, IMHO, a milestone in the right direction.\r\n\r\nThe characters work pretty well and it is funny when needed and not too full of cheesy songs.",
//      "url":"https://www.themoviedb.org/review/56e4290b92514172c7001002"},
//
//      {"id":"5752fa5d925141187e0021a6",
//      "author":"Reno",
//      "content":"> Try everything (but differently). So Disney has done it again.\r\n\r\nThis beautiful animation came to exist because of coming together of the directors of 'Tangled' and 'Wreck-it-Ralp'. It is Disney who had once again done it, since their rival Pixer is going down in a rapid speed. As a Disney fan since my childhood, I'm very happy for their success in live-shot films and animations, especially for this one.\r\n\r\nOkay, since the revolution of 3D animation over 20 years ago after overthrowing the 2D animation, most of the big productions like Disney, Pixer and Dreamworks with few others never failed to deliver. Believe me, I was not interested in this film when I first saw the teaser and trailer. But they have done great promotions and so the film did awesomely at screens worldwide. I was totally blown away after seeing it, Disney's another unique universal charactered story. From the little children to the grown ups, everybody definitely going to enjoy it.\r\n\r\nAll kinds of animals coming together happens only in cinemas, and that too mostly in animations. But todays kids are very sharp who ask lots of questions, so they had a fine explanation for the doubts regarding putting animals in a same society. It was like the United States, where everyone came from different continents and represents different race. And so in this film every animal came from different land to live together peacefully in a city called Zootopia.\r\n\r\nSo the story begins when Judy the rabbit follows her dream to become a police officer in Zootopia. There she meets Nick the fox, who are actually arch-rival species in the wild, but it was thousands of years ago before adapting the civilisation. So trust is what not promised between them, but they're forced to work together after a small missing person case becomes their prime agenda. Solving the mystery is what brings the end to this wonderful tale.\r\n\r\nThese days animations are not just concentrated on comedies, trying to get us emotionally as well. Maybe that's how they're grabbing the adult audience, especially the families. Shakira's cameo was the highlight, and her song 'Try Everything' helped the get attention from all the corners.\r\n\r\nThe Oscars was concluded just a couple of months ago, but it already feels like the fever is gripping again for the next edition and looks like this film is leading the way for the animation category. I know it's too early, but I hope it wins it. And finally a request for the Disney, bring it on a sequel as soon as possible.\r\n\r\n8/10",
//      "url":"https://www.themoviedb.org/review/5752fa5d925141187e0021a6"}

    private static final String JSON_REVIEWS_MOVIE_ID = "id";
    private static final String JSON_REVIEWS_RESULTS_KEY = "results";
    private static final String JSON_REVIEW_RESULTS_ID = "id";
    private static final String JSON_REVIEW_AUTHOR = "author";
    private static final String JSON_REVIEW_CONTENT = "content";
    private static final String JSON_REVIEW_URL = "url";


    public static List<MovieReview> getMovieReviews(Context context, String moviesReviewsJsonStr) throws JSONException {

        List<MovieReview> movieReviews = new ArrayList<>();

        JSONObject reviewJSONObject = new JSONObject(moviesReviewsJsonStr);
        String movieID = reviewJSONObject.optString(JSON_REVIEWS_MOVIE_ID);
        JSONArray movieReviewJSONResults = reviewJSONObject.getJSONArray(JSON_REVIEWS_RESULTS_KEY);
        //Loop over results, create movie objects and add to a list of movies
        for (int i = 0; i < movieReviewJSONResults.length(); i++) {
            JSONObject currentReview = movieReviewJSONResults.getJSONObject(i);
            String movieReviewID = currentReview.optString(JSON_REVIEW_RESULTS_ID);
            String movieReviewAuthor = currentReview.optString(JSON_REVIEW_AUTHOR);
            String movieReviewContent = currentReview.optString(JSON_REVIEW_CONTENT);
            String movieReviewUrl = currentReview.optString(JSON_REVIEW_URL);

            MovieReview review = new MovieReview(movieReviewID, movieReviewAuthor, movieReviewContent, movieReviewUrl);
            movieReviews.add(review);
        }

        return movieReviews;
    }


}
