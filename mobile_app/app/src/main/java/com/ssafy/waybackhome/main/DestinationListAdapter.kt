package com.ssafy.waybackhome.main

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

class DestinationListAdapter(private val location : LiveData<LatLng>) : ListAdapter<Destination, DestinationListAdapter.DestinationViewHolder>(DestinationComparator) {

    private var onItemClickListener : OnItemClickListener<Destination>? = null
    fun setOnItemClickListener(onItemClickListener: OnItemClickListener<Destination>){
        this.onItemClickListener = onItemClickListener
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
        }
        fun updateDistance(location : LatLng){
            item?.run {
                val dist = location.distanceTo(LatLng(lat, lng))
                binding.tvListItemDestinationDist.text = if(dist > 1_000) "%.1f km".format(dist/1_000) else "%.1f m".format(dist)
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