package dev.jetlaunch.locationtracker.models.timeout

import dev.jetlaunch.locationtracker.behavior.IDB

internal class TimeoutDataBase (private val db: IDB<Long>){
    fun saveTimeout(timeout: Long){
        db.writeRecord(timeout)
    }
    fun readTimeOut(): Long = db.readRecord()
}