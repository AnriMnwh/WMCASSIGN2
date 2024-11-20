package com.anari.assignment2subscriber
import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

class LocationDatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    companion object {
        const val DATABASE_NAME = "LocationDatabase"
        const val DATABASE_VERSION = 1
        const val TABLE_LOCATIONS = "locations"
        
        const val COLUMN_ID = "id"
        const val COLUMN_DEVICE_ID = "device_id"
        const val COLUMN_LATITUDE = "latitude"
        const val COLUMN_LONGITUDE = "longitude"
        const val COLUMN_TIMESTAMP = "timestamp"
    }

    override fun onCreate(db: SQLiteDatabase) {
        val createTable = """
            CREATE TABLE $TABLE_LOCATIONS (
                $COLUMN_ID INTEGER PRIMARY KEY AUTOINCREMENT,
                $COLUMN_DEVICE_ID TEXT,
                $COLUMN_LATITUDE REAL,
                $COLUMN_LONGITUDE REAL,
                $COLUMN_TIMESTAMP INTEGER
            )
        """.trimIndent()
        
        db.execSQL(createTable)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL("DROP TABLE IF EXISTS $TABLE_LOCATIONS")
        onCreate(db)
    }

    fun insertLocation(deviceId: String, latitude: Double, longitude: Double, timestamp: Long) {
        val db = this.writableDatabase
        val values = ContentValues().apply {
            put(COLUMN_DEVICE_ID, deviceId)
            put(COLUMN_LATITUDE, latitude)
            put(COLUMN_LONGITUDE, longitude)
            put(COLUMN_TIMESTAMP, timestamp)
        }
        db.insert(TABLE_LOCATIONS, null, values)
    }

    private fun calculateDistance(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
        val R = 6371.0
        //i'll be real sir, I went online for help here and below for getSpeedStats.
        val dLat = Math.toRadians(lat2 - lat1)
        val dLon = Math.toRadians(lon2 - lon1)
        
        val a = sin(dLat / 2) * sin(dLat / 2) +
                cos(Math.toRadians(lat1)) * cos(Math.toRadians(lat2)) *
                sin(dLon / 2) * sin(dLon / 2)
        
        val c = 2 * atan2(sqrt(a), sqrt(1 - a))
        
        return R * c
    }

    fun getSpeedStats(deviceId: String, startTime: Long, endTime: Long): Triple<Double, Double, Double> {
        val db = this.readableDatabase
        val cursor = db.query(
            TABLE_LOCATIONS,
            arrayOf(COLUMN_LATITUDE, COLUMN_LONGITUDE, COLUMN_TIMESTAMP),
            "$COLUMN_DEVICE_ID = ? AND $COLUMN_TIMESTAMP BETWEEN ? AND ?",
            arrayOf(deviceId, startTime.toString(), endTime.toString()),
            null,
            null,
            "$COLUMN_TIMESTAMP ASC"
        )

        var minSpeed = Double.MAX_VALUE
        var maxSpeed = Double.MIN_VALUE
        var totalSpeed = 0.0
        var count = 0

        cursor.use { cursor ->
            //From here on, I have a bit of an idea of how this works and I mostly went online to see what to do and followed guides on calculating speed.
            if (cursor.count < 2) {
                return Triple(0.0, 0.0, 0.0)
            }

            var prevLat: Double? = null
            var prevLon: Double? = null
            var prevTime: Long? = null

            while (cursor.moveToNext()) {
                val lat = cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_LATITUDE))
                val lon = cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_LONGITUDE))
                val time = cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_TIMESTAMP))

                if (prevLat != null && prevLon != null && prevTime != null) {
                    val distance = calculateDistance(prevLat, prevLon, lat, lon)
                    val timeDiff = (time - prevTime) / (1000.0 * 60.0 * 60.0)
                    if (timeDiff > 0) {
                        val speed = distance / timeDiff

                        // Update stats
                        minSpeed = minOf(minSpeed, speed)
                        maxSpeed = maxOf(maxSpeed, speed)
                        totalSpeed += speed
                        count++
                    }
                }

                prevLat = lat
                prevLon = lon
                prevTime = time
            }
        }
        if (count == 0) {
            return Triple(0.0, 0.0, 0.0)
        }
        return Triple(
            minSpeed,
            maxSpeed,
            totalSpeed / count
        )
    }

    fun getLatestSpeed(deviceId: String): Double {
        val currentTime = System.currentTimeMillis()
        // Get speed stats for last minute
        val (_, _, avgSpeed) = getSpeedStats(deviceId, currentTime - 60000, currentTime)
        return avgSpeed
    }
} 