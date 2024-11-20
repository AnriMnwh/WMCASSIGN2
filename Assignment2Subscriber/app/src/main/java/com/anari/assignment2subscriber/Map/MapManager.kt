package com.anari.assignment2subscriber.Map

import android.graphics.Color
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.PolylineOptions
import java.util.HashMap
//For this whole class, I followed online tutorials on how to use google maps (tutorials will be in references) and came up with my own stuff to make it work i think.
class MapManager(private val googleMap: GoogleMap) {
    private val markers = HashMap<String, MarkerOptions>()
    private val pathPoints = HashMap<String, MutableList<LatLng>>()
    private val deviceColors = HashMap<String, Int>()
    private var colorIndex = 0
    
    private val availableColors = listOf(
        Color.BLUE,
        Color.RED,
        Color.GREEN,
        Color.MAGENTA,
        Color.CYAN,
        Color.YELLOW,
        Color.DKGRAY
    )

    fun updateDeviceLocation(deviceId: String, latitude: Double, longitude: Double) {
        val position = LatLng(latitude, longitude)

        if (!deviceColors.containsKey(deviceId)) {
            deviceColors[deviceId] = availableColors[colorIndex % availableColors.size]
            colorIndex++
        }

        if (markers.containsKey(deviceId)) {
            markers[deviceId]?.position(position)
        } else {
            markers[deviceId] = MarkerOptions()
                .position(position)
                .title(deviceId)
        }

        if (!pathPoints.containsKey(deviceId)) {
            pathPoints[deviceId] = mutableListOf()
        }
        pathPoints[deviceId]?.add(position)

        googleMap.clear()

        markers.values.forEach { markerOptions ->
            googleMap.addMarker(markerOptions)
        }

        pathPoints.forEach { (deviceId, points) ->
            if (points.size > 1) {
                val polylineOptions = PolylineOptions()
                    .addAll(points)
                    .color(deviceColors[deviceId] ?: Color.BLUE)
                    .width(5f)
                googleMap.addPolyline(polylineOptions)
            }
        }

        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(position, 15f))
    }

    fun clearMap() {
        googleMap.clear()
        markers.clear()
        pathPoints.clear()
        deviceColors.clear()
        colorIndex = 0
    }
} 