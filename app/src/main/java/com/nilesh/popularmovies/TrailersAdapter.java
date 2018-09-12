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

import com.nilesh.popularmovies.data.MovieTrailer;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Nilesh on 07/03/2018.
 */

public class TrailersAdapter extends RecyclerView.Adapter<TrailersAdapter.TrailersAdapterViewHolder> {
    private static final String TAG = TrailersAdapter.class.getSimpleName();
    private List<MovieTrailer> mTrailerList = new ArrayList<>();
    private Context mContext;

    final private TrailersAdapter.TrailersAdapterOnClickHandler mClickHandler;

    public interface TrailersAdapterOnClickHandler {
        void onTrailerClick(MovieTrailer trailer);
    }


    public TrailersAdapter(@NonNull Context context, List<MovieTrailer> listTrailers, TrailersAdapter.TrailersAdapterOnClickHandler clickHandler) {
        mContext = context;
        mTrailerList = listTrailers;
        mClickHandler = clickHandler;
    }

    @Override
    public TrailersAdapter.TrailersAdapterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        this.mContext = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View view = inflater.inflate(R.layout.movie_item_detail_trailers, parent, false);
        return new TrailersAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(TrailersAdapter.TrailersAdapterViewHolder holder, int position) {
        MovieTrailer trailer = mTrailerList.get(position);
        holder.bindTrailer(trailer);
    }

    @Override
    public int getItemCount() {
        if (mTrailerList == null) {
            return 0;
        }
        return mTrailerList.size();
    }


    public class TrailersAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        ImageView trailerImageView;
        TextView trailerDesc;
        MovieTrailer mTrailer;

        public TrailersAdapterViewHolder(View itemView) {
            super(itemView);
            trailerImageView = itemView.findViewById(R.id.iv_trailerImage);
            trailerDesc = itemView.findViewById(R.id.tv_trailerDesc);

            itemView.setOnClickListener(this);
        }


        public void bindTrailer(MovieTrailer trailer) {
            mTrailer = trailer;

            String id = trailer.getID();
            itemView.setTag(id);
            Log.d(TAG, "ID TEST: Trailer ID was - " + id);

            Picasso.with(mContext)
                    .load("http://img.youtube.com/vi/" + trailer.getKey() + "/0.jpg")
                    .placeholder(android.R.drawable.ic_menu_slideshow)
                    .error(R.drawable.tmd)
                    .into(trailerImageView);
            trailerDesc.setText(trailer.getName());
        }

        @Override
        public void onClick(View v) {
            Log.i(TAG, "onClick: something was clicked");
            Log.i(TAG, "onClick: Trailer ID " + v.getTag() + "was clicked");

            MovieTrailer t = mTrailerList.get(getAdapterPosition());
            mClickHandler.onTrailerClick(t);
        }

    }



    public void changeMoviesList(List<MovieTrailer>  newTrailerList) {
        Log.i(TAG, "changeMoviesList: pre change");
        mTrailerList = newTrailerList;
        notifyDataSetChanged();
        Log.i(TAG, "changeMoviesList: list should have updated");
    }

}
