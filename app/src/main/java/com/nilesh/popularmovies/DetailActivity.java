package com.nilesh.popularmovies;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.databinding.DataBindingUtil;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.support.v4.app.NavUtils;
import android.support.v4.content.Loader;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.ShareActionProvider;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.nilesh.popularmovies.data.MovieContract;
import com.nilesh.popularmovies.data.MovieReview;
import com.nilesh.popularmovies.data.MovieTrailer;
import com.nilesh.popularmovies.data.Movies;
import com.nilesh.popularmovies.databinding.ActivityDetailBinding;
import com.nilesh.popularmovies.utils.MoviesJsonUtils;
import com.nilesh.popularmovies.utils.MoviesNetworkUtils;
import com.squareup.picasso.Picasso;

import org.json.JSONException;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.security.auth.login.LoginException;

/**
 * Created by Nilesh on 27/02/2018.
 */

public class DetailActivity extends AppCompatActivity implements TrailersAdapter.TrailersAdapterOnClickHandler,
            ReviewsAdapter.ReviewsAdapterOnClickHandler {

    private static final String TAG = DetailActivity.class.getSimpleName();

    private static final int TRAILERS_LOADER = 82;
    private static final int REVIEWS_LOADER = 83;
    private static final String MOVIE_ID = "movieID";

    private final String KEY_RECYCLER_STATE_TRAILERS = "recyclerView_state_trailers";
    private final String KEY_RECYCLER_STATE_REVIEWS = "recyclerView_state_reviews";
    private Parcelable layoutManagerSavedState_trailers, layoutManagerSavedState_reviews;

    private static final String KEY_LIFECYCLE_STATE = "detail_callbacks";

    List<MovieTrailer> mMoviesTrailers = new ArrayList<>();
    List<MovieReview> mMoviesReviews = new ArrayList<>();

    private TrailersAdapter mTrailersAdapter;
    private ReviewsAdapter mReviewsAdapter;

    private Context mContext;

    private Movies mMovie;
    private String mMovieID;

    private ShareActionProvider mShareActionProvider;
    private MovieTrailer mTrailerToShare;
    Intent shareIntent;

    private ActivityDetailBinding mBindingDetail;

    private static final String mOutOfTen = "/10";
    private static final String mParcelableMovieKey = "Movie";
    private static final String mPosterPathPrefix = "http://image.tmdb.org/t/p/w500";
    private static final String mExampleText = "Example text";
    private static final String mYouTubePrefix = "vnd.youtube://";
    private static final String mFirstTrailerYouTube = "First Trailer";
    private static final String mShareType = "text/plain";
    private static final int POSITION_0 = 0;
    private static final String mMovieDeletedFav = "Movie Deleted from Fav db - ";
    private static final String mMovieInsertedFav = "Movie insterted into Fav db - ";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        mBindingDetail = DataBindingUtil.setContentView(this, R.layout.activity_detail);

        mContext = getApplicationContext();
        mTrailerToShare = null;

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        Bundle data = getIntent().getExtras();
        mMovie = data.getParcelable(mParcelableMovieKey);

        mBindingDetail.detailsTitle.setText(mMovie.getTitle());
        mBindingDetail.detailsOverview.setText(mMovie.getOverview());
        String ratingOutOfTen = String.valueOf(mMovie.getVoteAverage()) + mOutOfTen;
        mBindingDetail.detailsUserRating.setText(ratingOutOfTen);
        mBindingDetail.detailsReleaseDate.setText(mMovie.getReleaseDate());

        String posterURL = mMovie.getPosterPath();
        Picasso.with(this).load(mPosterPathPrefix + posterURL)
                .placeholder(android.R.drawable.ic_menu_rotate)
                .error(R.drawable.tmd)
                .into(mBindingDetail.detailsPoster);

        mMovieID = String.valueOf(mMovie.getID());

        //Put Trailers into its recyclerView
        mBindingDetail.detailTrailersRv.setLayoutManager(new LinearLayoutManager(this));
        mTrailersAdapter = new TrailersAdapter(this, mMoviesTrailers, this);
        mBindingDetail.detailTrailersRv.setAdapter(mTrailersAdapter);
        mBindingDetail.detailTrailersRv.setHasFixedSize(true);


        //Put Reviews into its recyclerView
        mBindingDetail.detailReviewsRv.setLayoutManager(new LinearLayoutManager(this));
        mReviewsAdapter = new ReviewsAdapter(this, mMoviesReviews, this);
        mBindingDetail.detailReviewsRv.setAdapter(mReviewsAdapter);
        mBindingDetail.detailReviewsRv.setHasFixedSize(true);


        //initialise loader
        Bundle trailerQueryBundle = new Bundle();
        trailerQueryBundle.putString(MOVIE_ID, mMovieID);

        getSupportLoaderManager().initLoader(TRAILERS_LOADER, trailerQueryBundle, trailerLoaderListener);
        getSupportLoaderManager().initLoader(REVIEWS_LOADER, trailerQueryBundle, reviewsLoaderListener);

        Uri uri = MovieContract.MovieEntry.CONTENT_URI;
        uri = uri.buildUpon().appendPath(mMovieID).build();

        if (MovieExists(uri) != null) {
            Log.i(TAG, "onCreate: setting FAV button true");
            changeFABImage(true);
        } else {
            Log.i(TAG, "onCreate: setting FAV button false");
            changeFABImage(false);
        }

        //Restore positions via save and restore InstanceState methods
        restoreLayoutManagerPosition();
    }



    protected void onSaveInstanceState(Bundle state) {
        // putting recyclerview position
        Log.i(TAG, "onSaveInstanceState: called");
        Parcelable listState_trailers = mBindingDetail.detailTrailersRv.getLayoutManager().onSaveInstanceState();
        Parcelable listState_reviews = mBindingDetail.detailReviewsRv.getLayoutManager().onSaveInstanceState();
        state.putParcelable(KEY_RECYCLER_STATE_TRAILERS, listState_trailers);
        state.putParcelable(KEY_RECYCLER_STATE_REVIEWS, listState_reviews);
        super.onSaveInstanceState(state);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        Log.i(TAG, "onRestoreInstanceState: called");
        if (savedInstanceState != null) {
            Log.i(TAG, "onRestoreInstanceState: savedInstanceState not empty");
            layoutManagerSavedState_trailers = (savedInstanceState).getParcelable(KEY_RECYCLER_STATE_TRAILERS);
            layoutManagerSavedState_reviews = (savedInstanceState).getParcelable(KEY_RECYCLER_STATE_REVIEWS);
        }

        super.onRestoreInstanceState(savedInstanceState);
    }

    private void restoreLayoutManagerPosition() {
        if (layoutManagerSavedState_trailers != null) {
            mBindingDetail.detailTrailersRv.getLayoutManager().onRestoreInstanceState(layoutManagerSavedState_trailers);
        }
        if (layoutManagerSavedState_reviews != null) {
            mBindingDetail.detailReviewsRv.getLayoutManager().onRestoreInstanceState(layoutManagerSavedState_reviews);
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.detail_share_menu, menu);

        Log.i(TAG, "onCreateOptionsMenu: before share button set up");
        MenuItem shareItem = menu.findItem(R.id.trailer_share);
        mShareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(shareItem);
        setShareIntent(mExampleText);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    //Sort out sharing first trialer
    private void setShareIntent(String shareTrailerString) {
        Log.i(TAG, "setShareIntent: share method called");
        if (mTrailerToShare != null) {
            shareTrailerString = mMovie.getTitle() + " - " + mTrailerToShare.getName() + ": " + Uri.parse(mYouTubePrefix + mTrailerToShare.getKey()).toString();
        }
        shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.putExtra(Intent.EXTRA_TITLE, mFirstTrailerYouTube);
        shareIntent.putExtra(Intent.EXTRA_TEXT, shareTrailerString);
        shareIntent.setType(mShareType);

        if(mShareActionProvider != null) {
            mShareActionProvider.setShareIntent(shareIntent);
        }

    }

    private LoaderCallbacks<List<MovieTrailer>> trailerLoaderListener
            = new LoaderCallbacks<List<MovieTrailer>>() {

        @Override
        public Loader<List<MovieTrailer>> onCreateLoader(int id, final Bundle args) {
            Log.i(TAG, "onCreateLoader: Loader START before AsyncTaskLoader");
            return new android.support.v4.content.AsyncTaskLoader<List<MovieTrailer>>(mContext) {

                List<MovieTrailer> mMoviesTrailers_new;

                @Override
                protected void onStartLoading() {
                    super.onStartLoading();

                    Log.i(TAG, "onStartLoading: loader started");
                    if (args == null) {
                        Log.i(TAG, "onStartLoading: args bundle is empty");
                        return;
                    }
                    if (mMoviesTrailers_new != null) {
                        deliverResult(mMoviesTrailers_new);
                    } else {
                        forceLoad();
                    }
                }

                @Override
                public List<MovieTrailer> loadInBackground() {
                    String movieId = args.getString(MOVIE_ID);
                    Log.i(TAG, "loadInBackground: Get URL for movie id - " + movieId);
                    URL urlMovieTrailers = MoviesNetworkUtils.buildTrailersUrl(movieId);
                    Log.i(TAG, "loadInBackground: URL - " + urlMovieTrailers.toString());
                    String jsonTrailerResponse = null;


                    //Get the JSON data from The Movies Database
                    try {
                        Log.i(TAG, "loadInBackground: trailer data from The Movies API fetching started");
                        jsonTrailerResponse = MoviesNetworkUtils.getResponseFromHttpUrl(urlMovieTrailers);
                        Log.i(TAG, "loadInBackground: trailer data from The Movies API fetching successful");
                    } catch (Exception e) {
                        Log.i(TAG, "loadInBackground: trailer data from The Movies API fetching error");
                        e.printStackTrace();
                    }

                    //Parse the data to bring back (return) the MovieTrailer list or return Null if there was an issue
                    try {
                        if (jsonTrailerResponse != null) {
                            Log.i(TAG, "loadInBackground: parsing web data into list of movie trailers");
                            mMoviesTrailers_new = MoviesJsonUtils.getMovieTrailers(DetailActivity.this, jsonTrailerResponse);
                            Log.i(TAG, "loadInBackground: parsing web data into list of movie trailers success");
                            return mMoviesTrailers_new;
                        } else {
                            return mMoviesTrailers_new;
                        }

                    } catch (JSONException e) {
                        Log.i(TAG, "loadInBackground: parsing web data into list of movie trailers error");
                        e.printStackTrace();
                        return null;
                    }
                }

                @Override
                public void deliverResult(List<MovieTrailer> data) {
                    mMoviesTrailers_new = data;

                    if (data!=null) {
                        //set the default 1st movie trailer to share
                        mTrailerToShare = data.get(POSITION_0); //Needed at this line?
                        Log.i(TAG, "onLoadFinished: shareIntent extra text - " + mTrailerToShare.getKey());
                        setShareIntent(mTrailerToShare.getKey());
                    }

                    super.deliverResult(data);
                }
            };

        }


        @Override
        public void onLoadFinished(Loader<List<MovieTrailer>> loader, List<MovieTrailer> data) {
            if (data != null) {
                mMoviesTrailers = data;
                mTrailersAdapter.changeMoviesList(mMoviesTrailers);
                //set the default 1st movie trailer to share
                mTrailerToShare = mMoviesTrailers.get(POSITION_0); //Needed at this line?
                Log.i(TAG, "onLoadFinished: shareIntent extra text - " + mTrailerToShare.getKey());
                setShareIntent(mTrailerToShare.getKey());
            } else {
                String error = getResources().getString(R.string.trailer_download_error);
                showErrorMessage(error);
            }
        }

        @Override
        public void onLoaderReset(Loader<List<MovieTrailer>> loader) {
        }

    };

    private void showErrorMessage(String errorMessage) {
        Log.i(TAG, "showErrorMessage: ERROR - " + errorMessage);
        Intent errorIntent = new Intent(this, ErrorScreen.class);
        errorIntent.putExtra("ErrorMessage", errorMessage);
        startActivity(errorIntent);
    }


    private LoaderCallbacks<List<MovieReview>> reviewsLoaderListener
            = new LoaderCallbacks<List<MovieReview>>() {

        @Override
        public Loader<List<MovieReview>> onCreateLoader(int id, final Bundle args) {
            Log.i(TAG, "onCreateLoader: Loader START before AsyncTaskLoader");
            return new android.support.v4.content.AsyncTaskLoader<List<MovieReview>>(mContext) {

                List<MovieReview> mMoviesReviews_new;

                @Override
                protected void onStartLoading() {
                    super.onStartLoading();

                    Log.i(TAG, "onStartLoading: Reviews loader started");
                    if (args == null) {
                        Log.i(TAG, "onStartLoading: Reviews Loader - args bundle is empty");
                        return;
                    }
                    if (mMoviesReviews_new != null) {
                        deliverResult(mMoviesReviews_new);
                    } else {
                        forceLoad();
                    }
                }

                @Override
                public List<MovieReview> loadInBackground() {
                    String movieId = args.getString(MOVIE_ID);
                    Log.i(TAG, "loadInBackground: Get URL to get Reviews for movie id - " + movieId);
                    URL urlMovieReviews = MoviesNetworkUtils.buildReviewsUrl(movieId);
                    Log.i(TAG, "loadInBackground: URL for Reviews - " + urlMovieReviews.toString());
                    String jsonReviewsResponse = null;

                    //Get the JSON data from The Movies Database
                    try {
                        Log.i(TAG, "loadInBackground: review data from The Movies API fetching started");
                        jsonReviewsResponse = MoviesNetworkUtils.getResponseFromHttpUrl(urlMovieReviews);
                        Log.i(TAG, "loadInBackground: review data from The Movies API fetching successful");
                    } catch (Exception e) {
                        Log.i(TAG, "loadInBackground: review data from The Movies API fetching error");
                        e.printStackTrace();
                    }

                    //Parse the data to bring back (return) the MovieTrailer list or return Null if there was an issue
                    try {
                        if (jsonReviewsResponse != null) {
                            Log.i(TAG, "loadInBackground: parsing web data into list of movie reviews");
                            mMoviesReviews_new = MoviesJsonUtils.getMovieReviews(DetailActivity.this, jsonReviewsResponse);
                            Log.i(TAG, "loadInBackground: parsing web data into list of movie reviews success");
                            return mMoviesReviews_new;
                        } else {
                            return mMoviesReviews_new;
                        }

                    } catch (JSONException e) {
                        Log.i(TAG, "loadInBackground: parsing web data into list of movie reviews error");
                        e.printStackTrace();
                        return null;
                    }
                }

                @Override
                public void deliverResult(List<MovieReview> data) {
                    mMoviesReviews_new = data;
                    super.deliverResult(data);
                }

            };

        }


        @Override
        public void onLoadFinished(Loader<List<MovieReview>> loader, List<MovieReview> data) {
            if (data != null) {
                mMoviesReviews = data;
                mReviewsAdapter.changeReviewsList(mMoviesReviews);
            } else {
                String error = getResources().getString(R.string.review_download_error);
                showErrorMessage(error);
            }
        }

        @Override
        public void onLoaderReset(Loader<List<MovieReview>> loader) {
        }

    };



    @Override
    public void onTrailerClick(MovieTrailer trailer) {
        Log.i(TAG, "onTrailerClick: trailer clicked - " + trailer.getID());
        Log.i(TAG, "onTrailerClick: trailer clicked description - " + trailer.getName());

        //"https://www.youtube.com/watch?v=" + key //for playing the trailer
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(mYouTubePrefix + trailer.getKey()));
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        } else {
            Toast.makeText(getBaseContext(), "Trailer cannot be opened. Please check YouTube or equivalent app is available.", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onReviewClick(MovieReview review) {
        Log.i(TAG, "onReviewClick: review ID clicked - " + review.getID());
        Log.i(TAG, "onReviewClick: review clicked user name - " + review.getAuthor());
    }

    public void toggleFav(View view) {
        //Check to see if it was a fav or not - using MovieID (as may have gone through the data fetched from internet)
            //if Fav - delete from the db using the contentprovider & change pic
            //else - insert to the db using the contentprovider & change pic

        //Create cursor with selection args for movieID
            //if null/empty - movie not fav
            //else it is within the db therefore a fav

        Log.i(TAG, "toggleFav:  Started");

        Cursor movieFavCursor = null;

        Uri uri = MovieContract.MovieEntry.CONTENT_URI;
        uri = uri.buildUpon().appendPath(mMovieID).build();

        movieFavCursor = MovieExists(uri);

        if (movieFavCursor != null) {
            Log.i(TAG, "toggleFav: cursor has the data");
            int del = getContentResolver().delete(uri, null, null);

            if (del == 1) {
                //Toast.makeText(getBaseContext(), uri.toString() + mMovieDeletedFav + del, Toast.LENGTH_LONG).show();
                Log.i(TAG, "toggleFav: " + mMovieDeletedFav + " - " + mMovie.getTitle());
                //Log.i(TAG, "toggleFav: Output int Favs DB - Delete - " + logDBContents());
            }

            //toggle start on pic
            changeFABImage(false);

        } else { //Insert current film details as new fav
            Log.i(TAG, "toggleFav: cursor does not have the data - insert");
            ContentValues contentValues = new ContentValues();
            // Put the task description and selected mPriority into the ContentValues
            contentValues.put(MovieContract.MovieEntry.COLUMN_MOVIE_ID, mMovieID);
            contentValues.put(MovieContract.MovieEntry.COLUMN_MOVIE_TITLE, mMovie.getTitle());
            contentValues.put(MovieContract.MovieEntry.COLUMN_MOVIE_OVERVIEW, mMovie.getOverview());
            contentValues.put(MovieContract.MovieEntry.COLUMN_MOVIE_USER_RATING, String.valueOf(mMovie.getVoteAverage()));
            contentValues.put(MovieContract.MovieEntry.COLUMN_MOVIE_RELEASE_DATE, mMovie.getReleaseDate());
            contentValues.put(MovieContract.MovieEntry.COLUMN_MOVIE_POSTER_PATH, mMovie.getPosterPath());

            // Insert the content values via a ContentResolver
            Uri uriInsert = getContentResolver().insert(MovieContract.MovieEntry.CONTENT_URI, contentValues);

            // Display the URI that's returned with a Toast
            // [Hint] Don't forget to call finish() to return to MainActivity after this insert is complete
            if (uriInsert != null) {
                //Toast.makeText(getBaseContext(), uriInsert.toString() +  mMovieInsertedFav, Toast.LENGTH_LONG).show();
                Log.i(TAG, "toggleFav: " + mMovieInsertedFav + " - " + mMovie.getTitle());
                //Log.i(TAG, "toggleFav: Output int Favs DB - Inset - " + logDBContents());
            }

            //toggle start on pic
            changeFABImage(true);
        }


    }

    private Cursor MovieExists(Uri uri) {
        Cursor cursor = null;

        try {
            cursor = getContentResolver().query(uri,
                    null,
                    null,
                    null,
                    MovieContract.MovieEntry._ID);

        } catch (Exception e) {
            Log.e(TAG, "Failed to query content provider data");
            Log.e(TAG, "Failed to asynchronously load data.");
            e.printStackTrace();
        }

        if (cursor != null && cursor.getCount() > 0) { //Delete current film details from favs
            //Movie exists within database
            return cursor;
        } else {
            return null;
        }
    }


    private void changeFABImage(Boolean bool) {
        if (bool) {
            mBindingDetail.fabFav.setImageResource(android.R.drawable.btn_star_big_on);
        } else {
            mBindingDetail.fabFav.setImageResource(android.R.drawable.btn_star_big_off);
        }
    }


    //For testing purposes
    /*
    public int logDBContents() {
        Cursor favMoviesCursor = null;
        Log.i(TAG, "logDBContents: Trying to see whats in the fav moves table");
        Uri uri = MovieContract.MovieEntry.CONTENT_URI;
        try {
            favMoviesCursor = getContentResolver().query(uri,
                    null,
                    null,
                    null,
                    MovieContract.MovieEntry._ID);

        } catch (Exception e) {
            Log.e(TAG, "Failed to query content provider data.");
            Log.e(TAG, "Failed to asynchronously load data.");
            e.printStackTrace();
        }

        if (favMoviesCursor!=null) {
            if (favMoviesCursor.getCount()>0) {
                Log.i(TAG, "logDBContents: cursor size - " + favMoviesCursor.getCount());
                while (favMoviesCursor.moveToNext()) {
                    Log.i(TAG, "logDBContents: row - " + favMoviesCursor.getPosition());
                    Log.i(TAG, "logDBContents: value - " + favMoviesCursor.getString(1));
                    for (int j=1; j<favMoviesCursor.getColumnCount(); j++) {
                        Log.i(TAG, "logDBContents: Cursor field: " + favMoviesCursor.getColumnName(j) + " Value: " + favMoviesCursor.getString(j));
                    }
                }

                Log.i(TAG, "logDBContents: DB Favs for loop finished");
                return 1;
            } else {
                Log.i(TAG, "logDBContents: DB Favs Table Empty");
                return 2;
            }
        } else {
            Log.i(TAG, "logDBContents: DB Favs Cursor is null");
            return 0;
        }


    }
    */

}
