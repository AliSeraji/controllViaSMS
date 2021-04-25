package com.example.smscontroller.DatabaseAccess

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.example.smscontroller.databaseModel.Station

@Dao
interface StationDao {
    @Insert
    suspend fun insert(station: Station): Long?

    @Query("SELECT * FROM stations")
    fun loadAll(): LiveData<List<Station>>

    @Query("select * from stations where id=:findId")
    fun getStationById(findId:Long?):LiveData<Station?>

    @Query("DELETE FROM stations")
    suspend fun clear()
}