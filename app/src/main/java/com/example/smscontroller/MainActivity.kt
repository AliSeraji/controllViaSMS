package com.example.smscontroller

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.smscontroller.DatabaseAccess.DatabaseOperator
import com.example.smscontroller.databinding.ActivityMain2Binding
import java.util.*

class MainActivity : AppCompatActivity() {

    private lateinit var binding:ActivityMain2Binding
    private lateinit var viewModel: SMSViewModel

    private val permissions = arrayOf(
        Manifest.permission.SEND_SMS,
        Manifest.permission.READ_PHONE_STATE,
        Manifest.permission.RECEIVE_SMS
    )


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        forceRTL()
        //supportActionBar?.hide()
        setApplicationLocale("fa")
        binding=DataBindingUtil.setContentView(this, R.layout.activity_main2)
        init()

    }

    private fun init(){
        val application= requireNotNull(this).application
        val dataSource= DatabaseOperator.getInstance(application)
        val viewModelFactory=SMSViewModelFactory(dataSource.messageDao,dataSource.stationDao,application)
        viewModel= ViewModelProvider(this,viewModelFactory).get(SMSViewModel::class.java)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!arePermission()) {
                requestMultiplePermissions()
            }
        }

    }

    private fun forceRTL(){
        getWindow().getDecorView().setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
    }


    private fun setApplicationLocale(locale: String) {
        val resources = resources
        val dm = resources.displayMetrics
        val config = resources.configuration
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            config.setLocale(Locale(locale.toLowerCase()))
        } else {
            config.locale = Locale(locale.toLowerCase())
        }
        resources.updateConfiguration(config, dm)
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    fun arePermission(): Boolean {
        return permissions.none {
            checkSelfPermission(it) != PackageManager.PERMISSION_GRANTED
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    fun requestMultiplePermissions() {

        val remainedPermissions = permissions.filter {
            checkSelfPermission(it) != PackageManager.PERMISSION_GRANTED
        }
        requestPermissions(remainedPermissions.toTypedArray(), 101)
    }


    @RequiresApi(api = Build.VERSION_CODES.M)
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {

        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 101) {
            if (grantResults.any { it != PackageManager.PERMISSION_GRANTED }) { //avvalin chizi ke did ro baz az avval seda mokona
                if (permissions.any { shouldShowRequestPermissionRationale(it) }) { //Call shouldShowRequestPermissionRationale to see if the user checked Never ask again. shouldShowRequestPermissionRationale method returns false only if the user selected Never ask again or device policy prohibits the app from having that permission
                    AlertDialog.Builder(this)
                        .setMessage("تمام دسترسی ها برای استفاده کامل از نرم افزار الزامی است")
                        .setPositiveButton("باشه") { dialog, which -> requestMultiplePermissions() }
                        .setNegativeButton("تمایلی ندارم") { dialog, which -> dialog.dismiss() }
                        .create()
                        .show()
                }
            } else {
//                sendSMS()
                Toast.makeText(this, "با موفقیت انجام شد", Toast.LENGTH_SHORT).show()
            }
        }
    }



}