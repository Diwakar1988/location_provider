package com.github.diwakar1988.location.core;


import android.app.Activity;
import android.content.Context;
import android.content.IntentSender;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;

public abstract class BaseLocationProvider implements GoogleServicesConnectionListener {

    public static final int RC_PLAY_SERVICES_RESOLUTION = 0X1F;
    public static final int RC_LOCATION_SETTINGS = 0X2F;
    private static final long LOCATION_UPDATE_INTERVAL = 5000; //5 seconds
    private static final long LOCATION_UPDATE_FASTEST_INTERVAL = 2000; //2 seconds
    private Activity activity;
    private OnLocationListener listener;
    private int connectionState;

    // Google client to interact with Google API
    private GoogleApiClient googleApiClient;

    public BaseLocationProvider(Activity activity, OnLocationListener listener) {
        this.activity = activity;
        this.listener = listener;
        if (checkPlayServices()) {
            buildGoogleApiClient();
        }
    }

    protected void onStart() {
        if (googleApiClient != null) {
            googleApiClient.connect();
        }
    }

    protected void onStop() {
        this.activity = null;
        this.listener = null;
        if (googleApiClient != null) {
            googleApiClient.disconnect();
        }
        this.googleApiClient = null;
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        connectionState = STATE_CONNECTED;
    }

    @Override
    public void onConnectionSuspended(int i) {
        connectionState = STATE_SUSPENDED;
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        connectionState = STATE_FAILED;
    }

    public int getState() {
        return connectionState;
    }

    /**
     * Creating google api client object
     */
    protected synchronized void buildGoogleApiClient() {
        googleApiClient = new GoogleApiClient.Builder(activity)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API).build();
    }

    /**
     * Method to verify google play services on the device
     */
    private boolean checkPlayServices() {
        GoogleApiAvailability googleApiAvailability = GoogleApiAvailability.getInstance();
        int status = googleApiAvailability.isGooglePlayServicesAvailable(activity);
        if (status != ConnectionResult.SUCCESS) {
            if (googleApiAvailability.isUserResolvableError(status)) {
                googleApiAvailability.getErrorDialog(activity, status, RC_PLAY_SERVICES_RESOLUTION).show();
            } else {
                listener.onLocationError(new LocationException("Google play services not found."));
            }
            return false;
        }
        return true;
    }

    public Activity getActivity() {
        return activity;
    }

    public OnLocationListener getListener() {
        return listener;
    }

    public GoogleApiClient getGoogleApiClient() {
        return googleApiClient;
    }

    public abstract void stop();

    public abstract void start();

    public static boolean isLocationEnabled(Context context) {
        LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) && !locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
            //All location services are disabled
            return false;
        }
        return true;
    }

    public void enableLocationSettingsIfRequired() {
        if (googleApiClient == null) {
            return;
        }
        //create a LocationRequest
        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(LOCATION_UPDATE_INTERVAL);
        locationRequest.setInterval(LOCATION_UPDATE_FASTEST_INTERVAL);
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder().addLocationRequest(locationRequest);
        PendingResult<LocationSettingsResult> pendingResult =
                LocationServices.SettingsApi.checkLocationSettings(googleApiClient,
                        builder.build());
        pendingResult.setResultCallback(new ResultCallback<LocationSettingsResult>() {
            @Override
            public void onResult(@NonNull LocationSettingsResult result) {

                Status status = result.getStatus();
                switch (status.getStatusCode()) {
                    case LocationSettingsStatusCodes.SUCCESS:
                        // All location settings are satisfied. The client can
                        // initialize location requests here.

                        break;
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        // Location settings are not satisfied, but this can be fixed
                        // by showing the user a dialog.
                        try {
                            // Show the dialog by calling startResolutionForResult(),
                            // and check the result in onActivityResult().
                            status.startResolutionForResult(
                                    activity,
                                    RC_LOCATION_SETTINGS);
                        } catch (IntentSender.SendIntentException e) {
                            getListener().onLocationError(new LocationException("Unknown Location settings error."));
                        }
                        break;
                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        getListener().onLocationError(new LocationException("Location settings are not available."));
                        break;
                    case LocationSettingsStatusCodes.CANCELED:
                        getListener().onLocationError(new LocationException("Location settings canceled."));
                        break;
                    case LocationSettingsStatusCodes.ERROR:
                        getListener().onLocationError(new LocationException("Unknown Location settings error."));
                        break;
                }
            }
        });


    }
}
