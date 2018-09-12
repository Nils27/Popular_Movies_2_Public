package com.nilesh.popularmovies;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.nilesh.popularmovies.data.Movies;

import java.util.ArrayList;
import java.util.List;

import com.squareup.picasso.Picasso;

/**
 * Created by Nilesh on 26/02/2018.
 */

public class MoviesAdapter extends RecyclerView.Adapter<MoviesAdapter.MoviesAdapterViewHolder> {

    private static final String TAG = MoviesAdapter.class.getSimpleName();
    private List<Movies> mMoviesList = new ArrayList<>();
    private Context mContext;

    final private MoviesAdapterOnClickHandler mClickHandler;

    public interface MoviesAdapterOnClickHandler {
        void onMovieClick(Movies movie);
    }


    public MoviesAdapter(@NonNull Context context, List<Movies> listMovies, MoviesAdapterOnClickHandler clickHandler) {
        mContext = context;
        mMoviesList = listMovies;
        mClickHandler = clickHandler;
    }


    @Override
    public MoviesAdapter.MoviesAdapterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        this.mContext = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View view = inflater.inflate(R.layout.movie_item_main, parent, false);
        return new MoviesAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MoviesAdapter.MoviesAdapterViewHolder holder, int position) {
        //Log.i(TAG, "onBindViewHolder: bind data to be called");
        Movies movie = mMoviesList.get(position);
        holder.bindMovie(movie);

    }

    @Override
    public int getItemCount() {
        if (mMoviesList == null) {
            return 0;
        }
        return mMoviesList.size();
    }

    public class MoviesAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        ImageView movieImageView;
        TextView movieName;
        Movies mMovie;

        public MoviesAdapterViewHolder(View itemView) {
            super(itemView);
            movieImageView = itemView.findViewById(R.id.iv_moviePoster);
            movieName = itemView.findViewById(R.id.tv_movieName);

            itemView.setOnClickListener(this);
        }


        public void bindMovie(Movies movie) {
            mMovie = movie;

            long id = movie.getID();
            itemView.setTag(id);
            //Log.d("ID TEST", "ID was " + id);

            Picasso.with(mContext)
                    .load("http://image.tmdb.org/t/p/w500" + mMovie.getPosterPath())
                    .placeholder(android.R.drawable.ic_menu_slideshow)
                    .error(R.drawable.tmd)
                    .into(movieImageView);

            movieName.setText(movie.getTitle());
        }

        @Override
        public void onClick(View v) {
            Log.i(TAG, "onClick: something was clicked");
            Log.i(TAG, "onClick: ID " + v.getTag() + "was clicked");

            Movies mov = mMoviesList.get(getAdapterPosition());
            Log.i(TAG, "Adapter onClick: mov title - " + mov.getTitle());
            Log.i(TAG, "Adapter onClick: mov ID - " + mov.getID());
            mClickHandler.onMovieClick(mov);
        }
    }

    public void changeMoviesList(List<Movies>  newMoviesList) {
        Log.i(TAG, "changeMoviesList: pre change");
        mMoviesList = newMoviesList;
        notifyDataSetChanged();
        Log.i(TAG, "changeMoviesList: list should have updated");
    }

}
