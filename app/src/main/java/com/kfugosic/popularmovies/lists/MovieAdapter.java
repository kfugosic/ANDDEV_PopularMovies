package com.kfugosic.popularmovies.lists;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.Adapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.kfugosic.popularmovies.R;
import com.kfugosic.popularmovies.models.Movie;
import com.kfugosic.popularmovies.utils.NetworkUtils;
import com.squareup.picasso.Picasso;

import java.util.List;

public class MovieAdapter extends Adapter<MovieAdapter.MovieViewHolder> {

    private List<Movie> mMovies;

    final private ListItemClickListener mOnClickListener;

    public MovieAdapter(List<Movie> movies, ListItemClickListener clickListener) {
        mMovies = movies;
        mOnClickListener = clickListener;
    }

    @Override
    public MovieViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        int layoutId = R.layout.movie_poster;
        LayoutInflater inflater = LayoutInflater.from(context);
        boolean shouldAttachToParentImmidiately = false;

        View view = inflater.inflate(layoutId, parent, shouldAttachToParentImmidiately);
        return new MovieViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MovieViewHolder holder, int position) {
        Movie current = mMovies.get(position);
        String posterUrl = NetworkUtils.buildPosterUrl(current.getPosterPath()).toString();
        holder.loadImage(posterUrl);
    }

    @Override
    public int getItemCount() {
        return mMovies.size();
    }

    class MovieViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public final ImageView posterImageView;

        public MovieViewHolder(View itemView) {
            super(itemView);
            posterImageView = itemView.findViewById(R.id.poster_iv);
            posterImageView.setOnClickListener(this);
        }

        public void loadImage(String posterUrl) {
            Picasso.with(itemView.getContext())
                    .load(posterUrl)
                    .placeholder(R.drawable.loading)
                    .error(R.drawable.ic_do_not_disturb_alt_black_24dp)
                    .into(posterImageView);
        }

        @Override
        public void onClick(View view) {
            int position = getAdapterPosition();
            mOnClickListener.onListItemClick(mMovies.get(position));
        }
    }
}
