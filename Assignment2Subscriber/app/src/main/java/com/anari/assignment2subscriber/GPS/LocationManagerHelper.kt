package com.anari.assignment2subscriber.GPS

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import androidx.core.app.ActivityCompat

class LocationManagerHelper(private val context: Context) {
    private var locationManager: LocationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
    private var locationUpdateCallback: ((Location) -> Unit)? = null

    private val locationListener = LocationListener { location ->
        locationUpdateCallback?.invoke(location)
    }

    fun startLocationUpdates(callback: (Location) -> Unit) {
        locationUpdateCallback = callback
        
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            locationManager.requestLocationUpdates(
                LocationManager.GPS_PROVIDER,
                5000, // Update every 5 seconds
                10f,   // Or when moved 10 meters... according to da docs i put these in because it isn't too short of a time/ long of a distance to make it run down the battery really fast
                locationListener
            )
        }
    }

    fun stopLocationUpdates() {
        locationManager.removeUpdates(locationListener)
        locationUpdateCallback = null
    }
} 