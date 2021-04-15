package com.example.smscontroller

import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.example.smscontroller.databinding.ActivityMainBinding
import java.util.*

class SplahsActivity : AppCompatActivity() {
    private lateinit var binding:ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        binding=DataBindingUtil.setContentView(this,R.layout.activity_main)
        val intent= Intent(this,MainActivity::class.java)
        Handler(Looper.getMainLooper()).postDelayed({
            startActivity(intent)
        }, 1500)
    }


}