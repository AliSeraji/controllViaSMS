package com.example.smscontroller


import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.telephony.SmsMessage
import com.example.smscontroller.databaseModel.Message
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.util.*


class ReceiveSMS :BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {
        var extras = intent?.extras

        if (extras != null) {
            var sms = extras.get("pdus") as Array<*>
            for (i in sms.indices) {
                var format = extras.getString("format")
                val smsMessage = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    SmsMessage.createFromPdu(sms[i] as ByteArray, format)
                } else {
                    SmsMessage.createFromPdu(sms[i] as ByteArray)
                }
                var phoneNumber = smsMessage.displayOriginatingAddress
                //phoneNumber = phoneNumber.removeRange(0, 3)

                var receivedMessage = smsMessage.messageBody

                var intent = Intent(context, MainActivity::class.java)

                //here we need to find the stationId that has the physical address of received SMS
                //now it works
                var indexOfId=MainActivity.stationPhysicalID.indexOf(textFormatter(receivedMessage))
                //var index=MainActivity.stationPhoneNumbers.indexOf(indexOfId)

                if(indexOfId!=-1){
                    //get message id from viewModel

                    var message = Message(null, MainActivity.allStations[indexOfId].id!!, receivedMessage, Date())

                    GlobalScope.launch {
                        SMSViewModel.viewModelStatic!!.insertMessage(message)
                        MainActivity.allStations[indexOfId].isPending=false
                        SMSViewModel.viewModelStatic!!.updateStationCondition(MainActivity.allStations[indexOfId])
                    }
                    intent.putExtra("receivedMessage", "$receivedMessage##$phoneNumber")
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    if(!MainActivity.isActivityOpen)
                        context!!.startActivity(intent)
                }
            }
        }
    }

    private fun textFormatter(string:String):String{
        if(string=="")
            return ""
        val str=string.split(":","(",")")
        if(str.size<2)
            return ""
        return str[0]
    }


}