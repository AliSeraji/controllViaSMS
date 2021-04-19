package com.example.smscontroller.databaseModel

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "stations")
data class Station(
    @PrimaryKey(autoGenerate = true)
    var id:Long? = null,
    var name:String = "",
    var phone:String = "",
    var requestDataText:String=""
)