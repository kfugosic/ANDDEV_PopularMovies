package com.kfugosic.popularmovies.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

public class FavouriteMoviesContentProvider extends ContentProvider {

    private static final int FAVOURITE_MOVIES = 100;
    private static final int SPECIFIC_MOVIE = 101;
    private static final UriMatcher sUriMatcher = buildUriMatcher();

    private FavouriteMoviesDbHelper mFavouriteMoviesDbHelper;

    @Override
    public boolean onCreate() {
        Context context = getContext();
        mFavouriteMoviesDbHelper = new FavouriteMoviesDbHelper(context);
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        final SQLiteDatabase db = mFavouriteMoviesDbHelper.getReadableDatabase();
        int match = sUriMatcher.match(uri);
        Cursor resultCursor = null;
        switch (match) {
            case FAVOURITE_MOVIES:
                resultCursor = db.query(
                        FavouriteMoviesContract.FavouriteMoviesEntry.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder
                );
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        resultCursor.setNotificationUri(getContext().getContentResolver(), uri);

        return resultCursor;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues contentValues) {
        final SQLiteDatabase db = mFavouriteMoviesDbHelper.getWritableDatabase();
        int match = sUriMatcher.match(uri);
        Uri resultUri = null;
        switch (match) {
            case FAVOURITE_MOVIES:
                long id = db.insert(FavouriteMoviesContract.FavouriteMoviesEntry.TABLE_NAME, null, contentValues);
                if (id > 0) {
                    resultUri = ContentUris.withAppendedId(FavouriteMoviesContract.FavouriteMoviesEntry.CONTENT_URI, id);
                } else {
                    throw new android.database.SQLException("Failed to insert row into " + uri);
                }
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        getContext().getContentResolver().notifyChange(uri, null);
        return resultUri;
    }

    @Override
    public int delete(@NonNull Uri uri, String selection, String[] selectionArgs) {
        final SQLiteDatabase db = mFavouriteMoviesDbHelper.getWritableDatabase();
        int match = sUriMatcher.match(uri);
        int tasksDeleted = 0;
        switch (match) {
            case SPECIFIC_MOVIE:
                String id = uri.getPathSegments().get(1);
                tasksDeleted = db.delete(
                        FavouriteMoviesContract.FavouriteMoviesEntry.TABLE_NAME,
                        "_id=?",
                        new String[]{id}
                );
                break;
            case FAVOURITE_MOVIES:
                if (selection != null && selectionArgs != null) {
                    tasksDeleted = db.delete(
                            FavouriteMoviesContract.FavouriteMoviesEntry.TABLE_NAME,
                            selection,
                            selectionArgs
                    );
                }
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        if (tasksDeleted > 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return tasksDeleted;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues contentValues, @Nullable String s, @Nullable String[] strings) {
        final SQLiteDatabase db = mFavouriteMoviesDbHelper.getWritableDatabase();
        int match = sUriMatcher.match(uri);
        int tasksUpdated = 0;
        switch (match) {
            case SPECIFIC_MOVIE:
                String id = uri.getPathSegments().get(1);
                tasksUpdated = db.update(
                        FavouriteMoviesContract.FavouriteMoviesEntry.TABLE_NAME,
                        contentValues,
                        "_id=?",
                        new String[]{id}
                );
                break;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
        if (tasksUpdated > 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return tasksUpdated;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        int match = sUriMatcher.match(uri);
        switch (match) {
            case FAVOURITE_MOVIES:
                return "vnd.android.cursor.dir" + "/" + FavouriteMoviesContract.AUTHORITY + "/" + FavouriteMoviesContract.PATH_FAVOURITES;
            case SPECIFIC_MOVIE:
                return "vnd.android.cursor.item" + "/" + FavouriteMoviesContract.AUTHORITY + "/" + FavouriteMoviesContract.PATH_FAVOURITES;
            default:
                throw new UnsupportedOperationException("Unknown uri: " + uri);
        }
    }

    private static UriMatcher buildUriMatcher() {
        UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        uriMatcher.addURI(FavouriteMoviesContract.AUTHORITY, FavouriteMoviesContract.PATH_FAVOURITES, FAVOURITE_MOVIES);
        uriMatcher.addURI(FavouriteMoviesContract.AUTHORITY, FavouriteMoviesContract.PATH_FAVOURITES + "/#", SPECIFIC_MOVIE);
        return uriMatcher;
    }

}

