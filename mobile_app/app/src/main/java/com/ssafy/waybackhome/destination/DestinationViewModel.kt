package com.ssafy.waybackhome.destination

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.ssafy.waybackhome.data.geo.GeoAddress

class DestinationViewModel : ViewModel() {

    private var _address = MutableLiveData<GeoAddress>()
    val address : LiveData<GeoAddress> get() = _address

    fun setDestinationAddress(address: GeoAddress){
        _address.value = address
    }
}