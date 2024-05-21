package com.ssafy.waybackhome.main

import android.graphics.Color
import android.location.Address
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.NaverMap
import com.naver.maps.map.overlay.CircleOverlay
import com.naver.maps.map.overlay.Marker
import com.naver.maps.map.overlay.OverlayImage
import com.ssafy.waybackhome.R
import com.ssafy.waybackhome.data.LocalRepository
import com.ssafy.waybackhome.data.alarmbell.AlarmBellData
import com.ssafy.waybackhome.data.alarmbell.AlarmBellService
import com.ssafy.waybackhome.data.cctv.CctvData
import com.ssafy.waybackhome.data.cctv.CctvService
import com.ssafy.waybackhome.data.police.PoliceStationData
import com.ssafy.waybackhome.data.police.PoliceStationService
import com.ssafy.waybackhome.data.store.StoreData
import com.ssafy.waybackhome.data.store.StoreService
import kotlinx.coroutines.launch

class MainViewModel : ViewModel() {

    private val repo = LocalRepository.instance

    /**
     * 로컬 DB에 저장된 목적지 정보
     */
    val destinations = repo.selectAllDestination()

    var bottomSheetState : Int = BottomSheetBehavior.STATE_COLLAPSED

    private var _addressMarker = Marker()
    fun setAddressMarker(marker: Marker){
        _addressMarker = marker
    }
    fun clearAddressMarker(){
        _addressMarker.map = null
    }

    private var _destinationMarkers = mutableListOf<Marker>()
    fun addToDestinationMarkers(marker: Marker){
        _destinationMarkers.add(marker)
    }
    fun clearDestinationMarkers(){
        clearMarkers(_destinationMarkers)
    }
    private fun clearMarkers(markers : MutableList<Marker>){
        for(marker : Marker in markers){
            marker.map = null
        }
        markers.clear()
    }
    private fun clearCircles(circles : MutableList<CircleOverlay>){
        for(circle : CircleOverlay in circles){
            circle.map = null
        }
        circles.clear()
    }


    fun getAllData(location: LatLng, radius: Double){
        getCctvData(location, radius)
        getPoliceStationData(location, radius)
        getStoreData(location, radius)
        getAlarmBellData(location, radius)
    }
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

    private var _cctvMarkers = mutableListOf<Marker>()
    private var _cctvCircles = mutableListOf<CircleOverlay>()
    /**
     * @param naverMap naverMap : Visible / null : Invisible
     */
    fun setCctvMarkerVisibility(naverMap: NaverMap?){
        _cctvMarkers.forEach { marker ->
            marker.map = naverMap
        }
    }
    fun setCctvCirclesVisibility(naverMap: NaverMap?){
        _cctvCircles.forEach {marker ->
            marker.map = naverMap
        }
    }
    fun addToCctvMarkers(marker: Marker) = _cctvMarkers.add(marker)
    fun addToCctvCircles(circle: CircleOverlay) = _cctvCircles.add(circle)
    fun clearCctvMarkers() :Unit {
        clearMarkers(_cctvMarkers)
        clearCircles(_cctvCircles)
    }


    private var _stores = MutableLiveData<List<StoreData>>()
    val stores : LiveData<List<StoreData>> get() = _stores
    /**
     * @param location 검색 중심 좌표
     * @param radius 검색 반경 (단위:km)
     */
    fun getStoreData(location : LatLng, radius : Double){
        viewModelScope.launch {
            try{
                val list = StoreService.api.getStores(location.latitude, location.longitude, radius)
                _stores.value = list
            } catch(e : Exception) {
                _stores.value = listOf()
            }
        }
    }
    private var _storeMarkers = mutableListOf<Marker>()
    /**
     * @param naverMap naverMap : Visible / null : Invisible
     */
    fun setStoreMarkerVisibility(naverMap: NaverMap?){
        _storeMarkers.forEach { marker ->
            marker.map = naverMap
        }
    }
    fun addToStoreMarkers(marker: Marker) = _storeMarkers.add(marker)
    fun clearStoreMarkers() = clearMarkers(_storeMarkers)

    private var _policeStations = MutableLiveData<List<PoliceStationData>>()
    val policeStations : LiveData<List<PoliceStationData>> get() = _policeStations
    /**
     * @param location 검색 중심 좌표
     * @param radius 검색 반경 (단위:km)
     */
    fun getPoliceStationData(location : LatLng, radius : Double){
        viewModelScope.launch {
            try{
                val list = PoliceStationService.api.getPoliceStations(location.latitude, location.longitude, radius)
                _policeStations.value = list
            } catch(e : Exception) {
                _policeStations.value = listOf()
            }
        }
    }
    private var _policeStationMarkers = mutableListOf<Marker>()
    /**
     * @param naverMap naverMap : Visible / null : Invisible
     */
    fun setPoliceStationMarkerVisibility(naverMap: NaverMap?){
        _policeStationMarkers.forEach { marker ->
            marker.map = naverMap
        }
    }
    fun addToPoliceStationMarkers(marker: Marker) = _policeStationMarkers.add(marker)
    fun clearPoliceStationMarkers():Unit {
        clearMarkers(_policeStationMarkers)
    }

    private var _alarmBells = MutableLiveData<List<AlarmBellData>>()
    val alarmBells : LiveData<List<AlarmBellData>> get() = _alarmBells
    /**
     * @param location 검색 중심 좌표
     * @param radius 검색 반경 (단위:km)
     */
    fun getAlarmBellData(location : LatLng, radius : Double){
        viewModelScope.launch {
            try{
                val list = AlarmBellService.api.getAlarmBells(location.latitude, location.longitude, radius)
                _alarmBells.value = list
            } catch(e : Exception) {
                _alarmBells.value = listOf()
            }
        }
    }
    private var _alarmBellMarkers = mutableListOf<Marker>()
    /**
     * @param naverMap naverMap : Visible / null : Invisible
     */
    fun setAlarmBellMarkerVisibility(naverMap: NaverMap?){
        _alarmBellMarkers.forEach { marker ->
            marker.map = naverMap
        }
    }
    fun addToAlarmBellMarkers(marker: Marker) = _alarmBellMarkers.add(marker)
    fun clearAlarmBellMarkers() = clearMarkers(_alarmBellMarkers)
}