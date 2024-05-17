import android.Manifest
import android.annotation.SuppressLint
import android.graphics.Color
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.fragment.app.activityViewModels

import com.naver.maps.geometry.LatLng
import com.naver.maps.map.CameraPosition
import com.naver.maps.map.CameraUpdate
import com.naver.maps.map.MapFragment
import com.naver.maps.map.NaverMap
import com.naver.maps.map.NaverMapOptions
import com.naver.maps.map.NaverMapSdk
import com.naver.maps.map.OnMapReadyCallback
import com.naver.maps.map.LocationTrackingMode
import com.naver.maps.map.util.FusedLocationSource
import com.naver.maps.map.overlay.Marker
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.naver.maps.map.overlay.CircleOverlay
import com.ssafy.waybackhome.BuildConfig
import com.ssafy.waybackhome.LocationViewModel
import com.ssafy.waybackhome.R
import com.ssafy.waybackhome.databinding.FragmentMapContainerBinding
import com.ssafy.waybackhome.permission.PermissionChecker
import com.ssafy.waybackhome.util.BaseFragment

private const val TAG = "MapContainerFragment_싸피"
class MapContainerFragment : BaseFragment<FragmentMapContainerBinding>(FragmentMapContainerBinding::inflate), OnMapReadyCallback {

    private val locationViewModel by activityViewModels<LocationViewModel>()

    private lateinit var locationSource: FusedLocationSource
    private lateinit var naverMap: NaverMap
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var mapFragment: MapFragment

    private val permissionChecker = PermissionChecker(this)
    private val LOCATION_PERMISSION_REQUEST_CODE = 1000
    private val PERMISSIONS = arrayOf(
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_COARSE_LOCATION
    )
    fun changeBottomPadding(bottom : Int){
        naverMap.setContentPadding(0, 0, 0, bottom, true)
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
    private fun onLocationChange(location : Location){
        val currentLocation = LatLng(location.latitude, location.longitude)
        naverMap.locationOverlay.position = currentLocation
        naverMap.moveCamera(CameraUpdate.toCameraPosition(CameraPosition(currentLocation, 16.0)))
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
                    onLocationChange(location)
                } else {
                    Toast.makeText(context, "위치를 가져올 수 없습니다.", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener { e ->
                Toast.makeText(context, "위치를 가져오는 데 실패했습니다: $e", Toast.LENGTH_SHORT).show()
            }
    }
    private fun showPermissionRequestDialog(){
        permissionChecker.moveToSettings()
        //Toast.makeText(requireContext(), "rejected", Toast.LENGTH_SHORT).show()
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

        mapFragment.getMapAsync(this)
        locationSource = FusedLocationSource(this, LOCATION_PERMISSION_REQUEST_CODE)

        childFragmentManager.beginTransaction()
            .replace(R.id.mapFrame, mapFragment)
            .commitNow()
    }
    override fun onMapReady(naverMap: NaverMap) {
        this.naverMap = naverMap

        val padding = arguments?.getInt(ARG_PADDING)
        Log.d(TAG, "onMapReady: $padding")
        padding?.apply {
            naverMap.setContentPadding(0,0,0, this, true)
        }

        naverMap.locationSource = locationSource
        naverMap.locationOverlay.isVisible = true

        if(permissionChecker.checkPermission(requireContext(), PERMISSIONS)){
            enableLocationTracking()
        }
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
        initMap()
    }

    companion object{
        fun newInstance(bottomPadding : Int) : MapContainerFragment{
            Log.d(TAG, "newInstance: $bottomPadding")
            return MapContainerFragment().apply {
                arguments = Bundle().apply{
                    putInt(ARG_PADDING, bottomPadding)
                }
            }
        }
    }
}

const val ARG_PADDING = "ARG_PADDING"