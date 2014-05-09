package com.example.locationtracker;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class GeoLocation {
	public String timestamp;
	public double latitude;
	public double longitude;
	
	GeoLocation(String timestamp, double latitude, double longitude) {
		this.timestamp = timestamp;
		this.latitude = latitude;
		this.longitude = longitude;
	}
	
	GeoLocation(double latitude, double longitude) {
		this.timestamp = this.getNow();
		this.latitude = latitude;
		this.longitude = longitude;
	}
	
	@Override
	public String toString() {
		return String.valueOf(this.latitude) + ", " + String.valueOf(this.longitude) + " [" + this.timestamp + "]";
	}
	
	private String getNow() {
		SimpleDateFormat dateFormat = new SimpleDateFormat(
                "yyyy-MM-dd HH:mm:ss", Locale.getDefault());
        Date date = new Date();
        return dateFormat.format(date);
	}
}
