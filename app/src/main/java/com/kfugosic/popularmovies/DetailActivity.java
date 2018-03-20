package com.kfugosic.popularmovies;

import android.content.ActivityNotFoundException;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.kfugosic.popularmovies.data.FavouriteMoviesContract;
import com.kfugosic.popularmovies.lists.ListItemClickListener;
import com.kfugosic.popularmovies.lists.MovieReviewsAdapter;
import com.kfugosic.popularmovies.lists.MovieTrailersAdapter;
import com.kfugosic.popularmovies.lists.TrailersReviewsTuple;
import com.kfugosic.popularmovies.models.Movie;
import com.kfugosic.popularmovies.models.Review;
import com.kfugosic.popularmovies.models.Trailer;
import com.kfugosic.popularmovies.utils.MovieParsingUtils;
import com.kfugosic.popularmovies.utils.NetworkUtils;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class DetailActivity extends AppCompatActivity implements ListItemClickListener, LoaderManager.LoaderCallbacks<TrailersReviewsTuple> {

    public static final String CLICKED_MOVIE = "clicked_movie";

    private Movie selectedMovie;

    private TextView mTitleDisplay;
    private TextView mYearDisplay;
    private TextView mUserScoreDisplay;
    private TextView mOverviewDisplay;
    private ImageView mPosterDisplay;
    private ToggleButton mFavouriteButton;
    private RecyclerView mTrailersList;
    private RecyclerView mReviewsList;
    private TextView mTrailersHeadline;
    private TextView mReviewsHeadline;

    private static final String MOVIE_TRAILERS_URL_EXTRA = "movie_trailers_url";
    private static final String MOVIE_REVIEWS_URL_EXTRA = "movie_reviews_url";

    private static final int MOVIE_RETRIEVE_TRAILERS_AND_REVIEWS_LOADER_ID = 201;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        mTitleDisplay = findViewById(R.id.title_tv);
        mYearDisplay = findViewById(R.id.year_tv);
        mUserScoreDisplay = findViewById(R.id.user_score_tv);
        mOverviewDisplay = findViewById(R.id.overview_tv);
        mPosterDisplay = findViewById(R.id.poster_iv);

        mTrailersList = findViewById(R.id.trailers_rv);
        mReviewsList = findViewById(R.id.reviews_rv);
        mTrailersHeadline = findViewById(R.id.trailers_headline_tv);
        mReviewsHeadline = findViewById(R.id.reviews_headline_tv);

        Intent intent = getIntent();
        if (intent == null) {
            closeOnError();
            return;
        }

        selectedMovie = intent.getParcelableExtra(CLICKED_MOVIE);
        if (selectedMovie == null) {
            closeOnError();
            return;
        }

        try {
            populateUI();
        } catch (Exception e) {
            closeOnError();
            e.printStackTrace();
        }

    }

    private void populateUI() {
        mTitleDisplay.setText(selectedMovie.getTitle());
        mYearDisplay.setText(selectedMovie.getReleaseDate().substring(0, 4));
        mUserScoreDisplay.setText(String.format(getString(R.string.user_score_showing_format), selectedMovie.getUserRating()));
        mOverviewDisplay.setText(selectedMovie.getOverview());

        String posterPath = selectedMovie.getPosterPath();
        Picasso.with(this)
                .load(NetworkUtils.buildPosterUrl(posterPath).toString())
                .placeholder(R.drawable.loading)
                .error(R.drawable.ic_do_not_disturb_alt_black_24dp)
                .into(mPosterDisplay);

        mFavouriteButton = findViewById(R.id.favourite_tb);
        setInitialyChecked();
        mFavouriteButton.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    insertIntoFavourites();
                    mFavouriteButton.setBackgroundDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_favorite_24dp));
                } else {
                    removeFromFavourites();
                    mFavouriteButton.setBackgroundDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_favorite_empty_24dp));
                }
            }
        });

        initializeTrailersList();
        initializeReviewsList();
        populateListAdapters();
    }


    private void populateListAdapters() {
        URL queryUrlTrailers = NetworkUtils.buildTrailersUrl(selectedMovie.getId());
        URL queryUrlReviews = NetworkUtils.buildReviewsUrl(selectedMovie.getId());
        Bundle queryBundle = new Bundle();
        queryBundle.putString(MOVIE_TRAILERS_URL_EXTRA, queryUrlTrailers.toString());
        queryBundle.putString(MOVIE_REVIEWS_URL_EXTRA, queryUrlReviews.toString());

        LoaderManager loaderManager = getSupportLoaderManager();
        Loader<String> movieTrailersLoader = loaderManager.getLoader(MOVIE_RETRIEVE_TRAILERS_AND_REVIEWS_LOADER_ID);

        if (movieTrailersLoader == null) {
            loaderManager.initLoader(MOVIE_RETRIEVE_TRAILERS_AND_REVIEWS_LOADER_ID, queryBundle, this);
        } else {
            loaderManager.restartLoader(MOVIE_RETRIEVE_TRAILERS_AND_REVIEWS_LOADER_ID, queryBundle, this);
        }
    }

    private void initializeTrailersList() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        mTrailersList.setLayoutManager(layoutManager);
        mTrailersList.setHasFixedSize(true);
        mTrailersList.setAdapter(new MovieTrailersAdapter(new ArrayList<Trailer>(), null));
    }

    private void initializeReviewsList() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        mReviewsList.setLayoutManager(layoutManager);
        mReviewsList.setHasFixedSize(true);
        mReviewsList.setAdapter(new MovieReviewsAdapter(new ArrayList<Review>(), null));
    }

    private void setInitialyChecked() {
        Cursor queryResult = getContentResolver().query(
                FavouriteMoviesContract.FavouriteMoviesEntry.CONTENT_URI,
                null,
                "tmdbid=?",
                new String[]{selectedMovie.getId()},
                null
        );
        if (queryResult != null && queryResult.getCount() > 0) {
            mFavouriteButton.setChecked(true);
            mFavouriteButton.setBackgroundDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_favorite_24dp));
        } else {
            mFavouriteButton.setChecked(false);
            mFavouriteButton.setBackgroundDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.ic_favorite_empty_24dp));
        }
        queryResult.close();
    }

    private Uri insertIntoFavourites() {
        ContentValues contentValues = new ContentValues();
        contentValues.put(FavouriteMoviesContract.FavouriteMoviesEntry.COLUMN_TMDBID, selectedMovie.getId());
        contentValues.put(FavouriteMoviesContract.FavouriteMoviesEntry.COLUMN_TITLE, selectedMovie.getTitle());
        contentValues.put(FavouriteMoviesContract.FavouriteMoviesEntry.COLUMN_OVERVIEW, selectedMovie.getOverview());
        contentValues.put(FavouriteMoviesContract.FavouriteMoviesEntry.COLUMN_POSTER_PATH, selectedMovie.getPosterPath());
        contentValues.put(FavouriteMoviesContract.FavouriteMoviesEntry.COLUMN_USER_RATING, selectedMovie.getUserRating());
        contentValues.put(FavouriteMoviesContract.FavouriteMoviesEntry.COLUMN_RELEASE_DATE, selectedMovie.getReleaseDate());
        Uri uri = getContentResolver().insert(FavouriteMoviesContract.FavouriteMoviesEntry.CONTENT_URI, contentValues);
        setResult(MainActivity.RESULT_FAVOURITES_MODIFIED);
        return uri;
    }

    private int removeFromFavourites() {
        int deletedCount = getContentResolver().delete(
                FavouriteMoviesContract.FavouriteMoviesEntry.CONTENT_URI,
                "tmdbid=?",
                new String[]{selectedMovie.getId()}
        );
        if (deletedCount > 0) {
            setResult(MainActivity.RESULT_FAVOURITES_MODIFIED);
        }
        return deletedCount;
    }

    private void closeOnError() {
        finish();
        Toast.makeText(this, R.string.error_text, Toast.LENGTH_SHORT).show();
    }


    @Override
    public Loader<TrailersReviewsTuple> onCreateLoader(int id, final Bundle args) {
        return new AsyncTaskLoader<TrailersReviewsTuple>(this) {

            @Override
            protected void onStartLoading() {
                forceLoad();
                super.onStartLoading();
            }

            @Override
            public TrailersReviewsTuple loadInBackground() {

                if (args == null || args.isEmpty()) {
                    return null;
                }

                String queryUrlTrailers = args.getString(MOVIE_TRAILERS_URL_EXTRA);
                String queryUrlReviews = args.getString(MOVIE_REVIEWS_URL_EXTRA);

                String queryResultTrailers = null;
                String queryResultReviews = null;
                try {
                    queryResultTrailers = NetworkUtils.getResponseFromHttpUrl(new URL(queryUrlTrailers));
                } catch (IOException e) {
                    e.printStackTrace();
                }
                try {
                    queryResultReviews = NetworkUtils.getResponseFromHttpUrl(new URL(queryUrlReviews));
                } catch (IOException e) {
                    e.printStackTrace();
                }

                List<Trailer> trailers = MovieParsingUtils.parseTrailersJson(queryResultTrailers);
                List<Review> reviews = MovieParsingUtils.parseReviewsJson(queryResultReviews);

                return new TrailersReviewsTuple(trailers, reviews);
            }

        };
    }

    @Override
    public void onLoadFinished(Loader<TrailersReviewsTuple> loader, TrailersReviewsTuple data) {
        if (data == null) {
            mReviewsHeadline.setText(R.string.reviews_not_available);
            mReviewsHeadline.setText(R.string.trailers_not_available);
            return;
        }
        if (data.getTrailerList() != null && !data.getTrailerList().isEmpty()) {
            mTrailersHeadline.setText(R.string.trailers);
            MovieTrailersAdapter adapterTrailers = new MovieTrailersAdapter(data.getTrailerList(), this);
            mTrailersList.setAdapter(adapterTrailers);
            adapterTrailers.notifyDataSetChanged();
        } else {
            mTrailersHeadline.setText(R.string.trailers_not_available);
        }
        if (data.getReviewList() != null && !data.getReviewList().isEmpty()) {
            mReviewsHeadline.setText(R.string.reviews);
            MovieReviewsAdapter adapterReviews = new MovieReviewsAdapter(data.getReviewList(), this);
            mReviewsList.setAdapter(adapterReviews);
            adapterReviews.notifyDataSetChanged();
        } else {
            mReviewsHeadline.setText(R.string.reviews_not_available);
        }

    }

    @Override
    public void onLoaderReset(Loader<TrailersReviewsTuple> loader) {
    }

    @Override
    public void onListItemClick(Object clickedItem) {
        if (clickedItem instanceof Trailer) {
            Trailer chosenTrailer = (Trailer) clickedItem;
            Intent appIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube:" + chosenTrailer.getKey()));
            Intent webIntent = new Intent(Intent.ACTION_VIEW,
                    Uri.parse("http://www.youtube.com/watch?v=" + chosenTrailer.getKey()));

            if(appIntent.resolveActivity(getPackageManager()) != null) {
                startActivity(appIntent);
            } else if(webIntent.resolveActivity(getPackageManager()) != null) {
                startActivity(webIntent);
            }
            Toast.makeText(this, ((Trailer) clickedItem).getName(), Toast.LENGTH_SHORT).show();
        } else if (clickedItem instanceof Review) {
            Review chosenReview = (Review) clickedItem;
            Intent webIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(chosenReview.getUrl()));
            startActivity(webIntent);
        }
    }
}
