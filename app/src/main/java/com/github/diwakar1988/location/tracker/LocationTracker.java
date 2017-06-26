package com.github.diwakar1988.location.tracker;

import android.app.Activity;

import com.github.diwakar1988.location.core.BaseLocationProvider;
import com.github.diwakar1988.location.provider.OnLocationListener;


public class LocationTracker extends BaseLocationProvider {
    public LocationTracker(Activity activity, OnLocationListener listener) {
        super(activity, listener);
    }

    public void track(){

    }
    @Override
    public void stop() {
        onStop();
    }

    @Override
    public void start() {
        onStart();
    }
}
