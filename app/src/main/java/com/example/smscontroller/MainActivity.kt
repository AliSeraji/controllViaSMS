package com.example.smscontroller

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.Looper
import android.view.View
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.smscontroller.DatabaseAccess.DatabaseOperator
import com.example.smscontroller.databaseModel.Station
import com.example.smscontroller.databinding.ActivityMain2Binding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*
import java.util.logging.Handler
import kotlin.collections.ArrayList

const val ITEM_IS_PENDING=1201
const val ITEM_IS_NOT_PENDING=1202



class MainActivity : AppCompatActivity() {

    private lateinit var binding:ActivityMain2Binding
    private lateinit var viewModel: SMSViewModel
    private var doubleBackToExitPressedOnce = false

    private val permissions = arrayOf(
        Manifest.permission.SEND_SMS,
        Manifest.permission.READ_PHONE_STATE,
        Manifest.permission.RECEIVE_SMS
    )

    companion object{
        var stationPhysicalID=ArrayList<String>()
        var stationPhoneNumbers=ArrayList<String?>()
        var allStations:MutableList<Station> = mutableListOf()
        var isActivityOpen:Boolean=true
        var newStations=ArrayList<Station>()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        forceRTL()
        //supportActionBar?.hide()
        setApplicationLocale("fa")
        binding=DataBindingUtil.setContentView(this, R.layout.activity_main2)
        init()

    }

    private fun setUpViewModel(){
        val application= requireNotNull(this).application
        val dataSource= DatabaseOperator.getInstance(application)
        val viewModelFactory=SMSViewModelFactory(
            dataSource.messageDao,
            dataSource.stationDao,
            application
        )
        viewModel= ViewModelProvider(this, viewModelFactory).get(SMSViewModel::class.java)
        SMSViewModel.viewModelStatic=viewModel
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!arePermission()) {
                requestMultiplePermissions()
            }
        }


    }

    private fun init(){
        setUpViewModel()
        viewModel.getAllStationsToObserve().observe(this, {
            this.lifecycleScope.launch {
                allStations.clear()
                stationPhoneNumbers.clear()
                stationPhysicalID.clear()
                for (station in it) {
                    allStations.add(station)
                    stationPhoneNumbers.add(station.phone)
                    stationPhysicalID.add(station.physicalID)
                }
            }
        })


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

    override fun onResume() {
        super.onResume()
        isActivityOpen=true
    }

    override fun onDestroy() {
        super.onDestroy()
        isActivityOpen=false
    }

    override fun onStop() {
        super.onStop()
        isActivityOpen=false
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        isActivityOpen=false
    }

    override fun onStart() {
        super.onStart()
        isActivityOpen=true
    }

    override fun onPause() {
        super.onPause()
        isActivityOpen=false
    }

    override fun onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed()
            isActivityOpen=true
            return
        }

        this.doubleBackToExitPressedOnce = true
        Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show()

        android.os.Handler(Looper.getMainLooper()).postDelayed({
            doubleBackToExitPressedOnce = false
            isActivityOpen=false
        }, 2000)

    }

}