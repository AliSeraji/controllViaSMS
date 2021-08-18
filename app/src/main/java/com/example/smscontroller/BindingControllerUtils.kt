package com.example.smscontroller

import android.widget.TextView
import androidx.appcompat.widget.AppCompatTextView
import androidx.databinding.BindingAdapter


@BindingAdapter("deviceName")
fun AppCompatTextView.setDeviceName(item:MainData?){
    item?.let {
        text=item.station.name
    }
}

@BindingAdapter("deviceQuantity")
fun AppCompatTextView.setDeviceQuantity(item:MainData?){
    item?.let{
        text=context.getString(R.string.quantity,textFormatter(item.message?.text))
    }
}

private fun textFormatter(text:String?):String{
    if(text==null || text=="")
        return ""
    val str=text.split(":","(",")")
    if(str.size<2)
        return ""
    return str[1]
}

