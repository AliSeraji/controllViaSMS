package com.example.smscontroller.Fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import com.example.smscontroller.MessageRecyclerAdopter
import com.example.smscontroller.R
import com.example.smscontroller.SMSViewModel
import com.example.smscontroller.databinding.FragmentMessagesBinding
import kotlinx.coroutines.launch

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class MessagesFragment : Fragment() {
    private var param1: String? = null
    private var param2: String? = null

    private lateinit var recyclerView:MessageRecyclerAdopter
    private lateinit var binding:FragmentMessagesBinding
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
        savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment

        binding=DataBindingUtil.inflate(inflater,R.layout.fragment_messages, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        lifecycleScope.launch {
            init(arguments?.getLong("stationId"))
        }
    }

    private fun init(id:Long?){
        viewModel= ViewModelProvider(requireActivity()).get(SMSViewModel::class.java)
        recyclerView= MessageRecyclerAdopter(requireContext())
        binding.deviceRecyclerView.adapter=recyclerView
        viewModel.getAllMessagesOfAStation(id).observe(viewLifecycleOwner,{
            it.let {
                recyclerView.addViewSubmitList(it)
            }
        })

        binding.backBtn.setOnClickListener {
            view?.findNavController()?.navigate(R.id.action_messagesFragment_to_controllerFragment)
        }

    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            MessagesFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}