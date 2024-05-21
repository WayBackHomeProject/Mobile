package com.ssafy.waybackhome.main

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.naver.maps.geometry.LatLng
import com.ssafy.waybackhome.data.Destination
import com.ssafy.waybackhome.databinding.ListItemDestinationBinding
import com.ssafy.waybackhome.util.OnItemClickListener
import com.ssafy.waybackhome.util.OnItemOptionsClickListener
import com.ssafy.waybackhome.util.formatMeter

class DestinationListAdapter(private val context : Context, private val location : LiveData<LatLng>) : ListAdapter<Destination, DestinationListAdapter.DestinationViewHolder>(DestinationComparator) {

    private var onItemClickListener : OnItemClickListener<Destination>? = null
    private var onItemOptionsClickListener : OnItemOptionsClickListener<Destination>? = null
    fun setOnItemClickListener(onItemClickListener: OnItemClickListener<Destination>){
        this.onItemClickListener = onItemClickListener
    }
    fun setOnItemOptionsClickListener(onItemOptionsClickListener: OnItemOptionsClickListener<Destination>){
        this.onItemOptionsClickListener = onItemOptionsClickListener
    }
    inner class DestinationViewHolder(private val binding: ListItemDestinationBinding) : ViewHolder(binding.root){

        private var item: Destination? = null
        fun bind(item : Destination){
            this.item = item
            binding.tvListItemDestinationName.text = item.name
            binding.tvListItemDestinationRoad.text = item.address
            binding.root.setOnClickListener {
                onItemClickListener?.onClick(item)
            }
            binding.btnListItemMore.setOnClickListener {
                onItemOptionsClickListener?.onClick(item, it)
            }
        }
        fun updateDistance(location : LatLng){
            item?.run {
                val dist = location.distanceTo(LatLng(lat, lng))
                binding.tvListItemDestinationDist.text = dist.formatMeter()
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DestinationViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val viewHolder = DestinationViewHolder(ListItemDestinationBinding.inflate(inflater, parent, false))
        location.observe(viewHolder.itemView.context as LifecycleOwner){dist ->
            viewHolder.updateDistance(dist)
        }
        return viewHolder
    }

    override fun onBindViewHolder(holder: DestinationViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}

object DestinationComparator : DiffUtil.ItemCallback<Destination>(){
    override fun areItemsTheSame(oldItem: Destination, newItem: Destination): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: Destination, newItem: Destination): Boolean {
        return oldItem == newItem
    }

}