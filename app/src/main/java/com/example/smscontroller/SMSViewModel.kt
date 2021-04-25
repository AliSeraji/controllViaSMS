package com.example.smscontroller


import android.app.Application
import com.example.smscontroller.databaseModel.Message
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.smscontroller.DatabaseAccess.MessageDao
import com.example.smscontroller.DatabaseAccess.StationDao
import com.example.smscontroller.databaseModel.Station
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SMSViewModel(private val messageDao:MessageDao
                    ,private val stationDao: StationDao
                    ,application:Application)
                    :AndroidViewModel(application){

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


    private suspend fun addMessage(message: Message){
            messageDao.insert(message)
    }

    private suspend fun addStation(station:Station){
            stationDao.insert(station)
    }

    private fun getAllMessages(){
            allMessages=messageDao.loadAll()
    }

    private fun getAllStations(){
            allStations=stationDao.loadAll()
    }

    fun getDataForObservation():LiveData<List<Station>>{
        return allStations
    }

    private fun getStationMessage(stationID:Long){
            allMessagesOfAStation=messageDao.loadAllFromStation(stationID)
    }

    fun clearDatabase(){
        viewModelScope.launch {
            clear()
        }
    }

    fun getStationById(id:Long?):LiveData<Station?>{
        return stationDao.getStationById(id)
    }

    fun createStation():Station{
        return Station()
    }

    private suspend fun clear() {
        messageDao.clear()
        stationDao.clear()
    }
}