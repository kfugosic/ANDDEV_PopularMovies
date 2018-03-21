package com.kfugosic.popularmovies;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.kfugosic.popularmovies.data.FavouriteMoviesContract;
import com.kfugosic.popularmovies.enums.SortType;
import com.kfugosic.popularmovies.lists.MainMovieListItemClickListener;
import com.kfugosic.popularmovies.lists.MovieAdapter;
import com.kfugosic.popularmovies.models.Movie;
import com.kfugosic.popularmovies.utils.MovieParsingUtils;
import com.kfugosic.popularmovies.utils.NetworkUtils;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements MainMovieListItemClickListener, LoaderManager.LoaderCallbacks<List<Movie>> {

    private static final String KEY_INSTANCE_STATE_RV_POSITION = "rv_position";

    public static final int MOVIEDB_QUERY_LOADER_HIGHESTRATED = 1;
    public static final int MOVIEDB_QUERY_LOADER_POPULAR = 2;
    public static final int MOVIEDB_QUERY_LOADER_FAVOURITES = 3;

    public static final int MOVIE_DETAILS_REQUEST = 1;
    public static final int RESULT_FAVOURITES_MODIFIED = 11;

    private static final String MOVIEDB_QUERY_URL_EXTRA = "query_url";
    private static final String SORT_TYPE = "sort_type";
    private static final int DEFAULT_SORT_TYPE = SortType.POPULAR;

    private static final int POSTER_WIDTH = 185;

    private int currentSortType;

    private boolean favouritesModified;

    private RecyclerView moviesGrid;
    private Parcelable mLayoutManagerState;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        moviesGrid = findViewById(R.id.movies_rv);

        GridLayoutManager mLayoutManager = new GridLayoutManager(this, numberOfColumns());

        moviesGrid.setLayoutManager(mLayoutManager);
        moviesGrid.setHasFixedSize(true);
        moviesGrid.setAdapter(new MovieAdapter(new ArrayList<Movie>(), null));

        currentSortType = (int) getPreferences(Context.MODE_PRIVATE).getInt(SORT_TYPE, DEFAULT_SORT_TYPE);

        if(savedInstanceState != null) {
            mLayoutManagerState = savedInstanceState.getParcelable(KEY_INSTANCE_STATE_RV_POSITION);
        }

        fillAdapter();

    }

    @SuppressLint("RestrictedApi")
    @Override
    public void onListItemClick(Movie clickedItem, ImageView imageView) {
        Intent intent = new Intent(this, DetailActivity.class);
        intent.putExtra(DetailActivity.CLICKED_MOVIE, (Movie) clickedItem);
        ActivityOptionsCompat options = ActivityOptionsCompat.
                makeSceneTransitionAnimation(this, (View) imageView, getString(R.string.poster_transition));


        if (Build.VERSION.SDK_INT >= 16) {
            startActivityForResult(intent, MOVIE_DETAILS_REQUEST, options.toBundle());
        } else {
            startActivityForResult(intent, MOVIE_DETAILS_REQUEST);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == MOVIE_DETAILS_REQUEST) {
            if (resultCode == RESULT_FAVOURITES_MODIFIED) {
                favouritesModified = true;
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int menuItemSelected = item.getItemId();
        boolean modified = false;

        switch (menuItemSelected) {
            case R.id.action_sort_popular:
                if(currentSortType != SortType.POPULAR){
                    modified = true;
                }
                currentSortType = SortType.POPULAR;
                break;
            case R.id.action_sort_highest_rated:
                if(currentSortType != SortType.HIGHEST_RATED){
                    modified = true;
                }
                currentSortType = SortType.HIGHEST_RATED;
                break;
            case R.id.action_sort_favourites:
                if(currentSortType != SortType.FAVOURITES){
                    modified = true;
                }
                currentSortType = SortType.FAVOURITES;
                break;
        }
        if(modified) {
            mLayoutManagerState = null;
            fillAdapter();

            SharedPreferences sharedPref =getPreferences(Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putInt(SORT_TYPE, currentSortType);
            editor.commit();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onPause() {
        super.onPause();
        mLayoutManagerState = moviesGrid.getLayoutManager().onSaveInstanceState();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putParcelable(KEY_INSTANCE_STATE_RV_POSITION, mLayoutManagerState);
        super.onSaveInstanceState(outState);
    }

    //
    // Loading movies grid
    //

    private void fillAdapter() {
        int loaderId = -1;
        if (currentSortType == SortType.POPULAR) {
            loaderId = MOVIEDB_QUERY_LOADER_POPULAR;
        } else if (currentSortType == SortType.HIGHEST_RATED) {
            loaderId = MOVIEDB_QUERY_LOADER_HIGHESTRATED;
        } else if (currentSortType == SortType.FAVOURITES) {
            loaderId = MOVIEDB_QUERY_LOADER_FAVOURITES;
        }

        Bundle queryBundle = null;
        if (currentSortType != SortType.FAVOURITES) {
            URL queryUrl = NetworkUtils.buildUrl(currentSortType);
            queryBundle = new Bundle();
            queryBundle.putString(MOVIEDB_QUERY_URL_EXTRA, queryUrl.toString());
        }

        LoaderManager loaderManager = getSupportLoaderManager();
        Loader<String> moviesGridLoader = loaderManager.getLoader(loaderId);

        if (moviesGridLoader == null) {
            loaderManager.initLoader(loaderId, queryBundle, this);
        } else {
            loaderManager.restartLoader(loaderId, queryBundle, this);
        }
    }

    @Override
    public Loader<List<Movie>> onCreateLoader(int id, final Bundle args) {
        return new AsyncTaskLoader<List<Movie>>(this) {

            List<Movie> mQueryResults;

            @Override
            protected void onStartLoading() {
                super.onStartLoading();
                if (mQueryResults == null) {
                    forceLoad();
                    return;
                }
                if (favouritesModified && currentSortType == SortType.FAVOURITES) {
                    favouritesModified = false;
                    forceLoad();
                    return;
                }
                deliverResult(mQueryResults);
            }

            @Override
            public List<Movie> loadInBackground() {

                if (currentSortType == SortType.FAVOURITES) {
                    Cursor cursor = getContentResolver().query(FavouriteMoviesContract.FavouriteMoviesEntry.CONTENT_URI,
                            null,
                            null,
                            null,
                            FavouriteMoviesContract.FavouriteMoviesEntry._ID);
                    return MovieParsingUtils.parseMovieCursor(cursor);
                }

                String queryUrl = args.getString(MOVIEDB_QUERY_URL_EXTRA);
                if (queryUrl == null || queryUrl.isEmpty()) {
                    return null;
                }
                String queryResult = null;
                try {
                    queryResult = NetworkUtils.getResponseFromHttpUrl(new URL(queryUrl));
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return MovieParsingUtils.parseMoviesJson(queryResult);
            }

            @Override
            public void deliverResult(List<Movie> data) {
                mQueryResults = data;
                super.deliverResult(data);
            }

        };
    }

    @Override
    public void onLoadFinished(Loader<List<Movie>> loader, List<Movie> movies) {
        if (movies != null && !movies.isEmpty()) {
            MovieAdapter adapter = new MovieAdapter(movies, this);
            moviesGrid.setAdapter(adapter);
            adapter.notifyDataSetChanged();
            if(mLayoutManagerState != null) {
                moviesGrid.getLayoutManager().onRestoreInstanceState(mLayoutManagerState);
            }
        } else {
            Toast.makeText(this, R.string.error_text, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onLoaderReset(Loader<List<Movie>> loader) {
    }

    //
    // Utils
    //

    private int numberOfColumns() {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        // You can change this divider to adjust the size of the poster
        float widthDivider = pxFromDp(POSTER_WIDTH);
        int width = displayMetrics.widthPixels;
        int nColumns = (int) (width / widthDivider);
        if (nColumns < 2) return 2;
        return nColumns;
    }

    public static float pxFromDp(final float dp) {
        return dp * Resources.getSystem().getDisplayMetrics().density;
    }
}
