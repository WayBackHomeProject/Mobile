package com.ssafy.waybackhome.destination

import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
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

class DestinationFragment : BaseFragment<FragmentDestinationBinding>(FragmentDestinationBinding::inflate), OnMapReadyCallback {

    private val viewModel : DestinationViewModel by viewModels()

    private lateinit var naverMap: NaverMap
    private lateinit var mapFragment: MapFragment

    private fun initData(){
        val destination = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            arguments?.getParcelable(ARG_DEST, Destination::class.java)
        } else {
            arguments?.getParcelable(ARG_DEST)// as Destination?
        }

        if (destination != null) {
            viewModel.setDestination(destination)
        }
    }
    private fun initObserver(){
        viewModel.destination.observe(viewLifecycleOwner){ destination ->
            initView(destination)
        }
    }
    private fun initView(destination: Destination){
        binding.tvAddress.text = destination.address
        binding.etDestinationName.setText(destination.name)
    }
    private fun initListener(){
        binding.btnDestinationConfirm.setOnClickListener{
            // 목적지 입력이 되었을 경우에만 저장
            if(binding.etDestinationName.text.isNullOrBlank()){
                binding.etDestinationName.error = "목적지 이름을 입력해 주세요."
            } else {
                viewModel.destination.value?.run {
                    if(name.isBlank()) saveDestination(this)
                    else editDestination(this)
                }
            }
        }
        binding.btnCloseDestination.setOnClickListener {
            findNavController().popBackStack()
        }
    }
    private fun saveDestination(destination: Destination){
        viewModel.insertDestination(
            destination.copy(
                name = binding.etDestinationName.text.toString()
            )
        )
    }
    private fun editDestination(destination: Destination){
        viewModel.updateDestination(
            destination.copy(
                name = binding.etDestinationName.text.toString()
            )
        )
    }
    private fun initClient(){
        NaverMapSdk.getInstance(requireContext()).client =
            NaverMapSdk.NaverCloudPlatformClient(BuildConfig.NAVER_API_ID)
    }
    private fun initMap(){
        mapFragment = MapFragment.newInstance(
            NaverMapOptions()
                .camera(CameraPosition(NaverMap.DEFAULT_CAMERA_POSITION.target, 16.0))
                .locationButtonEnabled(true)
        )

        mapFragment.getMapAsync(this)

        childFragmentManager.beginTransaction()
            .replace(R.id.mapFrame, mapFragment)
            .commitNow()
    }
    private fun setLocation(){
        viewModel.destination.value?.run {
            val markerPosition = LatLng(lat, lng)
            val marker = Marker()
            marker.position = markerPosition
            marker.width = 30
            marker.height = 30
            marker.map = naverMap
        }
    }
    override fun onMapReady(naverMap: NaverMap) {
        this.naverMap = naverMap

        setLocation()
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initData()
        initClient()
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initObserver()
        initMap()
    }
    companion object{
        fun newInstance(destination: Destination) = DestinationFragment().apply {
            arguments = Bundle().apply {
                putParcelable(ARG_DEST, destination)
            }
        }
    }
}

private val ARG_DEST = "ARG_ADDRESS"