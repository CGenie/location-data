package com.example.locationtracker;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;

/* 
 * Table definition.
 */

final class CachedRequesterContract {
    // To prevent someone from accidentally instantiating the contract class,
    // give it an empty constructor.
    public CachedRequesterContract() {}

    /* Inner class that defines the table contents */
    public static abstract class CachedRequesterEntry implements BaseColumns {
        public static final String TABLE_NAME = "cached_requester";
        public static final String COLUMN_NAME_ID = "cached_requester_id";
        public static final String COLUMN_NAME_TIMESTAMP = "ts";
        public static final String COLUMN_NAME_LATITUDE = "latitude";
        public static final String COLUMN_NAME_LONGITUDE = "longitude";
    }
    
    private static final String TEXT_TYPE = " TEXT";
    private static final String DATETIME_TYPE = " DATETIME";
    private static final String DOUBLE_TYPE = " DOUBLE";
    private static final String COMMA_SEP = ",";
    public static final String SQL_CREATE_ENTRIES =
        "CREATE TABLE " + CachedRequesterEntry.TABLE_NAME + " (" +
        CachedRequesterEntry._ID + " INTEGER PRIMARY KEY," +
        CachedRequesterEntry.COLUMN_NAME_ID + TEXT_TYPE + COMMA_SEP +
        CachedRequesterEntry.COLUMN_NAME_TIMESTAMP + DATETIME_TYPE + COMMA_SEP +
        CachedRequesterEntry.COLUMN_NAME_LATITUDE + DOUBLE_TYPE + COMMA_SEP +
        CachedRequesterEntry.COLUMN_NAME_LONGITUDE + DOUBLE_TYPE +
        " )";

    public static final String SQL_DELETE_ENTRIES =
        "DROP TABLE IF EXISTS " + CachedRequesterEntry.TABLE_NAME;
}

public class CachedRequesterDbHelper extends SQLiteOpenHelper {
    // If you change the database schema, you must increment the database version.
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "CachedRequester.db";
    public SQLiteDatabase db;
    
    public CachedRequesterDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CachedRequesterContract.SQL_CREATE_ENTRIES);
    }
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // This database is only a cache for online data, so its upgrade policy is
        // to simply to discard the data and start over
        //db.execSQL(CachedRequesterContract.SQL_DELETE_ENTRIES);
        //onCreate(db);
    }
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    };
}