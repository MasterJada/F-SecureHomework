package dev.jetlaunch.locationtracker.models.timeout

import dev.jetlaunch.locationtracker.behavior.IDB
import java.io.File
import java.lang.Exception

internal class TimeoutFileDB(dir: File?) : IDB<Long> {
    private val dbFile = File(dir, "timeout.fdb")
    init {
        if (!dbFile.exists()) dbFile.createNewFile()
    }
    @Synchronized
    override fun  writeRecord(items: Long): Boolean{
         dbFile.printWriter().use { writer ->
             writer.write(items.toString())
             writer.flush()
         }
       return true
    }

    @Synchronized
    override fun readRecord(): Long {
        return try {
            dbFile.readLines().first().toLong()
        }catch (e: Exception){
            return 1000 * 60 * 60
        }
    }
}