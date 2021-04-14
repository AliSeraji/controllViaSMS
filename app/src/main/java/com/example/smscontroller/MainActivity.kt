package com.example.smscontroller

import android.content.res.Configuration
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.example.smscontroller.databinding.ActivityMainBinding
import java.util.*

class MainActivity : AppCompatActivity() {
    private lateinit var binding:ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=DataBindingUtil.setContentView(this,R.layout.activity_main)
        forceRTL()
    }

    private fun forceRTL(){
        //val locale = Locale("fa_IR") // This is for Persian (Iran)
        //Locale.setDefault(locale)
        //val config = Configuration()
        //config.setLocale(locale)
        //applicationContext.resources.updateConfiguration(config, applicationContext.resources.displayMetrics)
        getWindow().getDecorView().setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
    }
}