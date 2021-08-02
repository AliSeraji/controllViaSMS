package com.example.smscontroller.Fragments

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import com.example.smscontroller.MainActivity
import com.example.smscontroller.R
import com.example.smscontroller.SMSViewModel
import com.example.smscontroller.databinding.FragmentAddDeviceBinding
import kotlinx.coroutines.launch

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class AddDeviceFragment : Fragment() {
    private var param1: String? = null
    private var param2: String? = null
    private lateinit var binding:FragmentAddDeviceBinding
    private lateinit var viewModel: SMSViewModel

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
        binding=DataBindingUtil.inflate(inflater,R.layout.fragment_add_device, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewLifecycleOwner.lifecycleScope.launch {
            init()
        }
    }

    private suspend fun init(){
        viewModel=ViewModelProvider(requireActivity()).get(SMSViewModel::class.java)

        binding.returnBack.setOnClickListener {
            view?.findNavController()?.navigate(R.id.action_addDeviceFragment_to_controllerFragment)
        }

        binding.submit.setOnClickListener {
            binding.submit.startLoading()
            binding.submit.isEnabled=false
            binding.submit.resetAfterFailed=true


            if(binding.stationNumberInput.text.toString().trim().isNotEmpty()
                    && binding.stationNumberInput.text.toString().trim().isNotEmpty()
                    && binding.stationRequestInput.text.toString().trim().isNotEmpty()
                    ){
                val station=viewModel.createStation()

                station.name=binding.stationNameInput.text.toString()
                station.phone=binding.stationNumberInput.text.toString()
                station.requestDataText=binding.stationRequestInput.text.toString()
                station.physicalID=textFormatter(binding.stationRequestInput.text.toString())
                station.isPending=false

                viewModel.insertStation(station)
                Toast.makeText(requireContext(),R.string.success,Toast.LENGTH_LONG).show()
                binding.stationNameInput.text!!.clear()
                binding.stationNumberInput.text!!.clear()
                binding.stationRequestInput.text!!.clear()
                binding.submit.loadingSuccessful()
                Handler(Looper.getMainLooper()).postDelayed({
                    binding.submit.isEnabled=true
                    binding.submit.reset()
                }, 1500)

            }
            else{
                Toast.makeText(requireContext(),R.string.fill_all_fields,Toast.LENGTH_LONG).show()
                binding.submit.loadingFailed()
            }
            binding.submit.isEnabled=true
            binding.submit.reset()
        }

        binding.clear.setOnClickListener {
            viewModel.clearDatabase()
            Toast.makeText(requireContext(),R.string.data_clear_warning,Toast.LENGTH_LONG).show()
            MainActivity.allStations.clear()
            MainActivity.stationPhoneNumbers.clear()
            MainActivity.stationPhysicalID.clear()
        }

    }


    private fun textFormatter(string:String?):String{
        if(string==null || string=="")
            return ""
        val str=string.split("?",",")
        if(str.size<2)
            return ""
        return str[1]
    }

}