package com.nilesh.popularmovies;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.databinding.DataBindingUtil;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.LoaderManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.nilesh.popularmovies.data.MovieContract;
import com.nilesh.popularmovies.data.Movies;
import com.nilesh.popularmovies.databinding.ActivityMainBinding;
import com.nilesh.popularmovies.utils.MoviesJsonUtils;
import com.nilesh.popularmovies.utils.MoviesNetworkUtils;

import org.json.JSONException;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class MainActivityLoader extends AppCompatActivity implements LoaderManager.LoaderCallbacks<List<Movies>>, MoviesAdapter.MoviesAdapterOnClickHandler {

    private static final String TAG = MainActivityLoader.class.getSimpleName();
    private ActivityMainBinding mBindingMain;
    private MoviesAdapter mMoviesAdapter;
    private List<Movies> mMoviesList = new ArrayList<>();
    private String mSortType;

    private final String KEY_RECYCLER_STATE = "recyclerView_state";
    private Parcelable layoutManagerSavedState;

    private static final int MOVIES_LOADER = 27;
    private static final String SORT_TYPE = "SortTypeBoolean";


    private static final int SPINNER_POSITION_0 = 0;
    private static final int SPINNER_POSITION_1 = 1;
    private static final int SPINNER_POSITION_2 = 2;
    private static final String SPINNER_POSITION_SAVED = "SpinnerSelectionState";

    SharedPreferences sharedPref;
    private final String KEY_SORT = "sortOrderChosen";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mBindingMain = DataBindingUtil.setContentView(this, R.layout.activity_main);

        Log.i(TAG, "onCreate: Before recyclerView");

        mBindingMain.moviesRv.setLayoutManager(new GridLayoutManager(this, numberOfColumns(), GridLayoutManager.VERTICAL, false));

        mMoviesAdapter = new MoviesAdapter(MainActivityLoader.this, mMoviesList, this);
        mBindingMain.moviesRv.setAdapter(mMoviesAdapter);
        mBindingMain.moviesRv.setHasFixedSize(true);

        Log.i(TAG, "onCreate: Before AsyncTask");

        //default sort should be popularity
        sharedPref = this.getPreferences(Context.MODE_PRIVATE);
        mSortType = sharedPref.getString(getResources().getString(R.string.sort_order_savedstate), getString(R.string.sort_by_pop)) ;

        if (savedInstanceState!=null) {
            mSortType = (savedInstanceState).getString(KEY_SORT);
        }

        Bundle moviesQueryBundle = new Bundle();
        moviesQueryBundle.putString(SORT_TYPE, mSortType);

        if (!isOnline()) {
            Log.i(TAG, "onCreate: isOnline is false");
            String error = getResources().getString(R.string.isonline_error);
            showErrorMessage(error);
        } else {
            Log.i(TAG, "onCreate: inOnline is true");
            Log.i(TAG, "onCreate: mSortType - " + mSortType);
            //Only query internet if device is connected
            getSupportLoaderManager().initLoader(MOVIES_LOADER, moviesQueryBundle, this);
            Log.i(TAG, "onCreate: After AsyncTask");
        }

    }

    private int numberOfColumns() {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        // You can change this divider to adjust the size of the poster
        int widthDivider = 400;
        int width = displayMetrics.widthPixels;
        int nColumns = width / widthDivider;
        if (nColumns < 2) return 2; //to keep the grid aspect
        return nColumns;
    }

    private void getMoviesData() {
        Log.i(TAG, "getMoviesData: Loader starting");
        Bundle moviesQueryBundle = new Bundle();
        moviesQueryBundle.putString(SORT_TYPE, mSortType);

        Log.i(TAG, "getMoviesData: mSortType - " + mSortType);

        LoaderManager loaderManager = getSupportLoaderManager();
        android.support.v4.content.Loader<List<Movies>> moviesLoader = loaderManager.getLoader(MOVIES_LOADER);

        if (moviesLoader == null) {
            Log.i(TAG, "getMoviesData: initialise loader");
            loaderManager.initLoader(MOVIES_LOADER, moviesQueryBundle, this);
        } else {
            Log.i(TAG, "getMoviesData: restart loader");
            loaderManager.restartLoader(MOVIES_LOADER, moviesQueryBundle, this);
        }

        //In case of screen rotation
        restoreLayoutManagerPosition();
    }

    protected void onSaveInstanceState(Bundle state) {
        // putting recyclerview position
        Log.i(TAG, "onSaveInstanceState: called");
        Parcelable listState = mBindingMain.moviesRv.getLayoutManager().onSaveInstanceState();
        state.putParcelable(KEY_RECYCLER_STATE, listState);
        state.putString(KEY_SORT, mSortType);
        super.onSaveInstanceState(state);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        Log.i(TAG, "onRestoreInstanceState: called");
        if (savedInstanceState != null) {
            Log.i(TAG, "onRestoreInstanceState: savedInstanceState not empty");
            mSortType = (savedInstanceState).getString(KEY_SORT);
            Log.i(TAG, "onRestoreInstanceState: mSortType - " + mSortType);
            layoutManagerSavedState = (savedInstanceState).getParcelable(KEY_RECYCLER_STATE);
        }

        super.onRestoreInstanceState(savedInstanceState);
    }

    private void restoreLayoutManagerPosition() {
        if (layoutManagerSavedState != null) {
            mBindingMain.moviesRv.getLayoutManager().onRestoreInstanceState(layoutManagerSavedState);
        }
    }

    @Override
    public void onMovieClick(Movies mov) {

        Log.i(TAG, "onClick: RecyclerView Item Clicked");
        String movieTitle = mov.getTitle();
        long movieID = mov.getID();
        Log.i(TAG, "onClick: Movie ID - " + movieID);
        Log.i(TAG, "onClick: Movie Name - " + movieTitle);

        Intent detailsScreenIntent = new Intent(this, DetailActivity.class);
        //add parcelable
        detailsScreenIntent.putExtra("Movie", new Movies(
                mov.getPosterPath(),
                mov.getOverview(),
                mov.getReleaseDate(),
                mov.getID(),
                mov.getTitle(),
                mov.getVoteAverage()
            ));

        startActivity(detailsScreenIntent);
    }

    @Override
    public android.support.v4.content.Loader<List<Movies>> onCreateLoader(int id, final Bundle args) {

        Log.i(TAG, "onCreateLoader: Loader START before AsyncTaskLoader");
        return new android.support.v4.content.AsyncTaskLoader<List<Movies>>(this) {

            List<Movies> mMoviesList_new;

            @Override
            protected void onStartLoading() {
                super.onStartLoading();
                Log.i(TAG, "onStartLoading: loader started");
                if (args == null) {
                    Log.i(TAG, "onStartLoading: args bundle has been defaulted to true");
                    return;
                }
                mBindingMain.pbLoadingIndicator.setVisibility(View.VISIBLE);
                if (mMoviesList_new != null) {
                    deliverResult(mMoviesList_new);
                } else {
                    forceLoad();
                }
            }

            @Override
            public List<Movies> loadInBackground() {
                String sort = args.getString(SORT_TYPE);
                Log.i(TAG, "loadInBackground: Get URL");

                URL urlMovies = null;
                Cursor favMovCursor = null;

                Boolean localOrNetwork;

                Log.i(TAG, "loadInBackground: sort - " + sort);
                Log.i(TAG, "loadInBackground: mSortType - " + mSortType);

                if (sort.equals(getString(R.string.sort_by_fav))) {
                    Log.i(TAG, "loadInBackground: Fetching data from the local database");
                    localOrNetwork = true;

                    Uri uri = MovieContract.MovieEntry.CONTENT_URI;
                    try {
                        favMovCursor = getContentResolver().query(uri,
                                null,
                                null,
                                null,
                                MovieContract.MovieEntry._ID);

                    } catch (Exception e) {
                        Log.e(TAG, "Loader - loadInBackground: Failed to query content provider data.");
                        e.printStackTrace();
                    }

                } else if (sort.equals(getString(R.string.sort_by_pop))) {
                    urlMovies = MoviesNetworkUtils.buildUrl("popular");
                    Log.i(TAG, "loadInBackground: URL - " + urlMovies.toString());
                    localOrNetwork = false;

                } else {
                    urlMovies = MoviesNetworkUtils.buildUrl("top_rated");
                    Log.i(TAG, "loadInBackground: URL - " + urlMovies.toString());
                    localOrNetwork = false;

                }

                if (localOrNetwork) { //ContentProvider
                    List<Movies> mMoviesList_temp = new ArrayList<>();
                    for (favMovCursor.moveToFirst(); !favMovCursor.isAfterLast(); favMovCursor.moveToNext()) {
                        String posterpath = favMovCursor.getString(favMovCursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_MOVIE_POSTER_PATH));
                        String overview = favMovCursor.getString(favMovCursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_MOVIE_OVERVIEW));
                        String releaseDate = favMovCursor.getString(favMovCursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_MOVIE_RELEASE_DATE));
                        String moviesId = favMovCursor.getString(favMovCursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_MOVIE_ID));
                        int movId = Integer.valueOf(moviesId);
                        String title = favMovCursor.getString(favMovCursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_MOVIE_TITLE));
                        String voteAverage = favMovCursor.getString(favMovCursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_MOVIE_USER_RATING));
                        double vAvergae = Double.valueOf(voteAverage);

                        Movies movie = new Movies(posterpath, overview, releaseDate, movId, title, vAvergae);
                        mMoviesList_temp.add(movie);
                    }
                    mMoviesList_new = mMoviesList_temp;
                    favMovCursor.close();
                    return mMoviesList_new;
                } else { //False means TMD data from network
                    String jsonWeatherResponse = null;
                    try {
                        Log.i(TAG, "loadInBackground: data from The Movies API fetching started");
                        jsonWeatherResponse = MoviesNetworkUtils.getResponseFromHttpUrl(urlMovies);
                        Log.i(TAG, "loadInBackground: data from The Movies API fetching successful");
                    } catch (Exception e) {
                        Log.i(TAG, "loadInBackground: data from The Movies API fetching error");
                        e.printStackTrace();
                    }

                    try {
                        //jsonWeatherResponse != null - should handle if not connected as the error was parsing a null
                        if (jsonWeatherResponse != null) {
                            Log.i(TAG, "loadInBackground: parsing web data into list of movies");
                            mMoviesList_new = MoviesJsonUtils.getSimpleWeatherStringsFromJson(MainActivityLoader.this, jsonWeatherResponse);
                            Log.i(TAG, "loadInBackground: parsing web data success");
                            return mMoviesList_new;
                        } else {
                            return mMoviesList_new;
                        }

                    } catch (JSONException e) {
                        Log.i(TAG, "loadInBackground: parsing web data Error");
                        e.printStackTrace();

                        return null;
                    }
                }

            }

            @Override
            public void deliverResult(List<Movies> data) {
                mMoviesList_new = data;
                super.deliverResult(data);
            }
        };
    }

    @Override
    public void onLoadFinished(android.support.v4.content.Loader<List<Movies>> loader, List<Movies> data) {

        mBindingMain.pbLoadingIndicator.setVisibility(View.INVISIBLE);

        if (data != null) {
            mMoviesList = data;
            mMoviesAdapter.changeMoviesList(mMoviesList);
            restorePosition();
        } else {
            String error = getResources().getString(R.string.movie_data_error);
            showErrorMessage(error);
        }

    }

    @Override
    public void onLoaderReset(android.support.v4.content.Loader<List<Movies>> loader) {
        //Leave blank
    }

    private void showErrorMessage(String errorMessage) {
        Log.i(TAG, "showErrorMessage: ERROR - " + errorMessage);
        Intent errorIntent = new Intent(this, ErrorScreen.class);
        errorIntent.putExtra("ErrorMessage", errorMessage);
        errorIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(errorIntent);
    }



    private void restorePosition() {
        if (layoutManagerSavedState != null) {
            mBindingMain.moviesRv.getLayoutManager().onRestoreInstanceState(layoutManagerSavedState);
            layoutManagerSavedState = null;
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.sort_pref_menu, menu);

        MenuItem item = menu.findItem(R.id.sortSpinner);
        final Spinner sortSpinner = (Spinner) item.getActionView();

        ArrayAdapter<CharSequence> spinnerAdapter = ArrayAdapter.createFromResource(this,
                R.array.sort_array, R.layout.sort_spinner_item);
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        sortSpinner.setAdapter(spinnerAdapter);

        //saved Spinner state - position
        int pos = sharedPref.getInt(SPINNER_POSITION_SAVED, 0);
        sortSpinner.setSelection(pos);

        //Set listener
        sortSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//                CheckedTextView textView= (CheckedTextView) view;
//                textView.setTextColor(Color.WHITE);
                SharedPreferences.Editor editor = sharedPref.edit();
                switch (position) {
                    case SPINNER_POSITION_0:
                        //Toast.makeText(parent.getContext(), sortSpinner.getSelectedItem().toString() + " - item 1!", Toast.LENGTH_SHORT).show();
                        mSortType = getString(R.string.sort_by_pop);
                        editor.putString(getString(R.string.sort_order_savedstate), getString(R.string.sort_by_pop));
                        editor.putInt(SPINNER_POSITION_SAVED, SPINNER_POSITION_0);
                        editor.commit();
                        getMoviesData();
                        break;
                    case SPINNER_POSITION_1:
                        //Toast.makeText(parent.getContext(), sortSpinner.getSelectedItem().toString() + " - item 2!", Toast.LENGTH_SHORT).show();
                        mSortType = getString(R.string.sort_by_top);
                        editor.putString(getString(R.string.sort_order_savedstate), getString(R.string.sort_by_top));
                        editor.putInt(SPINNER_POSITION_SAVED, SPINNER_POSITION_1);
                        editor.commit();
                        getMoviesData();
                        break;
                    case SPINNER_POSITION_2:
                        //Toast.makeText(parent.getContext(), sortSpinner.getSelectedItem().toString() + " - item 3!", Toast.LENGTH_SHORT).show();
                        mSortType = getString(R.string.sort_by_fav);
                        editor.putString(getString(R.string.sort_order_savedstate), getString(R.string.sort_by_fav));
                        editor.putInt(SPINNER_POSITION_SAVED, SPINNER_POSITION_2);
                        editor.commit();
                        getMoviesData();
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                // sometimes you need nothing here
                Log.i(TAG, "onNothingSelected: Actionbar Spinner - nothing selected");
            }
        });

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        Log.i(TAG, "onOptionsItemSelected: mSort Type: " + mSortType);

        switch (item.getItemId()) {
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public boolean isOnline() {
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }


}

