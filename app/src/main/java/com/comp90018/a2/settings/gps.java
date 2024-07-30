package com.comp90018.a2.settings;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;

import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.widget.TextView;

import java.util.List;
import java.util.Locale;

public class gps extends AppCompatActivity implements LocationListener{
    private LocationManager locationManager;
    public String getLocation(Location location) {

        try {
            //Runtime permissions
            if (ContextCompat.checkSelfPermission(gps.this, Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(gps.this, new String[]{
                        Manifest.permission.ACCESS_FINE_LOCATION
                }, 100);
            }
            locationManager = (LocationManager) getApplicationContext().getSystemService(LOCATION_SERVICE);
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,5000,5,gps.this);

            Geocoder geocoder = new Geocoder(gps.this, Locale.getDefault());
            List<Address> addresses = geocoder.getFromLocation(location.getLatitude(),location.getLongitude(),1);
            String address = addresses.get(0).getAddressLine(0);
            return address;

        }catch (Exception e){
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public void onLocationChanged(Location location) {

    }
}
