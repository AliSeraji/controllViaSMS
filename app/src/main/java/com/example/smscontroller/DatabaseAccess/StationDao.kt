package com.example.smscontroller.DatabaseAccess

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.smscontroller.databaseModel.Station

@Dao
interface StationDao {
    @Insert
    suspend fun insert(station: Station): Long?

    @Update
    suspend fun update(station: Station)

    @Query("SELECT * FROM stations")
    fun loadAll(): LiveData<List<Station>>

    @Query("select * from stations where id=:findId")
    fun getStationById(findId:Long?):LiveData<Station?>

    @Query("select * from stations where phone=:phoneNumber")
    fun getStationByPhone(phoneNumber:String):LiveData<List<Station>>

    @Query("select phone from stations")
    fun getAllPhoneNo():LiveData<List<String?>>

    @Query("DELETE FROM stations")
    suspend fun clear()

    @Delete
    suspend fun deleteFromStation(station:Station?)
}