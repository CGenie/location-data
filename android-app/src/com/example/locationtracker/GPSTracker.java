package com.example.locationtracker;

/*
 * Tracks GPS data and sends it to the CachedRequester.
 */

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

public class GPSTracker {

	private Activity parentActivity;
	private LocationManager locationManager;
	private LocationListener locationListener;
	
	public GPSTracker(Activity parentActivity) {
		this.parentActivity = parentActivity;
		Assert.assertNotNull("parentActivity cannot be null", parentActivity);
		// Acquire a reference to the system Location Manager
		this.locationManager = (LocationManager) this.parentActivity.getSystemService(Context.LOCATION_SERVICE);
		
		this.setupLocationListener();
	}
	
	private void setupLocationListener() {
		// Define a listener that responds to location updates
		this.locationListener = new LocationListener() {
			public void onLocationChanged(Location location) {
				// Called when a new location is found by the network location provider.
				//makeUseOfNewLocation(location);
				System.out.println(location.toString());
			}

			public void onStatusChanged(String provider, int status, Bundle extras) {}

			public void onProviderEnabled(String provider) {}

			public void onProviderDisabled(String provider) {}
		};

		// Register the listener with the Location Manager to receive location updates
		// NOTE: listen only the GPS_PROVIDER, not the NETWORK_PROVIDER
		this.locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, this.locationListener);
	};
}
