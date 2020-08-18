package dev.jetlaunch.locationtracker.models.location

import dev.jetlaunch.locationtracker.behavior.IDB
import dev.jetlaunch.locationtracker.entity.LocationData
import java.io.File
import java.lang.Exception

internal class LocationFileDB(dir: File?) :
    IDB<List<LocationData>> {

    private val file = File(dir, "locations.fdb")

    init {
        if (!file.exists()) file.createNewFile()
    }

    @Synchronized
    override fun readRecord(): List<LocationData> {
        return try{
            file.readLines().mapNotNull(::mapStringToLocationData)
        }catch (e: Exception){
            emptyList()
        }

    }

    @Synchronized
    override fun writeRecord(items: List<LocationData>): Boolean {
        return return try {
            clearFile()
            file.printWriter().use { writer ->
                val converted = items.map(::mapLocationDataToString)
                for (item in converted) {
                    writer.println(item)
                }
                writer.flush()
            }
            true
        } catch (e: Exception) {
            false
        }
    }


    private fun clearFile(){
        val writer = file?.printWriter()
        writer.print("")
        writer.flush()
    }

    private fun mapStringToLocationData(line: String): LocationData? {
        return try {
            val data = line.split("|")
            val timestamp = data[0].toLong()
            val lat = data[1].toDouble()
            val lon = data[2].toDouble()
            LocationData(timestamp, lat, lon)
        }catch (e: Exception){
            null
        }



    }

    private fun mapLocationDataToString(locationData: LocationData): String{
        return locationData.timeStamp.toString() + "|" +locationData.lat.toString()+"|" + locationData.lon.toString()
    }
}