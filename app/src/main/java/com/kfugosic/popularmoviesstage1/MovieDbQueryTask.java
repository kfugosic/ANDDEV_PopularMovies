package com.kfugosic.popularmoviesstage1;

import android.content.Context;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.support.v7.widget.RecyclerView;
import android.widget.Toast;

import com.kfugosic.popularmoviesstage1.models.Movie;
import com.kfugosic.popularmoviesstage1.utils.NetworkUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class MovieDbQueryTask extends AsyncTask<URL, Void, String> {

    private static final String JSON_ALL_RESULTS = "results";
    private static final String JSON_MOVIE_ID = "id";
    private static final String JSON_MOVIE_TITLE = "original_title";
    private static final String JSON_MOVIE_OVERVIEW = "overview";
    private static final String JSON_POSTER_PATH = "poster_path";
    private static final String JSON_USER_RATING = "vote_average";
    private static final String JSON_RELEASE_DATE = "release_date";

    private RecyclerView mMoviesGrid;
    private ListItemClickListener mParentActivity;
    private Context mContext;

    public MovieDbQueryTask(RecyclerView moviesGrid, ListItemClickListener parrent, Context context) {
        this.mMoviesGrid = moviesGrid;
        mParentActivity = parrent;
        mContext = context;
    }

    @Override
    protected String doInBackground(URL... urls) {
        URL searchUrl = urls[0];
        String queryResults = null;
        try {
            queryResults = NetworkUtils.getResponseFromHttpUrl(searchUrl);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return queryResults;
    }

    @Override
    protected void onPostExecute(String s) {
        if (s != null && !s.isEmpty()) {
            List<Movie> allMovies = parseMovieJson(s);
            MovieAdapter adapter = new MovieAdapter(allMovies, mParentActivity);
            mMoviesGrid.setAdapter(adapter);
            adapter.notifyDataSetChanged();
        } else {
            Toast.makeText(mContext, R.string.error_text, Toast.LENGTH_SHORT).show();
        }
    }

    private static List<Movie> parseMovieJson(String json) {
        List<Movie> movies = new ArrayList<>();
        try {
            JSONObject moviesJson = new JSONObject(json);
            JSONArray allMovies = moviesJson.getJSONArray(JSON_ALL_RESULTS);
            for (int i = 0; i < allMovies.length(); i++) {
                JSONObject currentMovie = allMovies.getJSONObject(i);
                Movie newMovie = new Movie(
                        currentMovie.optString(JSON_MOVIE_ID),
                        currentMovie.optString(JSON_MOVIE_TITLE),
                        currentMovie.optString(JSON_MOVIE_OVERVIEW),
                        currentMovie.optString(JSON_POSTER_PATH),
                        currentMovie.optString(JSON_USER_RATING),
                        currentMovie.optString(JSON_RELEASE_DATE)
                );
                movies.add(newMovie);
            }
        } catch (JSONException e) {
            movies = null;
            e.printStackTrace();
        }
        return movies;
    }

}
