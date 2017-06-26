package com.github.diwakar1988.location.provider;


import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.location.Location;
import android.support.v4.app.ActivityCompat;

import com.github.diwakar1988.location.core.BaseLocationProvider;
import com.github.diwakar1988.location.core.LocationException;
import com.google.android.gms.location.LocationServices;

public class LocationProvider extends BaseLocationProvider {

    public LocationProvider(Activity activity, OnLocationListener listener) {
        super(activity,listener);
    }

    public void provideLocation() {

        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        switch (getState()){
            case STATE_NOT_CONNECTED:
                getListener().onLocationError(new LocationException("Google play services not connected."));
                return;
            case STATE_FAILED:
                getListener().onLocationError(new LocationException("Google play services connection failed."));
                return;

        }
        Location mLastLocation = LocationServices.FusedLocationApi
                .getLastLocation(getGoogleApiClient());

        if (mLastLocation != null) {
            getListener().onLocationAvailable(mLastLocation);
        } else {

            getListener().onLocationError(new LocationException());
        }
    }
    @Override
    public void stop(){
        onStop();
    }

    @Override
    public void start() {
        onStart();
    }
}
