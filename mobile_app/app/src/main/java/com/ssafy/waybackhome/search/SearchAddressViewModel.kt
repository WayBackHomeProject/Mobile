package com.ssafy.waybackhome.search

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.naver.maps.geometry.LatLng
import com.ssafy.waybackhome.data.Destination
import com.ssafy.waybackhome.data.geo.GeoAddress
import com.ssafy.waybackhome.data.geo.GeoAddressService
import kotlinx.coroutines.launch

class SearchAddressViewModel : ViewModel() {

    private val repo = GeoAddressService.api

    private var _addressList = MutableLiveData<List<GeoAddress>>()
    val addressList : LiveData<List<GeoAddress>> get() = _addressList
    fun search(keyword : String, coord : LatLng){
        viewModelScope.launch {
            val response = repo.getAddress(keyword, "${coord.longitude},${coord.latitude}")
            if(response.status == "OK"){
                _addressList.value = response.addresses
            }
        }
    }
}