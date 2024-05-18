package com.ssafy.waybackhome.destination

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ssafy.waybackhome.data.Destination
import com.ssafy.waybackhome.data.LocalRepository
import com.ssafy.waybackhome.data.geo.GeoAddress
import kotlinx.coroutines.launch

class DestinationViewModel : ViewModel() {

    private var _destination = MutableLiveData<Destination>()
    val destination : LiveData<Destination> get() = _destination
    fun setDestination(destination: Destination){
        _destination.value = destination
    }

    private val repo = LocalRepository.instance

    fun insertDestination(destination: Destination){
        viewModelScope.launch {
            repo.insertDestination(destination)
        }
    }
    fun updateDestination(destination: Destination){
        viewModelScope.launch {
            repo.updateDestination(destination)
        }
    }
}