package com.github.diwakar1988.location.core;


import com.google.android.gms.common.api.GoogleApiClient;

public interface GoogleServicesConnectionListener extends GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {
    int STATE_NOT_CONNECTED=0;
    int STATE_CONNECTED=1;
    int STATE_SUSPENDED=2;
    int STATE_FAILED=3;

    int getState();
}
