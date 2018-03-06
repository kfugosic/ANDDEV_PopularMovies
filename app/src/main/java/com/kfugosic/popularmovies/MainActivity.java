package com.kfugosic.popularmovies;

import android.content.Intent;
import android.content.res.Resources;
import android.os.PersistableBundle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuItem;

import com.kfugosic.popularmovies.models.Movie;
import com.kfugosic.popularmovies.utils.NetworkUtils;
import com.kfugosic.popularmovies.enums.SortType;

import java.net.URL;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity implements ListItemClickListener {

    private static final int NUMBER_OF_COLUMNS_PORTRAIT = 2;
    private static final int NUMBER_OF_COLUMNS_LANDSCAPE = 3;
    private static final int LANDSCAPE_ORIENTATION_ID = 2;
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

    private void fillAdapter() {
        URL queryUrl = NetworkUtils.buildUrl(currentSortType);
        new MovieDbQueryTask(moviesGrid, this, this).execute(queryUrl);
    }

    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        outState.putSerializable(SORT_TYPE, currentSortType);
        super.onSaveInstanceState(outState, outPersistentState);
    }

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
