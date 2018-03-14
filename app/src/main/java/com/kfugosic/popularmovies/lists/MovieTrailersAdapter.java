package com.kfugosic.popularmovies.lists;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.kfugosic.popularmovies.R;
import com.kfugosic.popularmovies.models.Trailer;
import com.kfugosic.popularmovies.utils.NetworkUtils;
import com.squareup.picasso.Picasso;

import java.util.List;

public class MovieTrailersAdapter extends RecyclerView.Adapter<MovieTrailersAdapter.MovieTrailersViewHolder> {

    private List<Trailer> mTrailers;

    final private ListItemClickListener mOnClickListener;

    public MovieTrailersAdapter(List<Trailer> trailers, ListItemClickListener clickListener) {
        mTrailers = trailers;
        mOnClickListener = clickListener;
    }

    @Override
    public MovieTrailersViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        int layoutId = R.layout.movie_trailer;
        LayoutInflater inflater = LayoutInflater.from(context);
        boolean shouldAttachToParentImmidiately = false;

        View view = inflater.inflate(layoutId, parent, shouldAttachToParentImmidiately);
        return new MovieTrailersViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MovieTrailersViewHolder holder, int position) {
        Trailer current = mTrailers.get(position);
        String posterUrl = NetworkUtils.buildYoutubeThumbnailUrl(current.getKey()).toString();
        holder.loadImage(posterUrl);
        holder.setTrailerName(current.getName());
    }

    @Override
    public int getItemCount() {
        return mTrailers.size();
    }

    public class MovieTrailersViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public final ImageView trailerImageView;
        public final TextView trailerTextView;

        public MovieTrailersViewHolder(View itemView) {
            super(itemView);
            trailerImageView = itemView.findViewById(R.id.trailer_iv);
            trailerTextView = itemView.findViewById(R.id.trailer_name_tv);
            trailerImageView.setOnClickListener(this);
        }

        public void loadImage(String posterUrl) {
            Picasso.with(itemView.getContext())
                    .load(posterUrl)
                    .placeholder(R.drawable.loading)
                    .error(R.drawable.ic_do_not_disturb_alt_black_24dp)
                    .into(trailerImageView);
        }

        public void setTrailerName(String name) {
            trailerTextView.setText(name);
        }

        @Override
        public void onClick(View view) {
            int position = getAdapterPosition();
            mOnClickListener.onListItemClick(mTrailers.get(position));
        }

    }
}
