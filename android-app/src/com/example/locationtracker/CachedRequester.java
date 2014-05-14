package com.example.locationtracker;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.provider.Settings.Secure;

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


// argument to RequestTask -- contains GeoLocation data with DB link
// (DB link is necessary to remove POSTed stuff from the DB)
class DBGeoLocation {
	public GeoLocationWithId geoLocation;
	public CachedRequesterDbHelper dbHelper;
	public String androidId;

	public DBGeoLocation(GeoLocationWithId geoLocation,
			CachedRequesterDbHelper dbHelper,
			String androidId) {
		this.geoLocation = geoLocation;
		this.dbHelper = dbHelper;
		this.androidId = androidId;
	}
}


class RequestTask extends AsyncTask<DBGeoLocation, String, List<DBGeoLocation>>{
	
    @Override
    protected List<DBGeoLocation> doInBackground(DBGeoLocation... geoLocations) {
    	List<DBGeoLocation> result = new ArrayList<DBGeoLocation>();
    	int count = geoLocations.length;
        HttpClient httpclient = new DefaultHttpClient();
        HttpResponse response;
        
        //private String android_id = Secure.getString(getContext().getContentResolver(),
        //        Secure.ANDROID_ID);
        
        for(int i = 0; i < count; i++) {
        	DBGeoLocation geoLocation = geoLocations[i];
	        HttpPost httppost = new HttpPost("http://10.0.2.2:8080/location/");
	        
	        try {
	            List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(4);
	            
	            nameValuePairs.add(new BasicNameValuePair("device_id", geoLocation.androidId));
	            nameValuePairs.add(new BasicNameValuePair("timestamp", geoLocation.geoLocation.timestamp));
	            nameValuePairs.add(new BasicNameValuePair("latitude", String.valueOf(geoLocation.geoLocation.latitude)));
	            nameValuePairs.add(new BasicNameValuePair("longitude", String.valueOf(geoLocation.geoLocation.longitude)));
	            httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
	            
	            response = httpclient.execute(httppost);
	            
	            if((response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) && (
	            		geoLocation.geoLocation.id != null)) {
	            	result.add(geoLocation);
	            }
	            
	            response.getEntity().consumeContent();
	        } catch (ClientProtocolException e) {
	            //TODO Handle problems..
	        } catch (IOException e) {
	            //TODO Handle problems..
			}
        }
        return result;
    }


    @Override
    protected void onPostExecute(List<DBGeoLocation> result) {
        super.onPostExecute(result);
        
        Iterator<DBGeoLocation> it = result.iterator();
        DBGeoLocation iid;
        SQLiteDatabase db = null;
        
        while(it.hasNext()) {
        	iid = it.next();
        	if(db == null) {
        		db = iid.dbHelper.getWritableDatabase();
        	}
        	db.delete(CachedRequesterEntry.TABLE_NAME,
        			  CachedRequesterEntry._ID + "=?",
        			  new String[] {String.valueOf(iid.geoLocation.id)});
        }
    }
}


public class CachedRequester {
	private CachedRequesterDbHelper dbHelper;
	private String androidId;
	private Activity parentActivity;
	
	public CachedRequester(Activity parentActivity) {
		Context ctx = parentActivity.getApplicationContext();
		this.parentActivity = parentActivity;
		this.androidId = Secure.getString(ctx.getContentResolver(),
                						   Secure.ANDROID_ID); 
		
		this.dbHelper = new CachedRequesterDbHelper(ctx);
		
		System.out.println(this.dbRead().toString());
	}
	
	public void store(GeoLocation location) {
		this.dbInsert(location);
		
		ConnectivityManager connectivityManager 
            = (ConnectivityManager) this.parentActivity.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
		
		if(activeNetworkInfo != null && activeNetworkInfo.isConnected()) {
			this.connected();
		};
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

	public void connected() {
		List<GeoLocationWithId> list = this.dbRead();
		GeoLocationWithId[] geoLocations = list.toArray(new GeoLocationWithId[list.size()]);
		DBGeoLocation[] dbGeoLocations = new DBGeoLocation[list.size()];
		
		for(int i = 0; i < list.size(); i++) {
			dbGeoLocations[i] = new DBGeoLocation(geoLocations[i], this.dbHelper, this.androidId);
		}
		
        new RequestTask().execute(dbGeoLocations);
	}
	

}
