package com.example.smscontroller.Fragments


import android.os.Bundle
import android.telephony.SmsManager
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import androidx.recyclerview.widget.RecyclerView
import com.example.smscontroller.*
import com.example.smscontroller.databaseModel.Station
import com.example.smscontroller.databinding.FragmentControllerBinding
import kotlinx.coroutines.launch

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"



class ControllerFragment : Fragment(),ControllerRecyclerAdopter.OnRecyclerItemClickListener,ControllerRecyclerAdopter.OnRecyclerItemFormatTextListener {

    enum class RecyclerviewIncomingOperation{
        STARTING,
        IDLE,
        REFRESHING,
        DELETING,
        UPDATING,
    }

    private var param1: String? = null
    private var param2: String? = null
    private var recyclerviewIncomingOperation=RecyclerviewIncomingOperation.STARTING

    private lateinit var binding:FragmentControllerBinding
    private lateinit var viewModel: SMSViewModel
    private lateinit var recyclerView:ControllerRecyclerAdopter
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
        binding.lifecycleOwner = this
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

        viewModel=ViewModelProvider(requireActivity()).get(SMSViewModel::class.java)
        recyclerView=ControllerRecyclerAdopter(requireContext(),this,this)
        recyclerView.stateRestorationPolicy=RecyclerView.Adapter.StateRestorationPolicy.PREVENT
        binding.deviceRecyclerView.adapter=recyclerView

        viewModel.getDataForMonitoring().observe(viewLifecycleOwner,{
            it.let {
                when(recyclerviewIncomingOperation){
                    RecyclerviewIncomingOperation.STARTING->{
                        recyclerView.addViewSubmitList(it)
                    }
                    RecyclerviewIncomingOperation.DELETING->{
                        Toast.makeText(requireContext(),"Deleting",Toast.LENGTH_LONG).show()
                        recyclerviewIncomingOperation=RecyclerviewIncomingOperation.IDLE
                    }
                    RecyclerviewIncomingOperation.REFRESHING->{
                        Toast.makeText(requireContext(),"Refreshing",Toast.LENGTH_LONG).show()
                        recyclerviewIncomingOperation=RecyclerviewIncomingOperation.IDLE
                    }
                    RecyclerviewIncomingOperation.UPDATING->{
                        Toast.makeText(requireContext(),"Updating",Toast.LENGTH_LONG).show()
                        recyclerviewIncomingOperation=RecyclerviewIncomingOperation.IDLE
                    }
                    else ->{}
                }
            }
        })
        


    }

    private fun events(){
        binding.addNewDevice.setOnClickListener {
            view?.findNavController()?.navigate(R.id.action_controllerFragment_to_addDeviceFragment)
        }
    }

    override fun onRefreshClick(station: Station,pos:Int) {

        recyclerviewIncomingOperation=RecyclerviewIncomingOperation.REFRESHING
        //recyclerView.notifyItemChanged(pos)
        /*viewModel.getDataForMonitoring().observe(viewLifecycleOwner, {
            it.let {
                lifecycleScope.launch{
                    var index=MainActivity.stationPhysicalID.indexOf(station.physicalID)
                    it[index].station.isPending=true
                    //MainActivity.allStations[index].isPending = true
                    viewModel.updateStationCondition(it[index].station)
                }
            }
                recyclerView.notifyItemChanged(pos)
                //recyclerView.notifyDataSetChanged()
                recyclerView.addViewSubmitList(it)
        })*/
        var smsManager = SmsManager.getDefault()
        if (android.os.Build.VERSION.SDK_INT < 22) {
            Log.e("Alert", "Checking SubscriptionId");
        } else {
            smsManager =
                SmsManager.getSmsManagerForSubscriptionId(smsManager.subscriptionId)
        }
        smsManager.sendTextMessage(station.phone, null, station.requestDataText, null, null)
        //Toast.makeText(requireContext(),R.string.sms_sent,Toast.LENGTH_LONG).show()
    }

    override fun onMoreDetailsClick(pos: Int, id: Long?) {
        var bundle= bundleOf("stationId" to id)
        view?.findNavController()?.navigate(R.id.action_controllerFragment_to_messagesFragment,bundle)
    }

    override fun onDeleteItemClick(pos: Int, station: Station?) {
        recyclerviewIncomingOperation=RecyclerviewIncomingOperation.DELETING
        viewModel.getDataForMonitoring().observe(viewLifecycleOwner,{
            it.let {
                lifecycleScope.launch {
                    //MainActivity.allStations=removeFromMutableList(MainActivity.allStations,station!!)
                    MainActivity.allStations.remove(station)
                    MainActivity.stationPhysicalID.remove(station!!.physicalID)
                    MainActivity.stationPhoneNumbers.remove(station.phone)
                    viewModel.deleteStation(station)
                }
                recyclerView.notifyItemRemoved(pos)
                recyclerView.notifyItemRangeChanged(pos, it.size)
                recyclerView.notifyDataSetChanged()

            }
        })

    }

    override fun onFormatText(text: String?): String {
        if(text==null || text=="")
            return ""
        val str=text.split(":","(",")")
        if(str.size<2)
            return ""
        return str[1]
    }

   companion object{
       var receivedSMSs=ArrayList<MainData>()
   }

}