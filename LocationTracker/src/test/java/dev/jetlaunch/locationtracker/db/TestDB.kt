package dev.jetlaunch.locationtracker.db

import dev.jetlaunch.locationtracker.entity.LocationData
import dev.jetlaunch.locationtracker.behavior.IDB

class TestDB: IDB<List<LocationData>> {
    var data = listOf<LocationData>()
    override fun readRecord() = data
    override fun writeRecord(items: List<LocationData>): Boolean {
        data = items
        return true
    }
}