package com.example.smscontroller


import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.Toast
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.smscontroller.databaseModel.Station
import com.example.smscontroller.databinding.ControllerItemBinding
import kotlinx.coroutines.*
import kotlinx.coroutines.sync.Mutex
import java.lang.ClassCastException


private val ITEM_VIEW_TYPE_ITEM = 1
private val ITEM_VIEW_TYPE_CONTROLLER=2
private val IS_PENDING="IS_PENDING"
private val IS_NOT_ENDING="IS_NOT_ENDING"
class ControllerRecyclerAdopter(context: Context, onItemClickListener: OnRecyclerItemClickListener):ListAdapter<ControllerRecyclerAdopter.DataItem,RecyclerView.ViewHolder>(AdopterDataDiffCallback()){

    private val mutex= Mutex()
    private val adapterScope = CoroutineScope(Dispatchers.Default)
    private val mOnItemClickListener=onItemClickListener
    private val mContext=context

    fun addViewSubmitList(list:List<MainData>?){
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

    /*override fun onBindViewHolder(
        holder: RecyclerView.ViewHolder,
        position: Int,
        payloads: MutableList<Any>
    ) {

        if(payloads.isEmpty()){
            Toast.makeText(mContext,"pay load empty",Toast.LENGTH_LONG).show()
            onBindViewHolder(holder,position)
            return
        }
        var bundle= payloads[0]
        Toast.makeText(mContext,"has pay load",Toast.LENGTH_LONG).show()
        if(bundle==true){
            Toast.makeText(mContext,"ISPENDING",Toast.LENGTH_LONG).show()
        }
        when(holder){
            is ControllerItemHolder->{
                val item=getItem(position) as DataItem.ControllerItem
                holder.bind(mContext,item.data,mOnItemClickListener)
            }
        }

        super.onBindViewHolder(holder, position, payloads)
    }*/

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when(holder){
                is ControllerItemHolder->{
                    val item=getItem(position) as DataItem.ControllerItem
                    holder.bind(mContext,item.data,mOnItemClickListener)
                }
            }
    }


    class ControllerItemHolder(val binding: ControllerItemBinding):RecyclerView.ViewHolder(binding.root){

        fun bind(context: Context,item:MainData,clickListener: OnRecyclerItemClickListener){
            val circularAnimation=CircularAnimation()
            if(!item.station.isPending && binding.getDeviceDetails.isAttachedToWindow){
                //binding.getDeviceQuantity.clearAnimation()
                circularAnimation.circularRevealAnimation(binding.getDeviceDetails)
                circularAnimation.circularRevealAnimation(binding.getDeviceQuantity)
                circularAnimation.circularRevealAnimation(binding.delDevice)
            }
            if(item.station.isPending && binding.getDeviceDetails.isAttachedToWindow){
                circularAnimation.circularHideAnimation(binding.getDeviceDetails)
                circularAnimation.circularHideAnimation(binding.getDeviceQuantity)
                circularAnimation.circularHideAnimation(binding.delDevice)
            }

            //binding.controllerData=item
            binding.mainData=item

            binding.getDeviceDetails.setOnClickListener {
                clickListener.onMoreDetailsClick(absoluteAdapterPosition,item.station.id!!)
            }

            binding.getDeviceQuantity.setOnClickListener {
                circularAnimation.circularHideAnimation(binding.getDeviceDetails)
                circularAnimation.circularHideAnimation(binding.getDeviceQuantity)
                circularAnimation.circularHideAnimation(binding.delDevice)
                clickListener.onRefreshClick(item.station,absoluteAdapterPosition)
            }

            binding.delDevice.setOnClickListener {
                clickListener.onDeleteItemClick(absoluteAdapterPosition,item.station)
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
        fun onRefreshClick(station: Station,pos:Int)
        fun onMoreDetailsClick(pos:Int,id:Long?)
        fun onDeleteItemClick(pos:Int,station:Station?)
    }

    sealed class DataItem{
        abstract val id:Long?
        abstract val isPending:Boolean
        abstract val text:String?
        data class ControllerItem(val data :MainData):DataItem(){
            override val id=data.station.id
            override val isPending: Boolean= data.station.isPending
            override val text: String= data.message!!.text
        }

    }

    class AdopterDataDiffCallback:DiffUtil.ItemCallback<DataItem>(){
        override fun areItemsTheSame(oldItem: DataItem, newItem: DataItem): Boolean {
            return  oldItem.id==newItem.id
        }

        override fun areContentsTheSame(oldItem: DataItem, newItem: DataItem): Boolean {
            return oldItem == newItem
        }

        /*override fun getChangePayload(oldItem: DataItem, newItem: DataItem): Any? {
            var bundle=Bundle()
            return when{
                !oldItem.isPending && newItem.isPending ->{
                    bundle.putBoolean(IS_NOT_ENDING,newItem.isPending)
                }
                oldItem.isPending && !newItem.isPending ->{
                    bundle.putBoolean(IS_PENDING,newItem.isPending)

                }
                else -> null
            }
        }*/
    }
}