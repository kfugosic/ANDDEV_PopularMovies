package com.kfugosic.popularmovies.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.kfugosic.popularmovies.data.FavouriteMoviesContract.FavouriteMoviesEntry;

public class FavouriteMoviesDbHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "favouriteMoviesDb.db";

    private static final int VERSION = 1;

    FavouriteMoviesDbHelper(Context context) {
        super(context, DATABASE_NAME, null, VERSION);
    }


    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        final String CREATE_TABLE = "CREATE TABLE " + FavouriteMoviesEntry.TABLE_NAME + " (" +
                FavouriteMoviesEntry._ID + " INTEGER PRIMARY KEY, " +
                FavouriteMoviesEntry.COLUMN_TMDBID + " TEXT NOT NULL, " +
                FavouriteMoviesEntry.COLUMN_TITLE + " TEXT NOT NULL, " +
                FavouriteMoviesEntry.COLUMN_OVERVIEW + " TEXT NOT NULL, " +
                FavouriteMoviesEntry.COLUMN_POSTER_PATH + " TEXT NOT NULL, " +
                FavouriteMoviesEntry.COLUMN_USER_RATING + " TEXT NOT NULL, " +
                FavouriteMoviesEntry.COLUMN_RELEASE_DATE + " TEXT NOT NULL);";
        sqLiteDatabase.execSQL(CREATE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + FavouriteMoviesEntry.TABLE_NAME);
        onCreate(sqLiteDatabase);
    }
}
