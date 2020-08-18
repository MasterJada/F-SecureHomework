package dev.jetlaunch.locationtracker.models.location

import dev.jetlaunch.locationtracker.behavior.IDB
import dev.jetlaunch.locationtracker.entity.LocationData
import kotlin.collections.ArrayList

internal class LocationDataBase(private val db: IDB<List<LocationData>>) {
    companion object{
        const val CAPACITY = 10
    }

    private val items: ArrayList<LocationData> = ArrayList(db.readRecord())


    fun add(item: LocationData, timeout: Long) {
        if((item.timeStamp - (items.maxBy { it.timeStamp }?.timeStamp ?: 0L)) < timeout){
            return
        }
        if(items.size < CAPACITY){
            items.add(item)
            save()
            return
        }
        items.sortBy { it.timeStamp }
        items.removeAt(0)
        items.add(item)
        save()
    }

    fun getItems(): List<LocationData>{
        return db.readRecord()
    }

    private fun save(){
        db.writeRecord(items)
    }


}