package com.example.locationtracker;

/*
 * Tracks GPS data and sends it to the CachedRequester.
 */

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import junit.framework.Assert;
import android.app.Activity;
import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

// to fiddle with emulator's location do:
// telnet localhost 5554
// geo fix <lat> <lng>

class LocationPrintCallback implements ILocationCallback {
	public void call(Location location) {
		System.out.println(location.toString());
	}
}

class LocationStoreCallback implements ILocationCallback {
	private CachedRequester cachedRequester;

	LocationStoreCallback(CachedRequester cachedRequester) {
		this.cachedRequester = cachedRequester;
	}
	
	public void call(Location location) {
		this.cachedRequester.store(new GeoLocation(location.getLatitude(), location.getLongitude()));
	}
}

public class GPSTracker {

	private Activity parentActivity;
	private LocationManager locationManager;
	private LocationListener locationListener;
	private List<ILocationCallback> locationUpdateMethods;
	private CachedRequester cachedRequester;
	
	private LocationStoreCallback locationStoreCallback;
	
	// debugging
	private LocationPrintCallback locationPrintCallback;
	
	public GPSTracker(Activity parentActivity) {
		this.locationUpdateMethods = new ArrayList<ILocationCallback>();
		this.parentActivity = parentActivity;
		Assert.assertNotNull("parentActivity cannot be null", parentActivity);
		// Acquire a reference to the system Location Manager
		this.locationManager = (LocationManager) this.parentActivity.getSystemService(Context.LOCATION_SERVICE);
		
		this.setupLocationListener();
		
		this.cachedRequester = new CachedRequester(this.parentActivity);
		this.locationStoreCallback = new LocationStoreCallback(this.cachedRequester);
		this.onLocationUpdate(this.locationStoreCallback);
		
		// debugging
		this.locationPrintCallback = new LocationPrintCallback();
		this.onLocationUpdate(this.locationPrintCallback);
	}
	
	private void setupLocationListener() {
		// Define a listener that responds to location updates
		this.locationListener = new LocationListener() {
			public void onLocationChanged(Location location) {
				// Called when a new location is found by the network location provider.
				//makeUseOfNewLocation(location);
				
				iterateLocationUpdateFunctions(location);
			}

			public void onStatusChanged(String provider, int status, Bundle extras) {}

			public void onProviderEnabled(String provider) {}

			public void onProviderDisabled(String provider) {}
		};

		// Register the listener with the Location Manager to receive location updates
		// NOTE: listen only the GPS_PROVIDER, not the NETWORK_PROVIDER
		this.locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this.locationListener);
	};
	
	private void iterateLocationUpdateFunctions(Location location) {
		for (Iterator<ILocationCallback> iter = this.locationUpdateMethods.iterator(); iter.hasNext(); ) {
		    ILocationCallback callback = iter.next();
		    
		    callback.call(location);
		}
		
	}
	
	public void onLocationUpdate(ILocationCallback callback) {
		locationUpdateMethods.add(callback);
	}

	public void connected() {
		this.cachedRequester.connected();
		
	}
}
