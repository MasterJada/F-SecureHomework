package dev.jetlaunch.locationtracker

import android.Manifest
import android.app.*
import android.content.Intent
import android.content.pm.PackageManager
import android.database.ContentObserver
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.*
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import dev.jetlaunch.locationtracker.entity.LocationData
import dev.jetlaunch.locationtracker.models.location.LocationDataBase
import dev.jetlaunch.locationtracker.api.LocationContentProvider
import dev.jetlaunch.locationtracker.locator.ServiceLocator
import dev.jetlaunch.locationtracker.models.timeout.TimeoutDataBase
import java.util.*

class LocationService : Service() {
    companion object {
        private const val NOTIFICATION_CHANEL = "Location"
        private const val SERVICE_INT_ID = 2804
        var isOnline = false
    }


    private val locationManager: LocationManager? by ServiceLocator

    private val database: LocationDataBase by ServiceLocator
    private val timeoutFileDatabase: TimeoutDataBase by ServiceLocator

    private var timeout = 0L


    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    override fun onCreate() {
        super.onCreate()
        isOnline = true
        timeout = timeoutFileDatabase.readTimeOut()
        contentResolver.registerContentObserver(
            LocationContentProvider.TIMEOUT_URI,
            true,
            object : ContentObserver(Handler(Looper.getMainLooper())) {
                override fun onChange(selfChange: Boolean) {
                    super.onChange(selfChange)
                    contentResolver.simpleQuery(LocationContentProvider.TIMEOUT_URI)?.use { cursor ->
                        if (cursor.moveToFirst()) {
                            timeout =
                                cursor.getLong(cursor.getColumnIndex(LocationContentProvider.COL_TIMEOUT))
                            restart()
                        }
                    }

                }
            })
        startForeground(SERVICE_INT_ID, createNotification())
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        startListener()

        return START_STICKY
    }

    private val locationListener = object : LocationListener {
        override fun onLocationChanged(location: Location?) {
            location?.let {
                val timestamp = Calendar.getInstance().timeInMillis
                database.add(LocationData(timestamp, it.latitude, it.longitude), timeout)
            }

        }

        override fun onStatusChanged(p0: String?, p1: Int, p2: Bundle?) = Unit

        override fun onProviderEnabled(p0: String?) = Unit

        override fun onProviderDisabled(p0: String?) = Unit

    }

    private fun restart() {
        locationManager?.removeUpdates(locationListener)
        startListener()
    }

    private fun startListener() {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {

            locationManager?.requestLocationUpdates(
                LocationManager.GPS_PROVIDER,
                timeout,
                0F,
                locationListener
            )
            locationManager?.requestLocationUpdates(
                LocationManager.NETWORK_PROVIDER,
                timeout,
                0F,
                locationListener
            )
            locationManager?.requestLocationUpdates(
                LocationManager.PASSIVE_PROVIDER,
                timeout,
                0F,
                locationListener
            )
        }
    }

    private fun createNotification(): Notification {
        createNotificationChanel()
        return NotificationCompat.Builder(this, "Location")
            .setSmallIcon(R.drawable.ic_baseline_location_on_24)
            .setContentTitle("Location in background")
            .setContentText("Now in background app track your location")
            .build()
    }

    private fun createNotificationChanel(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Notification chanel"
            val descriptionText = "Simple chanel"
            val importance = NotificationManager.IMPORTANCE_LOW
            val mChannel = NotificationChannel(NOTIFICATION_CHANEL, name, importance)
            mChannel.description = descriptionText
            val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(mChannel)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        isOnline = false
        locationManager?.removeUpdates(locationListener)
    }
}
