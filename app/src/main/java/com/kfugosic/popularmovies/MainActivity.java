package com.kfugosic.popularmovies;

import android.content.Intent;
import android.content.res.Resources;
import android.os.PersistableBundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.kfugosic.popularmovies.models.Movie;
import com.kfugosic.popularmovies.utils.JsonUtils;
import com.kfugosic.popularmovies.utils.NetworkUtils;
import com.kfugosic.popularmovies.enums.SortType;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements ListItemClickListener, LoaderManager.LoaderCallbacks<String> {

    private static final int MOVIEDB_QUERY_LOADER_HIGHESTRATED = 1;
    private static final int MOVIEDB_QUERY_LOADER_POPULAR = 2;

    private static final String MOVIEDB_QUERY_URL_EXTRA = "query_url";
    private static final String SORT_TYPE = "sort_type";
    private static final SortType DEFAULT_SORT_TYPE = SortType.POPULAR;

    private SortType currentSortType;

    private RecyclerView moviesGrid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        moviesGrid = findViewById(R.id.movies_rv);

        GridLayoutManager layoutManager = new GridLayoutManager(this, numberOfColumns());

        moviesGrid.setLayoutManager(layoutManager);
        moviesGrid.setHasFixedSize(true);
        moviesGrid.setAdapter(new MovieAdapter(new ArrayList<Movie>(), null));

        if (savedInstanceState == null || !savedInstanceState.containsKey(SORT_TYPE)) {
            currentSortType = DEFAULT_SORT_TYPE;
        } else {
            currentSortType = (SortType) savedInstanceState.get(SORT_TYPE);
        }

        fillAdapter();

    }

    @Override
    public void onListItemClick(Movie clickedMovie) {
        Intent intent = new Intent(this, DetailActivity.class);
        intent.putExtra(DetailActivity.CLICKED_MOVIE, clickedMovie);
        startActivity(intent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int menuItemSelected = item.getItemId();
        switch (menuItemSelected) {
            case R.id.action_sort_popular:
                currentSortType = SortType.POPULAR;
                fillAdapter();
                break;
            case R.id.action_sort_highest_rated:
                currentSortType = SortType.HIGHEST_RATED;
                fillAdapter();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        outState.putSerializable(SORT_TYPE, currentSortType);
        super.onSaveInstanceState(outState, outPersistentState);
    }

    //
    // Loading movies grid
    //

    private void fillAdapter() {
        URL queryUrl = NetworkUtils.buildUrl(currentSortType);
        Bundle queryBundle = new Bundle();
        queryBundle.putString(MOVIEDB_QUERY_URL_EXTRA, queryUrl.toString());

        int loaderId = currentSortType == SortType.POPULAR ? MOVIEDB_QUERY_LOADER_POPULAR : MOVIEDB_QUERY_LOADER_HIGHESTRATED;
        LoaderManager loaderManager = getSupportLoaderManager();
        Loader<String> moviesGridLoader = loaderManager.getLoader(loaderId);

        if (moviesGridLoader == null) {
            loaderManager.initLoader(loaderId, queryBundle, this);
        } else {
            loaderManager.restartLoader(loaderId, queryBundle, this);
        }
    }

    @Override
    public Loader<String> onCreateLoader(int id, final Bundle args) {
        return new AsyncTaskLoader<String>(this) {

            String mQueryResultJson;

            @Override
            protected void onStartLoading() {
                super.onStartLoading();
                if(args==null){
                    return;
                }
                if (mQueryResultJson != null) {
                    deliverResult(mQueryResultJson);
                } else {
                    forceLoad();
                }
            }

            @Override
            public String loadInBackground() {
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
                return queryResult;
            }

            @Override
            public void deliverResult(String data) {
                mQueryResultJson = data;
                super.deliverResult(data);
            }

        };
    }

    @Override
    public void onLoadFinished(Loader<String> loader, String data) {
        if (data != null && !data.isEmpty()) {
            List<Movie> allMovies = JsonUtils.parseMovieJson(data);
            MovieAdapter adapter = new MovieAdapter(allMovies, this);
            moviesGrid.setAdapter(adapter);
            adapter.notifyDataSetChanged();
        } else {
            Toast.makeText(this, R.string.error_text, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onLoaderReset(Loader<String> loader) {
    }

    //
    // Utils
    //

    private int numberOfColumns() {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        // You can change this divider to adjust the size of the poster
        float widthDivider = pxFromDp(185);
        int width = displayMetrics.widthPixels;
        int nColumns = (int)(width / widthDivider);
        if (nColumns < 2) return 2;
        return nColumns;
    }

    public static float pxFromDp(final float dp) {
        return dp * Resources.getSystem().getDisplayMetrics().density;
    }
}
