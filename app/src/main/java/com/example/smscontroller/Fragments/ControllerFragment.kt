package com.example.smscontroller.Fragments

import android.content.BroadcastReceiver
import android.os.Bundle
import android.telephony.SmsManager
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import com.example.smscontroller.*
import com.example.smscontroller.databinding.FragmentControllerBinding
import kotlinx.coroutines.launch

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class ControllerFragment : Fragment(),ControllerRecyclerAdopter.OnRecyclerItemClickListener,ControllerRecyclerAdopter.OnRecyclerItemFormatTextListener {
    private var param1: String? = null
    private var param2: String? = null

    private lateinit var binding:FragmentControllerBinding
    private lateinit var viewModel: SMSViewModel
    private lateinit var recyclerView:ControllerRecyclerAdopter
    private lateinit var broadcastObserver:BroadcastReceiver
    private lateinit var allPhoneNo:List<String?>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding=DataBindingUtil.inflate(inflater,R.layout.fragment_controller, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        lifecycleScope.launch {
         init()
        }
        events()
    }


    private fun init(){
        receiveSMS()
        viewModel=ViewModelProvider(requireActivity()).get(SMSViewModel::class.java)
        recyclerView=ControllerRecyclerAdopter(requireContext(),this,this)
        binding.deviceRecyclerView.adapter=recyclerView
        viewModel.getDataForObservation().observe(viewLifecycleOwner,{
            it.let {
                recyclerView.addViewSubmitList(it)
            }
        })
        viewModel.getAllPhoneNo().observe(viewLifecycleOwner,{
            allPhoneNo=it!!
        })

    }

    private fun events(){
        binding.addNewDevice.setOnClickListener {
            view?.findNavController()?.navigate(R.id.action_controllerFragment_to_addDeviceFragment)
        }
    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            ControllerFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
        }
    }

    override fun onRefreshClick(pos: Int, id: Long?, textView: TextView) {

        viewModel.getStationById(id).observe(viewLifecycleOwner,{
            sendSMS(it!!.phone,it.requestDataText)
            Toast.makeText(requireContext(),R.string.sms_sent,Toast.LENGTH_LONG).show()
        })

    }

    override fun onMoreDetailsClick(pos: Int, id: Long?) {
        var bundle= bundleOf("stationId" to id)
        view?.findNavController()?.navigate(R.id.action_controllerFragment_to_messagesFragment,bundle)
    }

    private fun sendSMS(deviceNo:String,text:String){
        val sms=SmsManager.getDefault()
        sms.sendTextMessage(deviceNo,"ourControllerApp",text,null,null)
    }

    private fun receiveSMS(){
        var msg:String?=null
        /*var br=object:BroadcastReceiver(){
            override fun onReceive(context: Context?, intent: Intent?) {
                val extras=intent!!.extras
                if(extras!=null) {
                    for (sms in Telephony.Sms.Intents.getMessagesFromIntent(intent)) {
                        if (allPhoneNo.contains(sms.displayOriginatingAddress)) {

                            Toast.makeText(requireContext(),sms.displayMessageBody,Toast.LENGTH_LONG).show()
                        }
                    }
                }
            }
        }
        requireActivity().registerReceiver(br,IntentFilter("android.provider.Telephony.SMS_RECEIVED"))*/
    }

    override fun onFormatText(text: String?): String {
        if(text==null || text=="")
            return ""
        val str=text.split(":","(",")")
        if(str.size<2)
            return ""
        return str[1]
    }
}

