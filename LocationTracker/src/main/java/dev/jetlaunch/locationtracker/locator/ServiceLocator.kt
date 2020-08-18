package dev.jetlaunch.locationtracker.locator

import android.content.Context
import android.location.LocationManager
import dev.jetlaunch.locationtracker.models.location.LocationDataBase
import dev.jetlaunch.locationtracker.models.location.LocationFileDB
import dev.jetlaunch.locationtracker.models.timeout.TimeoutDataBase
import dev.jetlaunch.locationtracker.models.timeout.TimeoutFileDB
import kotlin.reflect.KClass
import kotlin.reflect.KProperty

internal object ServiceLocator {
     val instances = HashMap<KClass<*>, Any>()

    inline fun<reified T: Any> register(obj: T){
        instances[T::class] = obj
    }

    inline fun<reified T: Any> get(): T{
        return instances[T::class] as T
    }

   inline operator fun<reified T: Any> getValue(ref: Any?, property: KProperty<*>): T{
      return get()
   }

    fun init(context: Context){
        register(context.filesDir)
        register(LocationFileDB(get()))
        register(LocationDataBase(get<LocationFileDB>()))
        register(TimeoutFileDB(get()))
        register(TimeoutDataBase(get<TimeoutFileDB>()))
        register(context.getSystemService(Context.LOCATION_SERVICE) as LocationManager)
    }
}

