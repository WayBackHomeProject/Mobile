package com.ssafy.waybackhome

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.naver.maps.geometry.LatLng

class LocationViewModel : ViewModel() {

    private val _currentLocation = MutableLiveData<LatLng>()
    val currentLocation : LiveData<LatLng> get() = _currentLocation

    fun setLocation(location : LatLng){
        _currentLocation.value = location
    }
    fun setLocation(lat : Double, lon : Double){
        _currentLocation.value = LatLng(lat, lon)
    }
}