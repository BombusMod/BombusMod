/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.bombusmod.android.location;

import PEP.location.LocationIO;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;

import androidx.core.app.ActivityCompat;

import org.bombusmod.BombusModActivity;

/**
 *
 * @author Vitaly
 */
public class LocationAndroid extends LocationIO {

    LocationManager locationManager;
    Location lastKnownLocation;

    Context ctx;

    public LocationAndroid(BombusModActivity ctx) {
        this.ctx = ctx;
        // Acquire a reference to the system Location Manager
        locationManager = (LocationManager) ctx.getSystemService(Context.LOCATION_SERVICE);
    }

    public void getCoordinates() {
        if (ActivityCompat.checkSelfPermission(ctx, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(ctx, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        lastKnownLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
    }

    public String getLatitude() {
        return String.valueOf(lastKnownLocation.getLatitude());
    }

    public String getLongitude() {
        return String.valueOf(lastKnownLocation.getLongitude());
    }
}
