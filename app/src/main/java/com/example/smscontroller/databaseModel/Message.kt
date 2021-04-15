package com.example.smscontroller.databaseModel

import androidx.room.*
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
    ),
    indices = [
        Index("station_id")
    ]
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