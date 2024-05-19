package com.ssafy.waybackhome.main

import MapContainerFragment
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.navGraphViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetBehavior.BottomSheetCallback
import com.naver.maps.geometry.LatLng
import com.ssafy.waybackhome.LocationViewModel
import com.ssafy.waybackhome.R
import com.ssafy.waybackhome.data.Destination
import com.ssafy.waybackhome.databinding.FragmentMainBinding
import com.ssafy.waybackhome.destination.DestinationViewModel
import com.ssafy.waybackhome.util.BaseFragment

private const val TAG = "MainFragment"
class MainFragment : BaseFragment<FragmentMainBinding>(FragmentMainBinding::inflate) {

    private val viewModel: MainViewModel by viewModels()
    private val locationViewModel : LocationViewModel by activityViewModels()
    private val destinationViewModel : DestinationViewModel by navGraphViewModels(R.id.nav_graph)

    private lateinit var destinationAdapter : DestinationListAdapter
    private lateinit var bottomSheetBehavior: BottomSheetBehavior<View>
    private lateinit var mapFragment : MapContainerFragment

    private fun editDestination(destination: Destination){
        destinationViewModel.setDestination(destination)
        val action = MainFragmentDirections.actionMainFragmentToDestinationFragment()
        findNavController().navigate(action)
    }
    private fun adjustMapSize(){
        val totalHeight = binding.root.height
        val bottomSheetTop = binding.mainBottomSheet.top
        mapFragment.changeBottomPadding(totalHeight-bottomSheetTop)
    }
    private fun initView(){
        destinationAdapter = DestinationListAdapter(locationViewModel.currentLocation)
        destinationAdapter.setOnItemClickListener{dest ->
            editDestination(dest)
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
        mapFragment = MapContainerFragment.newInstance(binding.mainBottomSheet.minimumHeight)
        childFragmentManager.beginTransaction()
            .replace(R.id.map_container_frame, mapFragment)
            .commitNow()
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
            val sorted = viewModel.destinations.value?.sortedBy {
                val latLng = LatLng(it.lat, it.lng)
                val dist = latLng.distanceTo(location)
                dist
            }
            if(sorted != null) destinationAdapter.submitList(sorted)
        }
    }
    private fun initListener(){
        binding.btnAddDest.setOnClickListener{
            openSearch()
        }
    }
    private fun openSearch(){
        val action = MainFragmentDirections.actionMainFragmentToSearchAddressFragment(null)
        findNavController().navigate(action)
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        initListener()
        initObserver()
    }
}