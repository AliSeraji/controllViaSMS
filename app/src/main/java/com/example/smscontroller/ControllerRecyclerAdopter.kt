package com.example.smscontroller


import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.smscontroller.databaseModel.Message
import com.example.smscontroller.databaseModel.Station
import com.example.smscontroller.databinding.ControllerItemBinding
import com.example.smscontroller.databinding.FragmentAddDeviceBinding
import kotlinx.coroutines.*
import java.lang.ClassCastException


private val ITEM_VIEW_TYPE_ITEM = 1

class ControllerRecyclerAdopter( onItemClickListener: OnRecyclerItemClickListener):ListAdapter<ControllerRecyclerAdopter.DataItem,RecyclerView.ViewHolder>(AdopterDataDiffCallback()){

    private val adapterScope = CoroutineScope(Dispatchers.Default)
    private val mOnItemClickListener=onItemClickListener

    fun addViewSubmitList(list:List<Station>?){
        adapterScope.launch {
            val items=when(list){
                null-> listOf(null)
                else ->list.map { DataItem.ControllerItem(it)}
            }
            withContext(Dispatchers.Main){
                    submitList(items)
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when(viewType){
                ITEM_VIEW_TYPE_ITEM->ControllerItemHolder.from(parent)
                else -> throw ClassCastException("UnKnown viewType $viewType")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
            when(holder){
                is ControllerItemHolder->{
                    val item=getItem(position) as DataItem.ControllerItem
                    holder.bind(item.stationItem,mOnItemClickListener)
                }
            }
    }


    class ControllerItemHolder(val binding: ControllerItemBinding):RecyclerView.ViewHolder(binding.root){

        fun bind(item:Station,clickListener: OnRecyclerItemClickListener){


            binding.deviceName.text=item.name


            binding.getDeviceDetails.setOnClickListener {
                clickListener.onMoreDetailsClick(absoluteAdapterPosition,item.id)
            }
            binding.getDeviceQuantity.setOnClickListener {
                clickListener.onRefreshClick(absoluteAdapterPosition,item.id,binding.deviceQuantity)
            }
            binding.executePendingBindings()
        }

        companion object{
            fun from(parent:ViewGroup):ControllerItemHolder{
                val layoutInflater=LayoutInflater.from(parent.context)
                val binding=ControllerItemBinding.inflate(layoutInflater,parent,false)
                return ControllerItemHolder(binding)
            }

        }
    }

    override fun getItemViewType(position: Int): Int {
        return when(getItem(position)){
            is DataItem.ControllerItem-> ITEM_VIEW_TYPE_ITEM
        }
    }

    interface OnRecyclerItemClickListener{
        fun onRefreshClick(pos:Int,id:Long?,textView: TextView)
        fun onMoreDetailsClick(pos:Int,id:Long?)
    }

    sealed class DataItem{
        abstract val id:Long?
        data class ControllerItem(val stationItem :Station):DataItem(){
            override val id=stationItem.id
        }
    }

    class AdopterDataDiffCallback:DiffUtil.ItemCallback<DataItem>(){
        override fun areItemsTheSame(oldItem: DataItem, newItem: DataItem): Boolean {
            return oldItem.id==newItem.id
        }
        override fun areContentsTheSame(oldItem: DataItem, newItem: DataItem): Boolean {
            return oldItem==newItem
        }
    }
}