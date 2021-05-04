package com.example.smscontroller

import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder

import android.telephony.SmsMessage
import android.widget.Toast
import androidx.core.content.getSystemService
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

                var index=MainActivity.stationPhoneNumbers.indexOf(phoneNumber)

                if(index!=-1){
                    var message = Message(null, MainActivity.allStations[index].id!!.toLong(), receivedMessage, Date())

                    GlobalScope.launch {
                        SMSViewModel.viewModelStatic!!.insertMessage(message)
                    }
                    intent.putExtra("receivedMessage", "$receivedMessage##$phoneNumber")
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    if(!MainActivity.isActivityOpen)
                        context!!.startActivity(intent)
                }

            }
        }
    }
}