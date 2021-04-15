package com.example.smscontroller.DatabaseAccess

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.smscontroller.databaseModel.Message


@Dao
interface MessageDao {
    @Insert
    suspend fun insert(message: Message):Long?

    @Query("SELECT * FROM messages")
    fun loadAll(): LiveData<List<Message>>

    @Query("SELECT * FROM messages WHERE  id =  :messageID")
    suspend fun loadByID(messageID:Long?):Message

    @Query("SELECT * FROM messages WHERE station_id=:stationID")
    fun loadAllFromStation(stationID:Long?): LiveData<List<Message>>

    @Query("DELETE FROM messages")
    suspend fun clear()

}