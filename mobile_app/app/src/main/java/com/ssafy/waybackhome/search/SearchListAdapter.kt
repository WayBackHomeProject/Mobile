package com.ssafy.waybackhome.search

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.ssafy.waybackhome.data.geo.GeoAddress
import com.ssafy.waybackhome.databinding.ListItemGeoAddressBinding

class SearchListAdapter : ListAdapter<GeoAddress, SearchListAdapter.SearchViewHolder>(GeoAddressComparator) {
    class SearchViewHolder(val binding : ListItemGeoAddressBinding) : ViewHolder(binding.root){
        fun bind(item : GeoAddress){

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SearchViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return SearchViewHolder(ListItemGeoAddressBinding.inflate(inflater, parent, false));
    }

    override fun onBindViewHolder(holder: SearchViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}

object GeoAddressComparator : DiffUtil.ItemCallback<GeoAddress>(){
    override fun areItemsTheSame(oldItem: GeoAddress, newItem: GeoAddress): Boolean {
        return oldItem.jibunAddress == newItem.jibunAddress
    }

    override fun areContentsTheSame(oldItem: GeoAddress, newItem: GeoAddress): Boolean {
        return oldItem == newItem
    }

}