package com.kfugosic.popularmovies.utils;

import android.net.Uri;
import android.util.Log;

import com.kfugosic.popularmovies.enums.SortType;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

public class NetworkUtils {

    private static final String TAG = NetworkUtils.class.getSimpleName();

    private static final String MOVIEDB_BASE_URL = "http://api.themoviedb.org/3/movie";
    private static final String QUERY_TYPE_POPULAR = "/popular";
    private static final String QUERY_TYPE_TOPRATED = "/top_rated";
    private static final String PATH_TRAILERS = "videos";
    private static final String PATH_REVIEWS = "reviews";

    private static final String MOVIEDB_POSTER_BASE_URL = "http://image.tmdb.org/t/p/";
    private static final String DEFAULT_IMAGE_SIZE = "w185";

    private static final String YOUTUBE_THUMBNAIL_BASE_URL = "https://img.youtube.com/vi";
    private static final String YOUTUBE_THUMBNAIL_SPECIFIER = "mqdefault.jpg";

    private static final String API_KEY_PARAM = "api_key";

    // TODO Put your API key here
    private static final String API_KEY = "";

    public static URL buildUrl(SortType type) {
        String baseUrl = MOVIEDB_BASE_URL;
        switch (type){
            case POPULAR:
                baseUrl += QUERY_TYPE_POPULAR;
                break;
            case HIGHEST_RATED:
                baseUrl += QUERY_TYPE_TOPRATED;
                break;
        }
        Uri builtUri = Uri.parse(baseUrl).buildUpon()
                .appendQueryParameter(API_KEY_PARAM, API_KEY)
                .build();

        URL url = null;
        try {
            url = new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        Log.v(TAG, "Built url: " + url);
        return url;
    }

    public static URL buildPosterUrl(String imageUrl) {
        Uri builtUri = Uri.parse(MOVIEDB_POSTER_BASE_URL).buildUpon()
                .appendEncodedPath(DEFAULT_IMAGE_SIZE)
                .appendEncodedPath(imageUrl)
                .build();

        URL url = null;
        try {
            url = new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        Log.v(TAG, "Built poster url: " + url);
        return url;
    }

    public static URL buildYoutubeThumbnailUrl(String videoUrl) {
        Uri builtUri = Uri.parse(YOUTUBE_THUMBNAIL_BASE_URL).buildUpon()
                .appendEncodedPath(videoUrl)
                .appendEncodedPath(YOUTUBE_THUMBNAIL_SPECIFIER)
                .build();

        URL url = null;
        try {
            url = new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        Log.v(TAG, "Built poster url: " + url);
        return url;
    }

    public static URL buildTrailersUrl(String movieId) {
        Uri builtUri = Uri.parse(MOVIEDB_BASE_URL).buildUpon()
                .appendEncodedPath(movieId)
                .appendEncodedPath(PATH_TRAILERS)
                .appendQueryParameter(API_KEY_PARAM, API_KEY)
                .build();

        URL url = null;
        try {
            url = new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        Log.v(TAG, "Built trailer url: " + url);
        return url;
    }

    public static URL buildReviewsUrl(String movieId) {
        Uri builtUri = Uri.parse(MOVIEDB_BASE_URL).buildUpon()
                .appendEncodedPath(movieId)
                .appendEncodedPath(PATH_REVIEWS)
                .appendQueryParameter(API_KEY_PARAM, API_KEY)
                .build();

        URL url = null;
        try {
            url = new URL(builtUri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        Log.v(TAG, "Built review url: " + url);
        return url;
    }

    public static String getResponseFromHttpUrl(URL url) throws IOException {
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        try {
            InputStream in = urlConnection.getInputStream();

            Scanner scanner = new Scanner(in);
            scanner.useDelimiter("\\A");

            boolean hasInput = scanner.hasNext();
            if (hasInput) {
                return scanner.next();
            } else {
                return null;
            }
        } finally {
            urlConnection.disconnect();
        }
    }

}
