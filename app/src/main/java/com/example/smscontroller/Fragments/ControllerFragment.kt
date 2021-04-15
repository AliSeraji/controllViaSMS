package com.example.smscontroller.Fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.example.smscontroller.DatabaseAccess.DatabaseOperator
import com.example.smscontroller.R
import com.example.smscontroller.SMSViewModel
import com.example.smscontroller.SMSViewModelFactory
import com.example.smscontroller.databinding.FragmentControllerBinding
import kotlinx.coroutines.launch

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class ControllerFragment : Fragment() {
    private var param1: String? = null
    private var param2: String? = null

    private lateinit var binding:FragmentControllerBinding
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
        // Inflate the layout for this fragment
        binding=DataBindingUtil.inflate(inflater,R.layout.fragment_controller, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        lifecycleScope.launch {
         init()
        }
    }


    private fun init(){
        val application= requireNotNull(this.activity).application
        val dataSource=DatabaseOperator.getInstance(application)
        val viewModelFactory=SMSViewModelFactory(dataSource.messageDao,dataSource.stationDao,application)
        viewModel=ViewModelProvider(this,viewModelFactory).get(SMSViewModel::class.java)
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
}