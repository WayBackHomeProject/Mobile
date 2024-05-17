package com.ssafy.waybackhome.main

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.ssafy.waybackhome.data.Destination
import com.ssafy.waybackhome.databinding.ListItemDestinationBinding

class DestinationListAdapter : ListAdapter<Destination, DestinationListAdapter.DestinationViewHolder>(DestinationComparator) {

    inner class DestinationViewHolder(val binding: ListItemDestinationBinding) : ViewHolder(binding.root){
        fun bind(item : Destination){
            binding.tvName.text = item.name
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DestinationViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return DestinationViewHolder(ListItemDestinationBinding.inflate(inflater, parent, false))
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