package com.ssafy.waybackhome.main

import android.Manifest
import android.annotation.SuppressLint
import android.graphics.Color
import android.location.Location
import android.os.Bundle
import android.view.View
import android.widget.Toast
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
import com.naver.maps.map.util.FusedLocationSource
import com.ssafy.waybackhome.BuildConfig
import com.ssafy.waybackhome.LocationViewModel
import com.ssafy.waybackhome.R
import com.ssafy.waybackhome.data.Destination
import com.ssafy.waybackhome.databinding.FragmentMainBinding
import com.ssafy.waybackhome.destination.DestinationViewModel
import com.ssafy.waybackhome.permission.PermissionChecker
import com.ssafy.waybackhome.util.BaseFragment

private const val TAG = "MainFragment"
class MainFragment : BaseFragment<FragmentMainBinding>(FragmentMainBinding::inflate),
    OnMapReadyCallback {

    // ViewModels
    private val viewModel: MainViewModel by viewModels()
    private val locationViewModel : LocationViewModel by activityViewModels()
    private val destinationViewModel : DestinationViewModel by navGraphViewModels(R.id.nav_graph)

    // View Components
    private lateinit var destinationAdapter : DestinationListAdapter
    private lateinit var bottomSheetBehavior: BottomSheetBehavior<View>

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
    private fun deleteDestination(destination: Destination){
        destinationViewModel.deleteDestination(destination)
    }

    /**
     * BottomSheet 사이즈에 따른 Map 사이즈 조절
     */
    private fun adjustMapSize(){
        val totalHeight = binding.root.height
        val bottomSheetTop = binding.mainBottomSheet.top
        naverMap.setContentPadding(0, 100, 0, totalHeight-bottomSheetTop, true)
    }
    private fun addCircle() {
        val circle = CircleOverlay()
        circle.center = LatLng(36.104704, 128.419193)
        circle.radius = 50.0
        circle.color = Color.argb(77, 0, 191, 255)
        circle.map = naverMap
    }
    private fun addMarker() {
        val markerPosition = LatLng(36.104704, 128.419193)
        val marker = Marker()
        marker.position = markerPosition
        marker.width = 30
        marker.height = 30
        marker.map = naverMap
    }
    private fun loadMarkerData(location: LatLng){
        viewModel.getCctvData(location, 0.5)
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
            editDestination(dest)
        }
        destinationAdapter.setOnItemOptionsClickListener{dest->
            deleteDestination(dest)
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
    }
    // 맵 초기화 이후에 활성화되는 관찰자
    // naverMap 객체에 의존적
    private fun initPostMapReadyObserver(){
        viewModel.cctvs.observe(viewLifecycleOwner){cctvList ->
            viewModel.cctvMarkers.clear()
            cctvList.forEach {cctv->
                val markerPosition = LatLng(cctv.latitude, cctv.longitude)
                val marker = Marker()
                marker.position = markerPosition
                marker.width = 30
                marker.height = 30
                marker.map = if(binding.chipCctv.isChecked) naverMap else null
                viewModel.cctvMarkers.add(marker)
            }
        }
        locationViewModel.currentLocation.observe(viewLifecycleOwner){location ->
            // 카메라 현재 위치로 이동
            naverMap.moveCamera(CameraUpdate.toCameraPosition(CameraPosition(location, 16.0)))
        }
    }
    // 맵 초기화 이후에 활성화되는 리스너
    private fun initPostMapReadyListener(){
        binding.chipCctv.setOnCheckedChangeListener { buttonView, isChecked ->
            viewModel.setCctvMarkerVisibility(if(isChecked) naverMap else null)
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

        if(permissionChecker.checkPermission(requireContext(), PERMISSIONS)){
            enableLocationTracking()
        }

        initPostMapReadyListener()
        initPostMapReadyObserver()

        addMarker()
        addCircle()
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