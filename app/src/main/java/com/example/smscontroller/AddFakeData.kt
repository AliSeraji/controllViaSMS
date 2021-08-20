package com.example.smscontroller

import com.example.smscontroller.databaseModel.Station

fun addFakeData():ArrayList<Station>{
    val fakeStations=ArrayList<Station>()
    for(i in 0..100){
        val station=Station()
        station.isPending=false
        station.name= "vm$i"
        station.phone="123"
        station.physicalID="vm$i"
        station.requestDataText="logo?vm$i,word"
        fakeStations.add(station)
    }
    return fakeStations
}