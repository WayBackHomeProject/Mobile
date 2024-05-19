package com.ssafy.waybackhome.destination

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ssafy.waybackhome.data.Destination
import com.ssafy.waybackhome.data.LocalRepository
import com.ssafy.waybackhome.data.geo.GeoAddress
import kotlinx.coroutines.launch

private const val TAG = "DestinationViewModel"
class DestinationViewModel : ViewModel() {

    private var _destination = MutableLiveData<Destination>()

    /**
     * [DestinationFragment]에서 사용되는 목적지 정보
     * Navigate할 때 초기값으로 입력
     */
    val destination : LiveData<Destination> get() = _destination
    fun changeAddress(address: GeoAddress){
        _destination.value = _destination.value?.copy(
            address = address.jibunAddress,
            road = address.roadAddress,
            lat = address.lat.toDouble(),
            lng = address.lon.toDouble()
        )
    }
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

    override fun onCleared() {
        super.onCleared()
        Log.d(TAG, "onCleared: ")
    }
}