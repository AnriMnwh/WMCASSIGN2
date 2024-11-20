package com.anari.assignment2subscriber

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import java.text.SimpleDateFormat
import java.util.*
import com.anari.assignment2subscriber.LocationDatabaseHelper.*
//thingies to make the UI go brrr
class DeviceReportActivity : AppCompatActivity() {
    private lateinit var dbHelper: LocationDatabaseHelper
    private var startTimestamp: Long = 0
    private var endTimestamp: Long = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_device_report)

        dbHelper = LocationDatabaseHelper(this)
        val deviceId = intent.getStringExtra("deviceId") ?: return

        findViewById<TextView>(R.id.deviceIdTitle).text = "Device: $deviceId"

        setupDateButtons()
        updateReport(deviceId)
    }

    private fun setupDateButtons() {
        findViewById<Button>(R.id.startDate).setOnClickListener {
            showDateTimePicker { timestamp ->
                startTimestamp = timestamp
                updateButtonText(findViewById(R.id.startDate), timestamp)
                updateReport(intent.getStringExtra("deviceId") ?: return@showDateTimePicker)
            }
        }

        findViewById<Button>(R.id.endDate).setOnClickListener {
            showDateTimePicker { timestamp ->
                endTimestamp = timestamp
                updateButtonText(findViewById(R.id.endDate), timestamp)
                updateReport(intent.getStringExtra("deviceId") ?: return@showDateTimePicker)
            }
        }
    }

    private fun showDateTimePicker(onDateTimeSelected: (Long) -> Unit) {
        val calendar = Calendar.getInstance()
        
        DatePickerDialog(this, { _, year, month, day ->
            TimePickerDialog(this, { _, hour, minute ->
                calendar.set(year, month, day, hour, minute)
                onDateTimeSelected(calendar.timeInMillis)
            }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true).show()
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show()
    }

    private fun updateButtonText(button: Button, timestamp: Long) {
        val sdf = SimpleDateFormat("MM/dd/yyyy HH:mm", Locale.getDefault())
        button.text = sdf.format(Date(timestamp))
    }

    private fun updateReport(deviceId: String) {
        if (startTimestamp == 0L || endTimestamp == 0L) return

        val (minSpeed, maxSpeed, avgSpeed) = dbHelper.getSpeedStats(deviceId, startTimestamp, endTimestamp)
        
        findViewById<TextView>(R.id.minSpeedReport).text = 
            "Minimum Speed: ${String.format("%.2f", minSpeed)} km/h"
        findViewById<TextView>(R.id.maxSpeedReport).text = 
            "Maximum Speed: ${String.format("%.2f", maxSpeed)} km/h"
        findViewById<TextView>(R.id.avgSpeedReport).text = 
            "Average Speed: ${String.format("%.2f", avgSpeed)} km/h"
    }
} 