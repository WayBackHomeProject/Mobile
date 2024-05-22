package com.ssafy.waybackhome.main

import android.Manifest
import android.annotation.SuppressLint
import android.graphics.Color
import android.graphics.PointF
import android.location.Geocoder
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.PopupMenu
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.addCallback
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.navGraphViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetBehavior.BottomSheetCallback
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.CameraAnimation
import com.naver.maps.map.CameraPosition
import com.naver.maps.map.CameraUpdate
import com.naver.maps.map.LocationTrackingMode
import com.naver.maps.map.MapFragment
import com.naver.maps.map.NaverMap
import com.naver.maps.map.NaverMapOptions
import com.naver.maps.map.NaverMapSdk
import com.naver.maps.map.OnMapReadyCallback
import com.naver.maps.map.overlay.CircleOverlay
import com.naver.maps.map.overlay.Marker
import com.naver.maps.map.overlay.OverlayImage
import com.naver.maps.map.util.FusedLocationSource
import com.ssafy.waybackhome.BuildConfig
import com.ssafy.waybackhome.LocationViewModel
import com.ssafy.waybackhome.R
import com.ssafy.waybackhome.data.Destination
import com.ssafy.waybackhome.data.alarmbell.AlarmBellData
import com.ssafy.waybackhome.data.cctv.CctvData
import com.ssafy.waybackhome.data.police.PoliceStationData
import com.ssafy.waybackhome.data.store.StoreData
import com.ssafy.waybackhome.databinding.FragmentMainBinding
import com.ssafy.waybackhome.destination.DestinationViewModel
import com.ssafy.waybackhome.dialog.ProfileDialogFragment
import com.ssafy.waybackhome.permission.PermissionChecker
import com.ssafy.waybackhome.util.BaseFragment
import com.ssafy.waybackhome.util.formatMeter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.Date
import java.util.Locale

private const val TAG = "MainFragment_싸피"
class MainFragment : BaseFragment<FragmentMainBinding>(FragmentMainBinding::inflate),
    OnMapReadyCallback {

    // ViewModels
    private val viewModel : MainViewModel by viewModels()
    private val locationViewModel : LocationViewModel by activityViewModels()
    private val destinationViewModel : DestinationViewModel by navGraphViewModels(R.id.nav_graph)

    // View Components
    private lateinit var destinationAdapter : DestinationListAdapter
    private lateinit var bottomSheetBehavior: BottomSheetBehavior<View>
    private lateinit var addressBottomSheetBehavior : BottomSheetBehavior<View>
    // Map
    private lateinit var mapFragment: MapFragment
    private lateinit var locationSource: FusedLocationSource
    private lateinit var naverMap: NaverMap
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    // Permissions
    private val permissionChecker = PermissionChecker(this)
    private val LOCATION_PERMISSION_REQUEST_CODE = 1000
    private val PERMISSIONS = arrayOf(
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_COARSE_LOCATION
    )

    private lateinit var onBackCallback : OnBackPressedCallback
    private var prevEventTime : Date? = null

    private fun showPermissionRequestDialog(){
        permissionChecker.moveToSettings()
        //Toast.makeText(requireContext(), "rejected", Toast.LENGTH_SHORT).show()
    }
    private fun openSearch(){
        val action = MainFragmentDirections.actionMainFragmentToSearchAddressFragment(null)
        findNavController().navigate(action)
    }
    private fun editDestination(destination: Destination){
        destinationViewModel.setDestination(destination)
        val action = MainFragmentDirections.actionMainFragmentToDestinationFragment()
        findNavController().navigate(action)
    }
    private fun makeNewDestination(destination: Destination){
        destinationViewModel.setDestination(destination)
        val action = MainFragmentDirections.actionMainFragmentToDestinationFragment()
        findNavController().navigate(action)
    }
    private fun deleteDestination(destination: Destination){
        destinationViewModel.deleteDestination(destination)
    }
    private fun moveCameraTo(location: LatLng){
        val cameraMove = CameraUpdate.scrollTo(location).animate(
            if(locationViewModel.distanceTo(location) > 1_000) CameraAnimation.Fly else CameraAnimation.Easing
        )
        naverMap.moveCamera(cameraMove)
    }
    private fun addBackButtonEvent(){
        requireActivity().onBackPressedDispatcher.addCallback(this){
            if(bottomSheetBehavior.state == BottomSheetBehavior.STATE_EXPANDED){
                bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
            } else {
                val current = Date()
                if(prevEventTime == null || (prevEventTime!!.time - current.time)/1000 > 60){
                    prevEventTime = current
                    Toast.makeText(requireContext(), "'뒤로가기' 버튼을 한번 더 누르면 종료됩니다.", Toast.LENGTH_SHORT).show()
                } else {
                    prevEventTime = null
                    requireActivity().finish()
                }
            }
        }
        onBackCallback = requireActivity().onBackPressedDispatcher.addCallback(this) {
            if(addressBottomSheetBehavior.state != BottomSheetBehavior.STATE_HIDDEN){
                hideAddressBottomSheet()
            }
        }
        onBackCallback.isEnabled = false
    }
    private fun selectLocation(location: LatLng){
        CoroutineScope(Dispatchers.Main).launch {
            val address = Geocoder(requireContext(), Locale.KOREA).getFromLocation(location.latitude, location.longitude,1)?.first()
            Log.d(TAG, "markSelectedLocation: $address")
            if(address != null && address.maxAddressLineIndex >= 0){
                val destination = Destination(
                    address = address.getAddressLine(0),
                    road = address.getAddressLine(0),
                    lat = address.latitude,
                    lng = address.longitude
                )
                markSelectedDestination(destination)
            }
        }
    }
    private fun markSelectedDestination(destination: Destination){
        viewModel.clearAddressMarker()
        val location = LatLng(destination.lat, destination.lng)
        val marker = Marker().apply {
            position = location
            width = 100
            height = 100
            anchor = PointF(0f, 1f)
            icon = OverlayImage.fromResource(R.drawable.flag)
            captionText = destination.address
            map = naverMap
        }
        viewModel.setAddressMarker(marker)
        moveCameraTo(location)
        viewModel.selectDestination(destination)
        showAddressBottomSheet(destination)
    }
    /**
     * 맵에 롱클릭 시
     */
    private fun showAddressBottomSheet(destination: Destination){
        binding.mainBottomSheet.visibility = View.GONE
        binding.mainFragFab.visibility = View.GONE

        val hasName = destination.name.isNotBlank()

        binding.tvDestinationName.visibility = if(hasName) View.VISIBLE else View.GONE
        binding.tvDestinationName.text = destination.name
        binding.tvAddress.text = destination.address
        val dist = locationViewModel.distanceTo(LatLng(destination.lat, destination.lng))
        binding.tvAddressDistance.text = dist.formatMeter()

        binding.btnDelete.visibility = if(hasName) View.VISIBLE else View.GONE
        binding.btnEdit.visibility = if(hasName) View.VISIBLE else View.GONE
        binding.btnCreate.visibility = if(hasName) View.GONE else View.VISIBLE
        addressBottomSheetBehavior.state = BottomSheetBehavior.STATE_EXPANDED
        onBackCallback.isEnabled = true
    }
    /**
     * 화면 터치 시
     * back 버튼 눌렀을 시
     */
    private fun hideAddressBottomSheet(){
        viewModel.clearAddressMarker()
        addressBottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
        binding.mainBottomSheet.visibility = View.VISIBLE
        binding.mainFragFab.visibility = View.VISIBLE
        onBackCallback.isEnabled = false
    }
    /**
     * BottomSheet 사이즈에 따른 Map 사이즈 조절
     */
    private fun adjustMapSize(){
        val totalHeight = binding.root.height
        val bottomSheetTop = binding.mainBottomSheet.top
        naverMap.setContentPadding(0, 100, 0, totalHeight-bottomSheetTop, true)
    }
    private fun loadMarkerData(location: LatLng){
        viewModel.getAllData(location, 2.0)
    }
    private fun setCctvMarker(cctvList : List<CctvData>){
        viewModel.clearCctvMarkers()

        cctvList.forEach {cctv->
            val markerPosition = LatLng(cctv.latitude, cctv.longitude)
            val marker = Marker().apply {
                position = markerPosition
                width = 30
                height = 30
                icon = OverlayImage.fromResource(R.drawable.cctv)
                map = if(binding.chipCctv.isChecked) naverMap else null
            }


            val circle = CircleOverlay().apply {
                center = markerPosition
                radius = 40.0
                color = Color.argb(100,254, 254, 100)
                outlineWidth = 1 // 테두리의 두께 설정
                outlineColor = Color.BLACK // 테두리의 색상 설정
                map = if(binding.chipCctv.isChecked) naverMap else null
            }
            viewModel.addToCctvMarkers(marker)
            viewModel.addToCctvCircles(circle)
        }
    }
    private fun setPoliceStationMarker(stationList : List<PoliceStationData>){
        viewModel.clearPoliceStationMarkers()
        stationList.forEach { station->
            val markerPosition = LatLng(station.lat, station.lng)
            val marker = Marker().apply {
                position = markerPosition
                width = 100
                height = 100
                icon = OverlayImage.fromResource(R.drawable.police)
                map = if(binding.chipPolice.isChecked) naverMap else null
            }
            viewModel.addToPoliceStationMarkers(marker)
        }
    }
    private fun setAlarmBellMarker(alarmBellList : List<AlarmBellData>){
        viewModel.clearAlarmBellMarkers()
        alarmBellList.forEach { bell->
            val markerPosition = LatLng(bell.lat, bell.lng)
            val marker = Marker().apply {
                position = markerPosition
                width = 75
                height = 75
                icon = OverlayImage.fromResource(R.drawable.alert)
                map = if(binding.chipPolice.isChecked) naverMap else null
            }
            viewModel.addToAlarmBellMarkers(marker)
        }
    }
    private fun setStoreMarkers(storeList : List<StoreData>){
        viewModel.clearStoreMarkers()
        storeList.forEach { store->
            val markerPosition = LatLng(store.lat, store.lng)
            val marker = Marker().apply {
                position = markerPosition
                width = 75
                height = 75
                icon = OverlayImage.fromResource(R.drawable.conveniencestore)
                map = if(binding.chipPolice.isChecked) naverMap else null
            }
            viewModel.addToStoreMarkers(marker)
        }
    }
    private fun setDestinationMarkers(destinations: List<Destination>){
        viewModel.clearDestinationMarkers()
        destinations.forEach { destination->
            val markerPosition = LatLng(destination.lat, destination.lng)
            val marker = Marker().apply {
                position = markerPosition
                width = 100
                height = 100
                anchor = PointF(0f, 1f)
                icon = OverlayImage.fromResource(R.drawable.flag)
                map = naverMap
                setOnClickListener {
                    showAddressBottomSheet(destination)
                    moveCameraTo(destination.location)
                    false
                }
            }
            viewModel.addToDestinationMarkers(marker)
        }
    }
    /**
     * 위치 변경 시
     */
    private fun onLocationChange(location: LatLng){
        // 목적지 목록 거리순 갱신
        val sorted = viewModel.destinations.value?.sortedBy {
            val latLng = LatLng(it.lat, it.lng)
            val dist = latLng.distanceTo(location)
            dist
        }
        if(sorted != null) destinationAdapter.submitList(sorted)
    }

    /**
     * 대략적인 위치 갱신 시
     */
    private fun onCoarseLocationChange(location: LatLng){
        loadMarkerData(location)
    }
    private fun onSetCurrentLocation(location : Location){
        val currentLocation = LatLng(location.latitude, location.longitude)
        naverMap.locationOverlay.position = currentLocation

        // 현재 위치 설정
        locationViewModel.setLocation(currentLocation)

        Toast.makeText(context,
            "현재 위치 - 위도: ${currentLocation.latitude}, 경도: ${currentLocation.longitude}",
            Toast.LENGTH_SHORT).show()
    }
    @SuppressLint("MissingPermission")
    private fun enableLocationTracking() {
        //fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext())
        naverMap.locationTrackingMode = LocationTrackingMode.Follow
        fusedLocationClient.lastLocation
            .addOnSuccessListener { location ->
                if (location != null) {
                    onSetCurrentLocation(location)
                } else {
                    Toast.makeText(context, "위치를 가져올 수 없습니다.", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener { e ->
                Toast.makeText(context, "위치를 가져오는 데 실패했습니다: $e", Toast.LENGTH_SHORT).show()
            }
    }

    private fun initView(){
        destinationAdapter = DestinationListAdapter(requireContext(), locationViewModel.currentLocation)
        destinationAdapter.setOnItemClickListener{dest ->
            markSelectedDestination(dest)
        }
        destinationAdapter.setOnItemOptionsClickListener{dest, anchor->
            PopupMenu(context, anchor).apply {
                setOnMenuItemClickListener {
                    when(it.itemId){
                        R.id.menu_delete -> deleteDestination(dest)
                        R.id.menu_edit -> editDestination(dest)
                    }
                    true
                }
                inflate(R.menu.menu_delete)
                show()
            }
        }
        binding.rvDestinations.adapter = destinationAdapter
        binding.rvDestinations.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)

        bottomSheetBehavior = BottomSheetBehavior.from(binding.mainBottomSheet)
        bottomSheetBehavior.state = viewModel.bottomSheetState
        // BottomSheet 크기에 따라 맵 사이즈 조절
        bottomSheetBehavior.addBottomSheetCallback(object : BottomSheetCallback(){
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                viewModel.bottomSheetState = newState
                if(newState != BottomSheetBehavior.STATE_EXPANDED){
                    adjustMapSize()
                }
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {
                if (bottomSheetBehavior.state == BottomSheetBehavior.STATE_DRAGGING || bottomSheetBehavior.state == BottomSheetBehavior.STATE_SETTLING) {
                    adjustMapSize()
                }
            }
        })
        addressBottomSheetBehavior = BottomSheetBehavior.from(binding.addressBottomSheet)
        addressBottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
        addressBottomSheetBehavior.addBottomSheetCallback(object : BottomSheetCallback(){
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                if(newState == BottomSheetBehavior.STATE_HIDDEN) hideAddressBottomSheet()
            }
            override fun onSlide(bottomSheet: View, slideOffset: Float) {}
        })
    }
    private fun initObserver(){
        // 목적지 목록 갱신
        viewModel.destinations.observe(viewLifecycleOwner){ list ->
            val sorted = list.sortedBy {
                val latLng = LatLng(it.lat, it.lng)
                val dist = locationViewModel.currentLocation.value?.let { location -> latLng.distanceTo(location) }
                dist
            }
            destinationAdapter.submitList(sorted)
        }
        // 위치에 따른 목적지 목록 순서 갱신
        locationViewModel.currentLocation.observe(viewLifecycleOwner){location ->
            onLocationChange(location)
        }
        // 대략적인 위치 갱신
        locationViewModel.coarseLocation.observe(viewLifecycleOwner){ location ->
            onCoarseLocationChange(location)
        }
    }
    private fun initListener(){
        binding.btnAddDest.setOnClickListener{
            openSearch()
        }
        binding.mainFragFab.setOnClickListener{
//            ProfileDialog(requireContext()).show()
            ProfileDialogFragment().show(childFragmentManager, "ProfileDialogFragment")
        }
        binding.btnDelete.setOnClickListener {
            viewModel.selectedDestination?.let { deleteDestination(it) }
        }
        binding.btnEdit.setOnClickListener {
            viewModel.selectedDestination?.let { editDestination(it) }
        }
        binding.btnCreate.setOnClickListener {
            viewModel.selectedDestination?.let { makeNewDestination(it) }
        }
        addBackButtonEvent()
    }
    // 맵 초기화 이후에 활성화되는 관찰자
    // naverMap 객체에 의존적
    private fun initPostMapReadyObserver(){
        viewModel.cctvs.observe(viewLifecycleOwner){cctvList ->
            setCctvMarker(cctvList)
        }
        viewModel.policeStations.observe(viewLifecycleOwner){stations->
            setPoliceStationMarker(stations)
        }
        viewModel.alarmBells.observe(viewLifecycleOwner){ bells ->
            setAlarmBellMarker(bells)
        }
        viewModel.stores.observe(viewLifecycleOwner){ stores ->
            setStoreMarkers(stores)
        }
        locationViewModel.currentLocation.observe(viewLifecycleOwner){location ->
            // 카메라 현재 위치로 이동
            naverMap.moveCamera(CameraUpdate.toCameraPosition(CameraPosition(location, 16.0)))
        }
        viewModel.destinations.observe(viewLifecycleOwner){destinations ->
            setDestinationMarkers(destinations)
        }
    }
    // 맵 초기화 이후에 활성화되는 리스너
    private fun initPostMapReadyListener(){
        binding.chipCctv.setOnCheckedChangeListener { buttonView, isChecked ->
            viewModel.setCctvMarkerVisibility(if(isChecked) naverMap else null)
            viewModel.setCctvCirclesVisibility(if(isChecked) naverMap else null)
        }
        binding.chipLamp.setOnCheckedChangeListener { buttonView, isChecked ->
            viewModel.setAlarmBellMarkerVisibility(if(isChecked) naverMap else null)
        }
        binding.chipPolice.setOnCheckedChangeListener { buttonView, isChecked ->
            viewModel.setPoliceStationMarkerVisibility(if(isChecked) naverMap else null)
        }
        binding.chipStore.setOnCheckedChangeListener { buttonView, isChecked ->
            viewModel.setStoreMarkerVisibility(if(isChecked) naverMap else null)
        }
        naverMap.addOnCameraChangeListener { reason, animated ->
            //Log.d(TAG, "initPostMapReadyListener: ${naverMap.cameraPosition.zoom}")
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
            false
        }
        naverMap.setOnMapClickListener { pointF, latLng ->
            if(addressBottomSheetBehavior.state == BottomSheetBehavior.STATE_EXPANDED) hideAddressBottomSheet()
        }
        naverMap.setOnMapLongClickListener { pointF, latLng ->
            selectLocation(latLng)
        }
    }
    private fun initPermissionEventListener(){
        // 위치 권한 요청을 수락하였을 때
        permissionChecker.setOnGrantedListener{
            //Toast.makeText(requireContext(), "granted", Toast.LENGTH_SHORT).show()
            enableLocationTracking()
        }
        // 위치 권한 요청을 거부하였을 때
        permissionChecker.setOnRejectedListener{
            showPermissionRequestDialog()
        }
    }
    private fun initClient(){
        NaverMapSdk.getInstance(requireContext()).client =
            NaverMapSdk.NaverCloudPlatformClient(BuildConfig.NAVER_API_ID)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext())
    }
    private fun initMap(){
        mapFragment = MapFragment.newInstance(
            NaverMapOptions()
                .camera(CameraPosition(NaverMap.DEFAULT_CAMERA_POSITION.target, 16.0))
                .locationButtonEnabled(true)
        )

        // onMapReady로 Callback
        mapFragment.getMapAsync(this)
        locationSource = FusedLocationSource(this, LOCATION_PERMISSION_REQUEST_CODE)

        childFragmentManager.beginTransaction()
            .replace(R.id.map_container_frame, mapFragment)
            .commitNow()
    }
    override fun onMapReady(naverMap: NaverMap) {
        this.naverMap = naverMap

        adjustMapSize()

        naverMap.locationSource = locationSource
        naverMap.locationOverlay.isVisible = true
        naverMap.uiSettings.isLocationButtonEnabled = true
        if(permissionChecker.checkPermission(requireContext(), PERMISSIONS)){
            enableLocationTracking()
        }

        initPostMapReadyListener()
        initPostMapReadyObserver()
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initPermissionEventListener()
        initClient()
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        initListener()
        initObserver()
        initMap()
    }
}