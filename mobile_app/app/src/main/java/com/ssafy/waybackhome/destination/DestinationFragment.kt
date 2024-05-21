package com.ssafy.waybackhome.destination

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.navigation.navGraphViewModels
import com.naver.maps.geometry.LatLng
import com.naver.maps.map.CameraPosition
import com.naver.maps.map.MapFragment
import com.naver.maps.map.NaverMap
import com.naver.maps.map.NaverMapOptions
import com.naver.maps.map.NaverMapSdk
import com.naver.maps.map.OnMapReadyCallback
import com.naver.maps.map.overlay.Marker
import com.ssafy.waybackhome.BuildConfig
import com.ssafy.waybackhome.R
import com.ssafy.waybackhome.data.Destination
import com.ssafy.waybackhome.databinding.FragmentDestinationBinding
import com.ssafy.waybackhome.util.BaseFragment

private const val TAG = "DestinationFragment"
class DestinationFragment : BaseFragment<FragmentDestinationBinding>(FragmentDestinationBinding::inflate), OnMapReadyCallback {

    private val destinationViewModel : DestinationViewModel by navGraphViewModels(R.id.nav_graph)

    private lateinit var naverMap: NaverMap
    private lateinit var mapFragment: MapFragment

    private fun closePage(){
        val action = DestinationFragmentDirections.actionDestinationFragmentToMainFragment()
        findNavController().navigate(action)
    }
    private fun saveDestination(destination: Destination){
        destinationViewModel.insertDestination(
            destination.copy(
                name = binding.etDestinationName.text.toString()
            )
        )
        closePage()
    }
    private fun editDestination(destination: Destination){
        destinationViewModel.updateDestination(
            destination.copy(
                name = binding.etDestinationName.text.toString()
            )
        )
        closePage()
    }
    private fun setLocation(){
        destinationViewModel.destination.value?.run {
            val markerPosition = LatLng(lat, lng)
            val marker = Marker()
            marker.position = markerPosition
            marker.width = 30
            marker.height = 30
            marker.map = naverMap

            //Log.d(TAG, "setLocation: $markerPosition")
            naverMap.cameraPosition = CameraPosition(markerPosition, 16.0)
        }
    }
    private fun initObserver(){
        destinationViewModel.destination.observe(viewLifecycleOwner){ destination ->
            initView(destination)
        }
    }
    private fun initView(destination: Destination){
        binding.tbDestination.setNavigationIcon(R.drawable.baseline_arrow_back_ios_24)
        binding.tvDestinationTitle.text = destination.address
        binding.etDestinationName.setText(destination.name)
//        binding.btnEditDestination.visibility = if(destinationViewModel.destination.value?.name.isNullOrBlank()) View.GONE else View.VISIBLE
    }
    private fun initListener(){
        binding.btnDestinationConfirm.setOnClickListener{
            // 목적지 입력이 되었을 경우에만 저장
            if(binding.etDestinationName.text.isNullOrBlank()){
                binding.etDestinationName.error = "목적지 이름을 입력해 주세요."
            } else {
                destinationViewModel.destination.value?.run {
                    if(name.isBlank()) saveDestination(this)
                    else editDestination(this)
                }
            }
        }
        binding.tbDestination.setOnMenuItemClickListener { menu->
            if(menu.itemId == R.id.menu_close){
                closePage()
            }
            false
        }
        binding.tvDestinationTitle.setOnClickListener {
            destinationViewModel.destination.value?.run {
                val action = DestinationFragmentDirections.actionDestinationFragmentToSearchAddressFragment(address)
                findNavController().navigate(action)
            }
        }
        binding.tbDestination.setNavigationOnClickListener {
            findNavController().popBackStack()
        }
    }
    private fun initClient(){
        NaverMapSdk.getInstance(requireContext()).client =
            NaverMapSdk.NaverCloudPlatformClient(BuildConfig.NAVER_API_ID)
    }
    private fun initMap(){
        mapFragment = MapFragment.newInstance(
            NaverMapOptions()
                .camera(CameraPosition(NaverMap.DEFAULT_CAMERA_POSITION.target, 16.0))
        )

        mapFragment.getMapAsync(this)

        childFragmentManager.beginTransaction()
            .replace(R.id.map_destination_page, mapFragment)
            .commitNow()
    }
    override fun onMapReady(naverMap: NaverMap) {
        this.naverMap = naverMap

        setLocation()
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initClient()
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initObserver()
        initMap()
        initListener()
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "onDestroy: ")
    }
}