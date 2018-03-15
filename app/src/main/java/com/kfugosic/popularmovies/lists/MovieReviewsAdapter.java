package com.kfugosic.popularmovies.lists;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.kfugosic.popularmovies.R;
import com.kfugosic.popularmovies.models.Review;

import java.util.List;

public class MovieReviewsAdapter extends RecyclerView.Adapter<MovieReviewsAdapter.MovieReviewsViewHolder> {

    private List<Review> mReviews;

    final private ListItemClickListener mOnClickListener;

    public MovieReviewsAdapter(List<Review> reviews, ListItemClickListener clickListener) {
        mReviews = reviews;
        mOnClickListener = clickListener;
    }

    @Override
    public MovieReviewsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        int layoutId = R.layout.movie_review;
        LayoutInflater inflater = LayoutInflater.from(context);
        boolean shouldAttachToParentImmidiately = false;
        View view = inflater.inflate(layoutId, parent, shouldAttachToParentImmidiately);
        return new MovieReviewsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MovieReviewsViewHolder holder, int position) {
        Review current = mReviews.get(position);
        holder.fillReview(current.getAuthor(), current.getContent());
    }

    @Override
    public int getItemCount() {
        return mReviews.size();
    }

    public class MovieReviewsViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public final TextView ReviewAuthor;
        public final TextView ReviewContent;

        public MovieReviewsViewHolder(View itemView) {
            super(itemView);
            ReviewAuthor = itemView.findViewById(R.id.review_author_tv);
            ReviewContent = itemView.findViewById(R.id.review_content_tv);
            itemView.findViewById(R.id.movie_review_frame).setOnClickListener(this);
        }

        public void fillReview(String author, String content) {
            ReviewAuthor.setText(author);
            ReviewContent.setText(content);
        }

        @Override
        public void onClick(View view) {
            int position = getAdapterPosition();
            mOnClickListener.onListItemClick(mReviews.get(position));
        }

    }
}
