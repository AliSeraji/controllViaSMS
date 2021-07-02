package com.example.smscontroller.DatabaseAccess

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.example.smscontroller.databaseModel.Message
import com.example.smscontroller.databaseModel.Station


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

    @Query("select * from (select * from messages where station_id=(select id from stations) order by id asc)")
    fun loadAllLastMessages():LiveData<List<Message>>

    @Query("select * from(select * from messages where station_id=:stationId) as t  limit 1")
    fun loadLastMessageOfEachStation(stationId:Long?):LiveData<Message>

    @Query("DELETE FROM messages")
    suspend fun clear()

    @Query("delete from messages where station_id=:sid")
    suspend fun deleteFromMessages(sid:Long?)
}