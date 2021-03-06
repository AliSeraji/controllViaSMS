package com.example.smscontroller


import android.app.Application
import androidx.lifecycle.*
import com.example.smscontroller.databaseModel.Message
import com.example.smscontroller.DatabaseAccess.MessageDao
import com.example.smscontroller.DatabaseAccess.StationDao
import com.example.smscontroller.databaseModel.Station
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*
import kotlin.collections.ArrayList


class SMSViewModel(private val messageDao:MessageDao
                    ,private val stationDao: StationDao
                    ,application:Application)
                    :AndroidViewModel(application){

    private lateinit var monitoringData:MutableLiveData<List<MainData>>
    private lateinit var allMessages:LiveData<List<Message>>
    private lateinit var allStations:LiveData<List<Station>>
    private lateinit var allMessagesOfAStation:LiveData<List<Message>>
    private lateinit var allLastMessages:LiveData<List<Message>>
    private lateinit var allPhoneNo:LiveData<List<String?>>

    companion object{
        var viewModelStatic:SMSViewModel? =null
    }

    init {
        viewModelScope.launch {
            getAllStations()
            getAllLastMessages()
            getAllMessages()
            retAllPhoneNo()
            prepareDataForMonitoring()
        }
    }

    fun insertMessage(message: Message){
        viewModelScope.launch {
            addMessage(message)
        }
    }

    fun updateStationCondition(station: Station){
        viewModelScope.launch {

            updateStation(station)
        }
    }

    fun insertStation(station: Station){
        viewModelScope.launch {
            addStation(station)
        }
    }

    private fun getAllLastMessages(){
        allLastMessages=messageDao.loadAllLastMessages()
    }

    fun getAllMessagesOfAStation(id:Long?):LiveData<List<Message>>{
        getStationMessage(id)
        return allMessagesOfAStation
    }


    private suspend fun addMessage(message: Message){
            messageDao.insert(message)
    }

    private suspend fun addStation(station:Station){
            stationDao.insert(station)
    }

    private suspend fun updateStation(station: Station){
        stationDao.update(station)
    }

    private fun getAllMessages(){
            allMessages=messageDao.loadAll()
    }

    private fun getAllStations(){
        allStations=stationDao.loadAll()
    }

    fun getAllStationsToObserve():LiveData<List<Station>>{
        return allStations
    }

    fun getDataForMonitoring():LiveData<List<MainData>>{
        return monitoringData
    }

    private fun getStationMessage(stationID:Long?){
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

    private fun retAllPhoneNo(){
        allPhoneNo=stationDao.getAllPhoneNo()
    }

    private fun prepareDataForMonitoring(){
        monitoringData=MediatorLiveData<List<MainData>>().apply {
            fun update() {
                val stations = allStations.value ?: return
                val messages=allMessages.value?:return
                val data = ArrayList<MainData>()
                for (index in stations.indices) {
                    if (messages.isEmpty())
                        data.add(MainData(stations[index], Message(null,stations[index].id,"", Date())))
                    else{
                        val stationMsg=ArrayList<Message>()
                        for(msg in messages){
                            if(msg.stationID==stations[index].id)
                                stationMsg.add(msg)
                        }
                        if(stationMsg.size==0){
                            data.add(MainData(stations[index], Message(null,stations[index].id,"", Date())))
                        }
                        else
                            data.add(MainData(stations[index], stationMsg[stationMsg.size-1]))
                    }
                }
                value = data
            }
            addSource(allStations) {update()}
            addSource(allMessages){update()}
            update()
        }

    }

    fun createStation():Station{
        return Station()
    }

    suspend fun deleteStation(s:Station?){
        for(msg in allMessages.value!!){
            if(msg.stationID==s!!.id)
                messageDao.deleteFromMessages(msg)
        }
        stationDao.deleteFromStation(s)
    }

    private suspend fun clear() {
        stationDao.clear()
        messageDao.clear()
    }
}