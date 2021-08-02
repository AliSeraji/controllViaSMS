package com.example.smscontroller.DatabaseAccess

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.smscontroller.databaseModel.Converters
import com.example.smscontroller.databaseModel.Message
import com.example.smscontroller.databaseModel.Station


@Database(entities = [Message::class,Station::class],version=7,exportSchema = false)
@TypeConverters(Converters::class)

abstract class DatabaseOperator:RoomDatabase() {
    abstract val messageDao:MessageDao
    abstract val stationDao:StationDao

    companion object{
        @Volatile
        private var INSTANCE:DatabaseOperator?=null

        fun getInstance(context:Context):DatabaseOperator{
            synchronized(this){
                var instance= INSTANCE
                if(instance==null) {
                    instance = Room.databaseBuilder(
                        context.applicationContext,
                        DatabaseOperator::class.java,
                        "message_history_database"
                    ).fallbackToDestructiveMigration()
                        .build()
                    INSTANCE=instance
                }
                return instance
            }
        }
    }
}