package com.github.diwakar1988.location;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.github.diwakar1988.location.core.BaseLocationProvider;
import com.github.diwakar1988.location.provider.LocationProvider;
import com.github.diwakar1988.location.core.OnLocationListener;

public class MainActivity extends AppCompatActivity implements View.OnClickListener,OnLocationListener{

    private static final int RC_PERMISSIONS = 0XFF32;
    private TextView tvResults;
    private Button btnStart;
    private Button btnEnd;
    private LocationProvider locationProvider;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tvResults = (TextView) findViewById(R.id.tv_results);
        btnStart = (Button) findViewById(R.id.btn_start);
        btnEnd = (Button) findViewById(R.id.btn_end);

        btnEnd.setOnClickListener(this);
        btnStart.setOnClickListener(this);

        locationProvider=new LocationProvider(this,this);

        setLocationSettingsMessage();
    }

    private boolean hasPermissions(){
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            String[] permissions = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};
            ActivityCompat.requestPermissions(this,
                    permissions,
                    RC_PERMISSIONS);

            return false;
        }
        return true;
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_start:
            case R.id.btn_end:
                fetchLocation();
                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        locationProvider.enableLocationSettingsIfRequired();
    }

    private void setLocationSettingsMessage() {
        if (BaseLocationProvider.isLocationEnabled(this)){
            setResults("Location enabled");
        }else {
            setResults("Location not enabled");
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        locationProvider.start();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        locationProvider.stop();
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==BaseLocationProvider.RC_LOCATION_SETTINGS){
            setLocationSettingsMessage();
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == RC_PERMISSIONS) {
            boolean allAllowed = true;
            for (int i = 0; i < grantResults.length; i++) {
                if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                    allAllowed = false;
                    break;
                }
            }
            if (allAllowed) {
                //all permissions granted now start app
                fetchLocation();
            } else {
                setResults("Please grant LOCATION permissions and try again.");
            }
        }
    }

    @Override
    public void onLocationAvailable(Location location) {
        StringBuilder sb = new StringBuilder();
        if (location!=null){
            sb.append("LAT = ");
            sb.append(location.getLatitude());
            sb.append("\n");
            sb.append("LNG = ");
            sb.append(location.getLongitude());
        }else {
            sb.append("Location not available.");
        }
        setResults(sb.toString());
    }

    @Override
    public void onLocationError(Throwable t) {
        tvResults.setText(t.getMessage());
    }

    private void setResults(String msg) {
        this.tvResults.setText(msg);
    }

    public void fetchLocation() {
        if (hasPermissions()){
            locationProvider.provideLocation();
        }
    }
}
