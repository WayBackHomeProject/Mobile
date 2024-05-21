package com.ssafy.waybackhome.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.NaverMap
import com.naver.maps.map.overlay.CircleOverlay
import com.naver.maps.map.overlay.Marker
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

    /**
     * @param location 검색 중심 좌표
     * @param radius 검색 반경 (단위:km)
     */
    fun getCctvData(location : LatLng, radius : Double){
        viewModelScope.launch {
            try{
                val list = CctvService.api.getCctv(location.latitude, location.longitude, radius)
                _cctvs.value = list
            } catch(e : Exception) {
                _cctvs.value = listOf()
            }
        }
    }
    var cctvMarkers = mutableListOf<Marker>()
    var cctvCircles = mutableListOf<CircleOverlay>()

    /**
     * @param naverMap naverMap : Visible / null : Invisible
     */
    fun setCctvMarkerVisibility(naverMap: NaverMap?){
        cctvMarkers.forEach {marker ->
            marker.map = naverMap
        }
    }
    fun setCctvCirclesVisibility(naverMap: NaverMap?){
        cctvCircles.forEach {marker ->
            marker.map = naverMap
        }
    }
}