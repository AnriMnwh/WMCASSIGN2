package com.anari.assignment2publisher

import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.hivemq.client.mqtt.mqtt5.Mqtt5BlockingClient
import com.hivemq.client.mqtt.mqtt5.Mqtt5Client
import org.json.JSONObject
import android.widget.Toast

class MainActivity : AppCompatActivity() {
    private var client: Mqtt5BlockingClient? = null
    private lateinit var locationManager: LocationManager
    private val LOCATION_PERMISSION_REQUEST_CODE = 1001
    private var isTransmitting = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        val transmit : Button = findViewById(R.id.publishbutton)
        val stopTransmit: Button = findViewById(R.id.stoppublishbutton)

        transmit.setOnClickListener {
            if (!isTransmitting) {
                checkLocationPermissions()
            }
        }

        stopTransmit.setOnClickListener {
            stopTransmission()
        }
    }

    private fun checkLocationPermissions() {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                LOCATION_PERMISSION_REQUEST_CODE
            )
        } else {
            starttransmission()
        }
    }

    private fun setupLocationUpdates() {
        locationManager = getSystemService(LOCATION_SERVICE) as LocationManager
        
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            locationManager.requestLocationUpdates(
                LocationManager.GPS_PROVIDER,
                5000, // Update every 5 seconds
                5f,   // Or when moved 5 meters
                locationListener
            )
        }
    }

    private val locationListener = LocationListener { location ->
        publishLocation(location)
    }

    fun publishLocation(location: location) {
        val locationData = JSONObject().apply {
            put("latitude", location.latitude)
            put("longitude", location.longitude)
            put("timestamp", System.currentTimeMillis())
            put("identifier", client.identifier)
        }

        client?.publishWith()
            ?.topic("assignment/location")
            ?.payload(locationData.toString().toByteArray())
            ?.send()
    }

    private fun starttransmission() {
        val Id : EditText = findViewById(R.id.studentID)
        val IDString = Id.text.toString()
        client = Mqtt5Client.builder()
            .identifier(IDString)
            .serverHost("broker.sundaebytestt.com")
            .serverPort(1883)
            .build()
            .toBlocking()
        
        client?.connect()

        setupLocationUpdates()
        isTransmitting = true
        Toast.makeText(this, "Location transmission started", Toast.LENGTH_SHORT).show()
    }

    private fun stopTransmission() {
        if (isTransmitting) {
            try {
                locationManager.removeUpdates(locationListener)
                client?.disconnect()
                client = null
                isTransmitting = false
                Toast.makeText(this, "Location transmission stopped", Toast.LENGTH_SHORT).show()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    override fun onDestroy() {
        stopTransmission()
        super.onDestroy()
    }

}