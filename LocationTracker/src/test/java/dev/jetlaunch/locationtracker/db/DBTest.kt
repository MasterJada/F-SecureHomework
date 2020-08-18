package dev.jetlaunch.locationtracker.db

import dev.jetlaunch.locationtracker.entity.LocationData
import dev.jetlaunch.locationtracker.models.location.LocationDataBase
import org.junit.Assert
import org.junit.Test


class DBTest {
    private val coords = listOf(
        LocationData(0L, 0.0, 0.0),
        LocationData(1L, 46.42443555, 30.7264483),
        LocationData(2L, 46.42443555, 30.72650235),
        LocationData(3L, 46.4244555, 30.7263496)
    )

    @Test
    fun `test capacity` (){
        val dataBase =
            LocationDataBase(TestDB())
        for ( i  in 0..12){
            dataBase.add(LocationData(0L, 0.0, 0.0), 0)
        }
        Assert.assertEquals(10, dataBase.getItems().size)
    }

    @Test
    fun `test adding items`(){
        val dataBase =
            LocationDataBase(TestDB())
        for( i in 0 .. 20){
            dataBase.add(LocationData(i.toLong(), 0.0, 0.0),0)
        }
        Assert.assertEquals(10, dataBase.getItems().size)
        Assert.assertEquals(11L, dataBase.getItems().first().timeStamp)
        Assert.assertEquals(20L, dataBase.getItems().last().timeStamp)
    }


    @Test
    fun `test mapping to list`() {
        val testDB = TestDB()
        val dataBase =
            LocationDataBase(testDB)
        for (c in coords) {
            dataBase.add(c, 0)
        }
        Assert.assertArrayEquals(coords.toTypedArray(), testDB.data.toTypedArray())
    }

    @Test
    fun `test test timeout`(){
        val testDB = TestDB()
        val dataBase =
            LocationDataBase(testDB)
        for( i in 0 .. 8){
            dataBase.add(LocationData(i.toLong(), 0.0, 0.0), 2L)
        }
        Assert.assertEquals(4, dataBase.getItems().size)
    }

}