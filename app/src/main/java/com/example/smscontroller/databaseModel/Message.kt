package com.example.smscontroller.databaseModel

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import java.util.*

@Entity(
    tableName = "messages",
    foreignKeys = arrayOf(
        ForeignKey(
            entity = Station::class,
            parentColumns = arrayOf("id"),
            childColumns = arrayOf("station_id"),
            onDelete = ForeignKey.CASCADE
        )
    )
)
data class  Message(
    @PrimaryKey(autoGenerate = true)
    var id: Long? = null,
    @ColumnInfo(name = "station_id")
    var stationID: Long?,
    var text: String = "",
    @ColumnInfo(name = "created_time")
    var createdTime: Date
)