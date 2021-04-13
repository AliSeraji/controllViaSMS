package com.example.smscontroller.databaseModel

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "stations")
data class Station(
    @PrimaryKey(autoGenerate = true)
    var id:Long? = null,
    @ColumnInfo(name = "station_name")
    var name:String = "",
    @ColumnInfo(name = "station_phone")
    var phone:String = ""
)