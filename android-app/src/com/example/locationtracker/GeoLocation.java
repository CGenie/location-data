package com.example.locationtracker;

import java.util.Date;

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
        Date date = new Date();
        return String.valueOf(date.getTime());
	}
}
