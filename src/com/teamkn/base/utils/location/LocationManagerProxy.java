package com.teamkn.base.utils.location;

import com.teamkn.application.MindpinApplication;

import android.content.Context;
import android.location.Location;
import android.location.LocationManager;

public class LocationManagerProxy {
	private boolean location_enabled;
	private LocationManager location_manager;
	private SimpleLocationListener listener;
	
	public LocationManagerProxy(){
		this.location_manager = (LocationManager) MindpinApplication.context.getSystemService(Context.LOCATION_SERVICE);
		this.listener = new SimpleLocationListener();
	}

	public void enable_my_location() {
		System.out.println("enable_my_location");
		if (!location_enabled) {
			for (final String provider : location_manager.getAllProviders()) {
				location_manager.requestLocationUpdates(provider, 20000, 1, listener);
			}
		}
		location_enabled = true;
	}

	public void disable_my_location() {
		System.out.println("disable_my_location");
		location_manager.removeUpdates(listener);
		location_enabled = false;
	}
	
	public Location get_my_location(){
		return listener.get_current_location();
	}
}
