package com.anari.assignment2subscriber

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.anari.assignment2subscriber.Controllers.MqttManager
import com.anari.assignment2subscriber.GPS.LocationManagerHelper
import com.google.android.gms.maps.SupportMapFragment
import com.anari.assignment2subscriber.Map.MapManager
import com.anari.assignment2subscriber.adapters.DeviceAdapter
import org.json.JSONObject
//sir, respectfully this project was absolute hell. It took me so long and multiple redos of this assignment to get this thing even working.
//I think I may have lost a couple years of my life with all of the writing and googling I had to do.
class MainActivity : AppCompatActivity() {
    private lateinit var locationManagerHelper: LocationManagerHelper
    private lateinit var mqttManager: MqttManager
    private lateinit var dbHelper: LocationDatabaseHelper
    private lateinit var mapManager: MapManager
    private lateinit var deviceAdapter: DeviceAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        //thingies to get map working and make it go brrr
        mapFragment.getMapAsync { googleMap ->
            mapManager = MapManager(googleMap)
        }
        initializeComponents()
        setupMqttSubscription()

        findViewById<RecyclerView>(R.id.deviceList).apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = DeviceAdapter().also { deviceAdapter = it }
        }
    }

    private fun initializeComponents() {
        locationManagerHelper = LocationManagerHelper(this)
        mqttManager = MqttManager()
        dbHelper = LocationDatabaseHelper(this)
    }

    private fun setupMqttSubscription() {
        mqttManager.connect("Subscriber-${System.currentTimeMillis()}")
        mqttManager.subscribe { payload ->
            handleReceivedLocation(payload)
        }
    }

    private fun handleReceivedLocation(payload: String) {
        try {
            val locationData = JSONObject(payload)
            val latitude = locationData.getDouble("latitude")
            val longitude = locationData.getDouble("longitude")
            val timestamp = locationData.getLong("timestamp")
            val deviceId = locationData.getString("identifier")
            
            val speed = dbHelper.getLatestSpeed(deviceId)
            
            dbHelper.insertLocation(deviceId, latitude, longitude, timestamp)
            updateMap(deviceId, latitude, longitude)
            
            runOnUiThread {
                deviceAdapter.updateDevice(deviceId, speed)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun updateMap(deviceId: String, latitude: Double, longitude: Double) {
        mapManager.updateDeviceLocation(deviceId, latitude, longitude)
    }

    override fun onDestroy() {
        super.onDestroy()
        mapManager.clearMap()
        dbHelper.close()
    }

}