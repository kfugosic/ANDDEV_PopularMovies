package com.kfugosic.popularmovies.data;

import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by Kristijan on 07-Mar-18.
 */

public class FavouriteMoviesContract {

    public static final String AUTHORITY = "com.kfugosic.popularmovies";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://"+AUTHORITY);
    public static final String PATH_FAVOURITES = "favourites";

    private FavouriteMoviesContract() {}

    public static final class FavouriteMoviesEntry implements BaseColumns {

        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_FAVOURITES).build();

        public static final String TABLE_NAME = "favourite_movies";

        public static final String COLUMN_TMDBID = "tmdbid";
        public static final String COLUMN_TITLE = "title";
        public static final String COLUMN_OVERVIEW = "overview";
        public static final String COLUMN_POSTER_PATH = "poster_path";
        public static final String COLUMN_USER_RATING = "user_rating";
        public static final String COLUMN_RELEASE_DATE = "release_date";

    }
}
