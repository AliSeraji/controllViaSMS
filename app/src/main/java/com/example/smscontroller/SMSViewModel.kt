package com.example.smscontroller


import android.app.Application
import android.provider.Telephony
import com.example.smscontroller.databaseModel.Message
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.smscontroller.DatabaseAccess.DatabaseOperator
import com.example.smscontroller.DatabaseAccess.MessageDao
import com.example.smscontroller.DatabaseAccess.StationDao
import com.example.smscontroller.databaseModel.Station
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class SMSViewModel(private val messageDao:MessageDao
                    ,private val stationDao: StationDao
                    ,application:Application)
                    :AndroidViewModel(application) {

    private lateinit var allMessages:LiveData<List<Message>>
    private lateinit var allStations:LiveData<List<Station>>
    private lateinit var allMessagesOfAStation:LiveData<List<Message>>

    init {
        viewModelScope.launch {
            getAllStations()
            getAllMessages()
        }
    }

    fun insertMessage(message: Message){
        viewModelScope.launch {
            addMessage(message)
        }
    }

    fun insertStation(station: Station){
        viewModelScope.launch {
            addStation(station)
        }
    }

    fun getAllMessagesOfAStation(id:Long):LiveData<List<Message>>{
        viewModelScope.launch {
            getStationMessage(id)
        }
        return allMessagesOfAStation
    }

    fun retrieveAllMessages():LiveData<List<Message>>{
        return allMessages
    }

    fun retrieveAllStations():LiveData<List<Station>>{
        return allStations
    }

    private suspend fun addMessage(message: Message){
        withContext(Dispatchers.IO){
            messageDao.insert(message)
        }
    }

    private suspend fun addStation(station:Station){
        withContext(Dispatchers.IO){
            stationDao.insert(station)
        }
    }

    private suspend fun getAllMessages(){
        withContext(Dispatchers.IO){
            allMessages=messageDao.loadAll()
        }
    }

    private suspend fun getAllStations(){
        withContext(Dispatchers.IO){
            allStations=stationDao.loadAll()
        }
    }

    private suspend fun getStationMessage(stationID:Long){
        withContext(Dispatchers.IO){
            allMessagesOfAStation=messageDao.loadAllFromStation(stationID)
        }
    }

    fun clearDatabase(){
        viewModelScope.launch {
            clear()
        }
    }

    private suspend fun clear() {
        messageDao.clear()
        stationDao.clear()
    }
}