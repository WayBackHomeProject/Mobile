package com.ssafy.waybackhome

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.naver.maps.geometry.LatLng

class LocationViewModel : ViewModel() {

    private val _currentLocation = MutableLiveData<LatLng>()

    /**
     * 현재 위치
     */
    val currentLocation : LiveData<LatLng> get() = _currentLocation

    private val _coarseLocation = MutableLiveData<LatLng>()

    /**
     * 대략적인 위치 (50m 마다 위치 갱신)
     */
    val coarseLocation : LiveData<LatLng> get() = _coarseLocation

    /**
     * 현재 위치 변경.
     * 대략적인 위치는 이전 위치로부터 50m 이상 차이가 날 때만 갱신
     */
    fun setLocation(location : LatLng){
        _currentLocation.value = location

        if(!_coarseLocation.isInitialized || _coarseLocation.value!!.distanceTo(location) > 50){
            _coarseLocation.value = location
        }
    }
}