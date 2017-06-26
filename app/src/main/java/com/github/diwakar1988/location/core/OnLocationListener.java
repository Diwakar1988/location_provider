package com.github.diwakar1988.location.core;


import android.location.Location;

public interface OnLocationListener {
    void onLocationAvailable(Location location);
    void onLocationError(Throwable t);
}
