package dev.jetlaunch.locationtracker.api

import android.content.ContentProvider
import android.content.ContentUris
import android.content.ContentValues
import android.content.UriMatcher
import android.database.Cursor
import android.database.MatrixCursor
import android.net.Uri
import dev.jetlaunch.locationtracker.models.location.LocationDataBase
import dev.jetlaunch.locationtracker.models.location.LocationFileDB
import dev.jetlaunch.locationtracker.models.timeout.TimeoutDataBase
import dev.jetlaunch.locationtracker.models.timeout.TimeoutFileDB

class LocationContentProvider : ContentProvider() {
    companion object {
        const val AUTHORITY = "com.jetlaunch.locationtracker"

        //columns for time DB
        const val COL_TIMEOUT = "COL_TIMEOUT"

        //columns for Location Data
        const val COL_TIMESTAMP = "COL_TIMESTAMP"
        const val COL_LON = "COL_LON"
        const val COL_LAT = "COL_LAT"

        private const val TIMEOUT_PATH = "TIMEOUT_PATH"
        private const val LAST_LOCATIONS_PATH = "LAST_LOCATIONS"

        val TIMEOUT_URI: Uri = Uri.parse("content://$AUTHORITY/$TIMEOUT_PATH")
        val LAST_LOCATIONS_URI: Uri = Uri.parse("content://$AUTHORITY/$LAST_LOCATIONS_PATH")

        const val URI_TIMEOUT_INT = 0
        const val URI_LAST_LOCATIONS_INT = 1

        const val LOCATION_TYPE = "TYPE_LOCATION"
        const val TIMEOUT_TYPE = "TYPE_TIMEOUT"


        private val uriMatcher = UriMatcher(UriMatcher.NO_MATCH).apply {
            addURI(
                AUTHORITY,
                TIMEOUT_PATH,
                URI_TIMEOUT_INT
            )
            addURI(
                AUTHORITY,
                LAST_LOCATIONS_PATH,
                URI_LAST_LOCATIONS_INT
            )
        }

    }

    private lateinit var locationsDB: LocationDataBase
    private lateinit var timeoutFileDB: TimeoutDataBase

    override fun onCreate(): Boolean {
        locationsDB = LocationDataBase(
            LocationFileDB(context?.filesDir)
        )
        timeoutFileDB = TimeoutDataBase( TimeoutFileDB(context?.filesDir))

        return true
    }

    /**
     * We not delete anything inside our files
     */
    override fun delete(uri: Uri, selection: String?, selectionArgs: Array<String>?): Int {
        return -1
    }

    override fun getType(uri: Uri): String? {
        return when (uriMatcher.match(uri)) {
            URI_LAST_LOCATIONS_INT -> LOCATION_TYPE
            URI_TIMEOUT_INT -> TIMEOUT_TYPE
            else -> null
        }
    }

    /**
     * All insertion to LOCATION is encapsulated inside library
     * So we can only insert TIMEOUT_URI
     */
    override fun insert(uri: Uri, values: ContentValues?): Uri? {
        val resultUri = ContentUris.withAppendedId(uri, 0L)
        when (uriMatcher.match(uri)) {
            URI_TIMEOUT_INT -> {
                (values?.get(COL_TIMEOUT) as? Long)?.let {
                    timeoutFileDB.saveTimeout(it)
                    context?.contentResolver?.notifyChange(resultUri, null)
                }
            }
        }

        return resultUri
    }

    /**
     * FDB is too primitive so we can pass only uri
     * to get COL_TIMEOUT
     * or {
     * COL_TIMESTAMP
     * COL_LAT
     * COL_LON
     * }
     */
    override fun query(
        uri: Uri, projection: Array<String>?, selection: String?,
        selectionArgs: Array<String>?, sortOrder: String?
    ): Cursor? {
        return when (uriMatcher.match(uri)) {
            URI_LAST_LOCATIONS_INT -> {
                val cursor = MatrixCursor(arrayOf(
                    COL_TIMESTAMP,
                    COL_LAT,
                    COL_LON
                ))
                locationsDB.getItems().forEach {
                    cursor.newRow().add(COL_TIMESTAMP, it.timeStamp)
                        .add(COL_LAT, it.lat)
                        .add(COL_LON, it.lon)

                }
                cursor
            }
            URI_TIMEOUT_INT -> {
                val cursor = MatrixCursor(arrayOf(COL_TIMEOUT))
                cursor.newRow().add(COL_TIMEOUT, timeoutFileDB.readTimeOut())
                cursor
            }
            else -> null
        }
    }

    /**
     * We don't update any data so it's useless
     */
    override fun update(
        uri: Uri, values: ContentValues?, selection: String?,
        selectionArgs: Array<String>?
    ): Int {
        return -1
    }
}
