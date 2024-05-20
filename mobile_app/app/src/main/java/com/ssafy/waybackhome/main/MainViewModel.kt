package com.ssafy.waybackhome.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.naver.maps.geometry.LatLng
import com.ssafy.waybackhome.data.LocalRepository
import com.ssafy.waybackhome.data.cctv.CctvData
import com.ssafy.waybackhome.data.cctv.CctvService
import kotlinx.coroutines.launch

class MainViewModel : ViewModel() {

    private val repo = LocalRepository.instance

    /**
     * 로컬 DB에 저장된 목적지 정보
     */
    val destinations = repo.selectAllDestination()

    var bottomSheetState : Int = BottomSheetBehavior.STATE_COLLAPSED

    private var _cctvs = MutableLiveData<List<CctvData>>()
    val cctvs : LiveData<List<CctvData>> get() = _cctvs
    fun getCctvDatas(location : LatLng, radius : Double){
        viewModelScope.launch {
            CctvService.api.getCctv(location.latitude, location.longitude, radius)
        }
    }
}