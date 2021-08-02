package com.example.smscontroller


import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.smscontroller.databaseModel.Station
import com.example.smscontroller.databinding.ControllerItemBinding
import kotlinx.coroutines.*
import java.lang.ClassCastException


private val ITEM_VIEW_TYPE_ITEM = 1

class ControllerRecyclerAdopter(context: Context, onItemClickListener: OnRecyclerItemClickListener, onFormatTextListener:OnRecyclerItemFormatTextListener):ListAdapter<ControllerRecyclerAdopter.DataItem,RecyclerView.ViewHolder>(AdopterDataDiffCallback()){

    private val adapterScope = CoroutineScope(Dispatchers.Default)
    private val mOnItemClickListener=onItemClickListener
    private val mContext=context
    private val mOnRecyclerItemFormatTextListener=onFormatTextListener

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

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
            when(holder){
                is ControllerItemHolder->{
                    val item=getItem(position) as DataItem.ControllerItem
                    holder.bind(mContext,item.data,mOnItemClickListener,mOnRecyclerItemFormatTextListener)
                }
            }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder,
                                  position: Int,
                                  payloads: MutableList<Any>) {
        if (!payloads.isEmpty()) {
            // Because these updates can be batched,
            // there can be multiple payloads for a single bind
            when (payloads[0]) {
                Payload.FAVORITE_CHANGE -> {
                    // Change only the "favorite" icon,
                    // leave background image alone:
                    bindFavoriteIcon(holder,
                        items[position].isFavorited)
                }
            }
        }
        // When payload list is empty,
        // or we don't have logic to handle a given type,
        // default to full bind:
        super.onBindViewHolder(holder, position, payloads)
    }


    class ControllerItemHolder(val binding: ControllerItemBinding):RecyclerView.ViewHolder(binding.root){
        private val circularAnimation=CircularAnimation()
        fun bind(context: Context,item:MainData,clickListener: OnRecyclerItemClickListener,formatTextListener: OnRecyclerItemFormatTextListener){
            if(!item.station.isPending && binding.getDeviceDetails.visibility== View.INVISIBLE){
                circularAnimation.circularRevealAnimation(binding.getDeviceDetails)
                circularAnimation.circularRevealAnimation(binding.getDeviceQuantity)
                circularAnimation.circularRevealAnimation(binding.delDevice)
            }

            binding.deviceName.text=item.station.name

            binding.deviceQuantity.text=context.getString(R.string.quantity,formatTextListener.onFormatText(item.message!!.text))

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


    interface OnRecyclerItemFormatTextListener{
        fun onFormatText(text:String?):String
    }

    sealed class DataItem{
        abstract val id:Long?
        data class ControllerItem(val data :MainData):DataItem(){
            override val id=data.station.id
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