package com.kfugosic.popularmovies;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.kfugosic.popularmovies.models.Movie;
import com.kfugosic.popularmovies.utils.NetworkUtils;
import com.squareup.picasso.Picasso;

public class DetailActivity extends AppCompatActivity {

    public static final String CLICKED_MOVIE = "clicked_movie";

    private TextView mTitleDisplay;
    private TextView mYearDisplay;
    private TextView mUserScoreDisplay;
    private TextView mOverviewDisplay;
    private ImageView mPosterDisplay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        mTitleDisplay = findViewById(R.id.title_tv);
        mYearDisplay = findViewById(R.id.year_tv);
        mUserScoreDisplay = findViewById(R.id.user_score_tv);
        mOverviewDisplay = findViewById(R.id.overview_tv);
        mPosterDisplay = findViewById(R.id.poster_iv);

        Intent intent = getIntent();
        if (intent == null) {
            closeOnError();
            return;
        }

        Movie movie = intent.getParcelableExtra(CLICKED_MOVIE);
        if (movie == null) {
            closeOnError();
            return;
        }

        try {
            populateUI(movie);
        } catch (Exception e) {
            closeOnError();
            e.printStackTrace();
        }

    }

    private void populateUI(Movie movie) {
        mTitleDisplay.setText(movie.getTitle());
        mYearDisplay.setText(movie.getReleaseDate().substring(0, 4));
        mUserScoreDisplay.setText(String.format(getString(R.string.user_score_showing_format), movie.getUserRating()));
        mOverviewDisplay.setText(movie.getOverview());

        String posterPath = movie.getPosterPath();
        Picasso.with(this)
                .load(NetworkUtils.buildPosterUrl(posterPath).toString())
                .placeholder(R.drawable.loading)
                .error(R.drawable.ic_do_not_disturb_alt_black_24dp)
                .into(mPosterDisplay);
    }

    private void closeOnError() {
        finish();
        Toast.makeText(this, R.string.error_text, Toast.LENGTH_SHORT).show();
    }
}
