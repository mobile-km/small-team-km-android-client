package com.teamkn.base.utils.location;

import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;

public class SimpleLocationListener implements LocationListener {
    private Location current_location;

    @Override
    public void onLocationChanged(Location location) {
        System.out.println("location changed");
        this.current_location = location;
    }

    @Override
    public void onProviderDisabled(String provider) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onProviderEnabled(String provider) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        // TODO Auto-generated method stub

    }

    public Location get_current_location() {
        return current_location;
    }

}
