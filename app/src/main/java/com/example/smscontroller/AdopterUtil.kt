package com.example.smscontroller

import androidx.appcompat.widget.AppCompatTextView
import androidx.databinding.BindingAdapter

class AdopterUtil {

@BindingAdapter("device_name")
fun AppCompatTextView.setDeviceName(item:MainData){
    text=item.station.name
}

@BindingAdapter("device_quantity")
fun AppCompatTextView.setDeviceQuantity(item:MainData){
    text=context.getString(R.string.quantity,formatText(item.message!!.text))
}

private fun formatText(text: String?): String {
    if(text==null || text=="")
        return ""
    val str=text.split(":","(",")")
    if(str.size<2)
        return ""
    return str[1]
}

}