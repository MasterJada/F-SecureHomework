package dev.jetlaunch.locationtracker.api

import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.os.Build
import dev.jetlaunch.locationtracker.LocationService
import dev.jetlaunch.locationtracker.entity.LocationData
import dev.jetlaunch.locationtracker.simpleQuery

class LocationApi {
    /**
     * Method which run background service for tracking User Api
     */
    fun startLocationUpdates(context: Context) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O)
            context.startService(Intent(context, LocationService::class.java))
        else
            context.startForegroundService(Intent(context, LocationService::class.java))
    }

    /**
     * stop background service
     */
    fun stopLocationUpdates(context: Context) {
        context.stopService(Intent(context, LocationService::class.java))
    }

    /**
     * update timeout between receiving GPS coordinates
     */
    fun changeLocationTimeout(timeout: Long, context: Context) {
        val cv = ContentValues()
        cv.put(LocationContentProvider.COL_TIMEOUT, timeout)
        context.contentResolver.insert(LocationContentProvider.TIMEOUT_URI, cv)
    }

    /**
     * Method return current timeout in milliseconds
     */
    fun getCurrentTimeout(context: Context): Long {
        context.contentResolver.simpleQuery(LocationContentProvider.TIMEOUT_URI)?.use { cursor ->
                return if (cursor.moveToFirst()) {
                    cursor.getLong(cursor.getColumnIndex(LocationContentProvider.COL_TIMEOUT))
                } else {
                    1000 * 60 * 60
                }
            }
        return 1000 * 60 * 60L
    }

    /**
     * Method return last   10 saved GPS coordinates and timestamp
     */
    fun getLastLocation(context: Context): List<LocationData> {
        context.contentResolver.simpleQuery(LocationContentProvider.LAST_LOCATIONS_URI)?.use { cursor ->
            val items = ArrayList<LocationData>()
            while (cursor.moveToNext()) {
                val timestamp =
                    cursor.getLong(cursor.getColumnIndex(LocationContentProvider.COL_TIMESTAMP))
                val lat = cursor.getDouble(cursor.getColumnIndex(LocationContentProvider.COL_LAT))
                val lon = cursor.getDouble(cursor.getColumnIndex(LocationContentProvider.COL_LON))
                items.add(LocationData(timestamp, lat, lon))
            }
            return items
        }
        return emptyList()
    }

    /**
     * Method return is service is active
     */
    fun isServiceActive(): Boolean{
        return LocationService.isOnline
    }
}