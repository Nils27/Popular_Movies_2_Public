package com.nilesh.popularmovies;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.nilesh.popularmovies.data.MovieReview;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Nilesh on 08/03/2018.
 */

public class ReviewsAdapter extends RecyclerView.Adapter<ReviewsAdapter.ReviewsAdapterViewHolder> {
    private static final String TAG = ReviewsAdapter.class.getSimpleName();
    private List<MovieReview> mReviewsList = new ArrayList<>();
    private Context mContext;

    final private ReviewsAdapter.ReviewsAdapterOnClickHandler mClickHandler;

    public interface ReviewsAdapterOnClickHandler {
        void onReviewClick(MovieReview review);
    }


    public ReviewsAdapter(@NonNull Context context, List<MovieReview> listReviews, ReviewsAdapter.ReviewsAdapterOnClickHandler clickHandler) {
        mContext = context;
        mReviewsList = listReviews;
        mClickHandler = clickHandler;
    }


    @Override
    public ReviewsAdapter.ReviewsAdapterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        this.mContext = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View view = inflater.inflate(R.layout.movie_item_detail_reviews, parent, false);
        return new ReviewsAdapterViewHolder(view);

    }

    @Override
    public void onBindViewHolder(ReviewsAdapter.ReviewsAdapterViewHolder holder, int position) {
        MovieReview rev = mReviewsList.get(position);
        holder.bindReview(rev);
    }

    @Override
    public int getItemCount() {
        if (mReviewsList == null) {
            return 0;
        }
        return mReviewsList.size();
    }



    public class ReviewsAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView tv_ReviewUser, tv_ReviewContent, tv_ReviewURL;
        MovieReview mReview;

        public ReviewsAdapterViewHolder(View itemView) {
            super(itemView);
            tv_ReviewUser = itemView.findViewById(R.id.tv_reviewUserName);
            tv_ReviewContent = itemView.findViewById(R.id.tv_reviewContent);
            tv_ReviewURL = itemView.findViewById(R.id.tv_reviewLink);

            itemView.setOnClickListener(this);
        }

        public void bindReview(MovieReview review) {
            mReview = review;

            String id = review.getID();
            itemView.setTag(id);
            Log.d(TAG, "TAG ID TEST: Review ID was - " + id);

            tv_ReviewUser.setText(review.getAuthor());
            tv_ReviewURL.setText(review.getUrl());
            tv_ReviewContent.setText(review.getContent());
        }

        @Override
        public void onClick(View v) {
            Log.i(TAG, "onClick: Review ID " + v.getTag() + "was clicked");

            MovieReview r = mReviewsList.get(getAdapterPosition());
            mClickHandler.onReviewClick(r);
        }

    }

    public void changeReviewsList(List<MovieReview>  newReviewsList) {
        Log.i(TAG, "changeReviewsList: pre change");
        mReviewsList = newReviewsList;
        notifyDataSetChanged();
        Log.i(TAG, "changeReviewsList: list should have updated");
    }


}
