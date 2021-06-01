package com.example.smscontroller

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.smscontroller.databaseModel.Message
import com.example.smscontroller.databinding.ControllerItemBinding
import com.example.smscontroller.databinding.MessageItemBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.lang.ClassCastException

private val ITEM_VIEW_TYPE = 2

class MessageRecyclerAdopter(context: Context) :ListAdapter<MessageRecyclerAdopter.DataItem,RecyclerView.ViewHolder>(MessageRecyclerAdopter.AdopterDataDiffCallback()) {

    private val adapterScope = CoroutineScope(Dispatchers.Default)
    private val mContext=context

    fun addViewSubmitList(list:List<Message>?){
        adapterScope.launch {
            val item=when(list){
                null-> listOf(null)
                else-> list.map { DataItem.MessageControllerItem(it) }
            }
            withContext(Dispatchers.Main){
                submitList(item)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when(viewType){
            ITEM_VIEW_TYPE->MessageViewHolder.from(parent)
            else-> throw ClassCastException("UnKnown viewType $viewType")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when(holder){
            is MessageViewHolder->{
                val item=getItem(position) as DataItem.MessageControllerItem
                holder.bind(mContext,item.data)
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when(getItem(position)){
            is DataItem-> ITEM_VIEW_TYPE
        }
    }


    class MessageViewHolder(val binding:MessageItemBinding):RecyclerView.ViewHolder(binding.root){
        fun bind(context: Context,item:Message){
            binding.messageDate.text=context.getString(R.string.date,item.createdTime.toString())
            binding.messageText.text=context.getString(R.string.message_text,"\n"+item.text)
        }

        companion object{
            fun from(parent:ViewGroup):MessageViewHolder{
                val layoutInflater= LayoutInflater.from(parent.context)
                val binding= MessageItemBinding.inflate(layoutInflater,parent,false)
                return MessageViewHolder(binding)
            }
        }

    }

    sealed class DataItem{
        abstract val id:Long?
        data class MessageControllerItem(val data:Message):DataItem(){
            override val id: Long=data.id!!
        }
    }

    class AdopterDataDiffCallback: DiffUtil.ItemCallback<MessageRecyclerAdopter.DataItem>(){
        override fun areItemsTheSame(oldItem: MessageRecyclerAdopter.DataItem, newItem: MessageRecyclerAdopter.DataItem): Boolean {
            return oldItem.id==newItem.id
        }
        override fun areContentsTheSame(oldItem: MessageRecyclerAdopter.DataItem, newItem: MessageRecyclerAdopter.DataItem): Boolean {
            return oldItem==newItem
        }
    }
}