package com.kfugosic.popularmovies.lists;


import android.widget.ImageView;

import com.kfugosic.popularmovies.models.Movie;

public interface MainMovieListItemClickListener {
    void onListItemClick(Movie clickedItem, ImageView imageView);
}
