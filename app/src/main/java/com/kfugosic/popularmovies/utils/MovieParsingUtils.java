package com.kfugosic.popularmovies.utils;

import android.database.Cursor;

import com.kfugosic.popularmovies.data.FavouriteMoviesContract;
import com.kfugosic.popularmovies.models.Movie;
import com.kfugosic.popularmovies.models.Review;
import com.kfugosic.popularmovies.models.Trailer;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class MovieParsingUtils {

    private static final String JSON_ALL_RESULTS = "results";
    private static final String JSON_ID = "id";
    private static final String JSON_MOVIE_TITLE = "original_title";
    private static final String JSON_MOVIE_OVERVIEW = "overview";
    private static final String JSON_POSTER_PATH = "poster_path";
    private static final String JSON_USER_RATING = "vote_average";
    private static final String JSON_RELEASE_DATE = "release_date";

    private static final String JSON_TRAILER_KEY = "key";
    private static final String JSON_TRAILER_NAME = "name";
    private static final String JSON_TRAILER_SITE = "site";

    private static final String JSON_REVIEW_AUTHOR = "author";
    private static final String JSON_REVIEW_CONTENT = "content";
    private static final String JSON_REVIEW_URL = "url";


    public static ArrayList<Trailer> parseTrailersJson(String json) {
        ArrayList<Trailer> trailers = new ArrayList<>();
        if (json == null) {
            return trailers;
        }
        try {
            JSONObject trailersJson = new JSONObject(json);
            JSONArray allTrailers = trailersJson.optJSONArray(JSON_ALL_RESULTS);
            for (int i = 0; i < allTrailers.length(); i++) {
                JSONObject currentTrailer = allTrailers.getJSONObject(i);
                Trailer newTrailer = new Trailer(
                        currentTrailer.optString(JSON_ID),
                        currentTrailer.optString(JSON_TRAILER_KEY),
                        currentTrailer.optString(JSON_TRAILER_NAME),
                        currentTrailer.optString(JSON_TRAILER_SITE)
                );
                trailers.add(newTrailer);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return trailers;
    }

    public static ArrayList<Review> parseReviewsJson(String json) {
        ArrayList<Review> reviews = new ArrayList<>();
        if (json == null) {
            return reviews;
        }
        try {
            JSONObject trailersJson = new JSONObject(json);
            JSONArray allTrailers = trailersJson.optJSONArray(JSON_ALL_RESULTS);
            for (int i = 0; i < allTrailers.length(); i++) {
                JSONObject currentTrailer = allTrailers.getJSONObject(i);
                Review newReview = new Review(
                        currentTrailer.optString(JSON_ID),
                        currentTrailer.optString(JSON_REVIEW_AUTHOR),
                        currentTrailer.optString(JSON_REVIEW_CONTENT),
                        currentTrailer.optString(JSON_REVIEW_URL)
                );
                reviews.add(newReview);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return reviews;
    }


    public static List<Movie> parseMoviesJson(String json) {
        List<Movie> movies = new ArrayList<>();
        if (json == null) {
            return movies;
        }
        try {
            JSONObject moviesJson = new JSONObject(json);
            JSONArray allMovies = moviesJson.optJSONArray(JSON_ALL_RESULTS);
            for (int i = 0; i < allMovies.length(); i++) {
                JSONObject currentMovie = allMovies.getJSONObject(i);
                Movie newMovie = new Movie(
                        currentMovie.optString(JSON_ID),
                        currentMovie.optString(JSON_MOVIE_TITLE),
                        currentMovie.optString(JSON_MOVIE_OVERVIEW),
                        currentMovie.optString(JSON_POSTER_PATH),
                        currentMovie.optString(JSON_USER_RATING),
                        currentMovie.optString(JSON_RELEASE_DATE)
                );
                movies.add(newMovie);
            }
        } catch (JSONException e) {
            //movies = null;
            e.printStackTrace();
        }
        return movies;
    }

    public static List<Movie> parseMovieCursor(Cursor cursor) {
        List<Movie> movies = new ArrayList<>();
        try {
            for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
                // The Cursor is now set to the right position
                Movie currentMovieInCursor = new Movie(
                        cursor.getString(cursor.getColumnIndex(FavouriteMoviesContract.FavouriteMoviesEntry.COLUMN_TMDBID)),
                        cursor.getString(cursor.getColumnIndex(FavouriteMoviesContract.FavouriteMoviesEntry.COLUMN_TITLE)),
                        cursor.getString(cursor.getColumnIndex(FavouriteMoviesContract.FavouriteMoviesEntry.COLUMN_OVERVIEW)),
                        cursor.getString(cursor.getColumnIndex(FavouriteMoviesContract.FavouriteMoviesEntry.COLUMN_POSTER_PATH)),
                        cursor.getString(cursor.getColumnIndex(FavouriteMoviesContract.FavouriteMoviesEntry.COLUMN_USER_RATING)),
                        cursor.getString(cursor.getColumnIndex(FavouriteMoviesContract.FavouriteMoviesEntry.COLUMN_RELEASE_DATE))
                );
                movies.add(currentMovieInCursor);
            }
        } catch (Exception e) {
            movies = null;
            e.printStackTrace();
        }
        return movies;
    }
}
