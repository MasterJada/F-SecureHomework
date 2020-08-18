package dev.jetlaunch.locationtracker

import android.content.ContentResolver
import android.database.Cursor
import android.net.Uri

fun ContentResolver.simpleQuery(uri: Uri): Cursor?{
    return query(uri, null, null, null, null)
}