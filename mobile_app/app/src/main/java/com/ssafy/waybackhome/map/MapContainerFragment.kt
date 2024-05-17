import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Bundle
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat

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
import com.ssafy.waybackhome.R
import com.ssafy.waybackhome.databinding.FragmentMapContainerBinding

class MapContainerFragment : Fragment(), OnMapReadyCallback {
    lateinit var binding: FragmentMapContainerBinding
    private lateinit var locationSource: FusedLocationSource
    private lateinit var naverMap: NaverMap
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var mapFragment: MapFragment

    private val LOCATION_PERMISSION_REQUEST_CODE = 1000
    private val PERMISSIONS = arrayOf(
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_COARSE_LOCATION
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        NaverMapSdk.getInstance(requireContext()).client =
            NaverMapSdk.NaverCloudPlatformClient("bl9m1myxy6")
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMapContainerBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        requestPermissions()
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

        naverMap.locationSource = locationSource
        naverMap.locationOverlay.isVisible = true

        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return
        }

        enableLocationTracking()

        addMarker()
        addCircle()
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

    fun onLocationPermissionGranted() {
        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED ||
            ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(context, "권한 승인됨. onLocationPermissionGranted", Toast.LENGTH_SHORT).show()
            enableLocationTracking()
        } else {
            Toast.makeText(context, "ACCESS_FINE_LOCATION 또는 ACCESS_COARSE_LOCATION 권한 없습니다.", Toast.LENGTH_SHORT).show()
        }
    }

    @SuppressLint("MissingPermission")
    private fun enableLocationTracking() {
        //fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext())
        naverMap.locationTrackingMode = LocationTrackingMode.Follow
        fusedLocationClient.lastLocation
            .addOnSuccessListener { location ->
                if (location != null) {
                    val currentLocation = LatLng(location.latitude, location.longitude)
                    naverMap.locationOverlay.position = currentLocation
                    naverMap.moveCamera(CameraUpdate.toCameraPosition(CameraPosition(currentLocation, 16.0)))

                    Toast.makeText(context,
                        "현재 위치 - 위도: ${currentLocation.latitude}, 경도: ${currentLocation.longitude}",
                        Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(context, "위치를 가져올 수 없습니다.", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener { e ->
                Toast.makeText(context, "위치를 가져오는 데 실패했습니다: $e", Toast.LENGTH_SHORT).show()
            }
    }

    private fun requestPermissions() {
        val permissionsToRequest = PERMISSIONS.filter {
            ContextCompat.checkSelfPermission(requireContext(), it) != PackageManager.PERMISSION_GRANTED
        }

        if (permissionsToRequest.isNotEmpty()) {
            requestPermissions(permissionsToRequest.toTypedArray(), LOCATION_PERMISSION_REQUEST_CODE)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.isNotEmpty() && grantResults.all { it == PackageManager.PERMISSION_GRANTED }) {
                Toast.makeText(requireContext(), "권한 허용됨 in onRequestPermissionsResult.", Toast.LENGTH_SHORT).show()
                onLocationPermissionGranted()
            } else {
                Toast.makeText(requireContext(), "위치 권한이 필요합니다.", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
