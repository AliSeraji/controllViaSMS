package com.example.smscontroller

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.smscontroller.DatabaseAccess.MessageDao
import com.example.smscontroller.DatabaseAccess.StationDao

class SMSViewModelFactory(private val dataSource1:MessageDao,private val dataSource2:StationDao,private val application:Application)
    :ViewModelProvider.Factory {

    @Suppress("unchecked_cast")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SMSViewModel::class.java)) {
            return SMSViewModel(dataSource1,dataSource2, application) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}