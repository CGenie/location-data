package com.example.locationtracker;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.locationtracker.CachedRequesterContract.CachedRequesterEntry;

/*
 * Caches requests to a specific URL: when network is OFF.
 * When it is ON, cached requests are sent.
 * Cache is done in private SQLite file.
 */

class GeoLocationWithId extends GeoLocation {
	public GeoLocationWithId(Integer id, String timestamp, double latitude,
			double longitude) {
		super(timestamp, latitude, longitude);
		this.id = id;
	}

	public Integer id;
	
	public GeoLocationWithId(String timestamp, double latitude, double longitude) {
		super(timestamp, latitude, longitude);
	}

	public GeoLocationWithId(double latitude, double longitude) {
		super(latitude, longitude);
	}

	@Override
	public String toString() {
		return "{" + String.valueOf(this.id) + "} " + super.toString();
	}
}

public class CachedRequester {
	private Activity parentActivity;
	private CachedRequesterDbHelper dbHelper;
	
	public CachedRequester(Activity parentActivity) {
		this.parentActivity = parentActivity;
		this.dbHelper = new CachedRequesterDbHelper(parentActivity.getApplicationContext());
		
		System.out.println(this.dbRead().toString());
	}
	
	public void store(GeoLocation location) {
		this.dbInsert(location);
	}
	
	private void dbInsert(GeoLocation location) {
		SQLiteDatabase db = this.dbHelper.getWritableDatabase();
		
		ContentValues values = new ContentValues();
		//values.put(CachedRequesterEntry.COLUMN_NAME_ENTRY_ID, null);
		values.put(CachedRequesterEntry.COLUMN_NAME_TIMESTAMP, location.timestamp);
		values.put(CachedRequesterEntry.COLUMN_NAME_LATITUDE, location.latitude);
		values.put(CachedRequesterEntry.COLUMN_NAME_LONGITUDE, location.longitude);

		// Insert the new row, returning the primary key value of the new row
		db.insert(
			  	 CachedRequesterEntry.TABLE_NAME,
			  	 null,
		         values);
	}
	
	private List<GeoLocationWithId> dbRead() {
		SQLiteDatabase db = this.dbHelper.getReadableDatabase();
		List<GeoLocationWithId> ret = new ArrayList<GeoLocationWithId>();

		// Define a projection that specifies which columns from the database
		// you will actually use after this query.
		String[] projection = {
		    CachedRequesterEntry._ID,
		    CachedRequesterEntry.COLUMN_NAME_TIMESTAMP,
		    CachedRequesterEntry.COLUMN_NAME_LATITUDE,
		    CachedRequesterEntry.COLUMN_NAME_LONGITUDE,
		    };

		Cursor c = db.query(
			CachedRequesterEntry.TABLE_NAME,  // The table to query
		    projection,                               // The columns to return
		    null,                                // The columns for the WHERE clause
		    null,                            // The values for the WHERE clause
		    null,                                     // don't group the rows
		    null,                                     // don't filter by row groups
		    null                                 // The sort order
		    );
		
		while(c.moveToNext()) {
			int idColId, timestampColId, latitudeColId, longitudeColId;
			
			idColId = c.getColumnIndex(CachedRequesterEntry._ID);
			timestampColId = c.getColumnIndex(CachedRequesterEntry.COLUMN_NAME_TIMESTAMP);
			latitudeColId = c.getColumnIndex(CachedRequesterEntry.COLUMN_NAME_LATITUDE);
			longitudeColId = c.getColumnIndex(CachedRequesterEntry.COLUMN_NAME_LONGITUDE);
			
			ret.add(new GeoLocationWithId(
						c.getInt(idColId),
						c.getString(timestampColId),
						c.getDouble(latitudeColId),
						c.getDouble(longitudeColId)));
		}
		
		return ret;
	}
	

}
