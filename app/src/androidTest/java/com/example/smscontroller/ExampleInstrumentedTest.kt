package com.example.smscontroller

import androidx.room.Room
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.example.smscontroller.DatabaseAccess.DatabaseOperator
import com.example.smscontroller.DatabaseAccess.MessageDao
import com.example.smscontroller.DatabaseAccess.StationDao
import com.example.smscontroller.databaseModel.Message
import com.example.smscontroller.databaseModel.Station
import org.junit.After

import org.junit.Test
import org.junit.runner.RunWith

import org.junit.Assert.*
import org.junit.Before
import java.io.IOException
import java.lang.Exception
import java.util.*

/**
 * Instrumented test, which will execute on an Android device.
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
@RunWith(AndroidJUnit4::class)
class ExampleInstrumentedTest {
    private lateinit var messageDao: MessageDao
    private lateinit var stationDao: StationDao
    private lateinit var db:DatabaseOperator

    @Before
    fun createDb(){
        val context=InstrumentationRegistry.getInstrumentation().targetContext
        db= Room.inMemoryDatabaseBuilder(context,DatabaseOperator::class.java).allowMainThreadQueries().build()
        messageDao=db.messageDao
        stationDao=db.stationDao
    }

    @After
    @Throws(IOException::class)
    fun closeDb() {
        db.close()
    }

    @Test
    @Throws(Exception::class)
    fun insertAndGetData() {
        val station = Station()
        station.id=1.toLong()
        station.name="hashem"
        station.phone="98986565"
        stationDao.insert(station)
        val aStation=stationDao.getStationById(1.toLong())

        val message=Message(1.toLong(),1.toLong(),"sssss",Date())
        messageDao.insert(message)
        val aMessage=messageDao.loadByID(1.toLong())

        assertEquals(aMessage?.id,1.toLong())
        assertEquals(aStation.value?.id,null)

        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        assertEquals("com.example.smscontroller", appContext.packageName)
    }
}