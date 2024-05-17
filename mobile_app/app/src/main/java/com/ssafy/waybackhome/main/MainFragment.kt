package com.ssafy.waybackhome.main

import MapContainerFragment
import android.os.Bundle
import android.view.View
import androidx.core.view.ViewCompat
import androidx.core.view.marginBottom
import androidx.core.view.updateLayoutParams
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetBehavior.BottomSheetCallback
import com.ssafy.waybackhome.R
import com.ssafy.waybackhome.databinding.FragmentMainBinding
import com.ssafy.waybackhome.permission.PermissionChecker
import com.ssafy.waybackhome.search.SearchAddressFragment
import com.ssafy.waybackhome.util.BaseFragment

class MainFragment : BaseFragment<FragmentMainBinding>(FragmentMainBinding::inflate) {

    private val viewModel: MainViewModel by viewModels()

    private lateinit var destinationAdapter : DestinationListAdapter
    private lateinit var bottomSheetBehavior: BottomSheetBehavior<View>
    private lateinit var mapFragment : MapContainerFragment

    private fun adjustMapSize(){
        val totalHeight = binding.root.height
        val bottomSheetTop = binding.mainBottomSheet.top
        mapFragment.changeBottomPadding(totalHeight-bottomSheetTop)
    }
    private fun initView(){
        destinationAdapter = DestinationListAdapter()
        binding.rvDestinations.adapter = destinationAdapter
        binding.rvDestinations.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)

        bottomSheetBehavior = BottomSheetBehavior.from(binding.mainBottomSheet)
        bottomSheetBehavior.state = viewModel.bottomSheetState
        bottomSheetBehavior.addBottomSheetCallback(object : BottomSheetCallback(){
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                viewModel.bottomSheetState = newState
                when(newState){
                    BottomSheetBehavior.STATE_EXPANDED -> {}
                    else -> {
                        adjustMapSize()
                    }
                }
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {
                when(bottomSheetBehavior.state){
                    BottomSheetBehavior.STATE_DRAGGING, BottomSheetBehavior.STATE_SETTLING -> {
                        adjustMapSize()
                    }
                    else -> {}
                }
            }
        })
        mapFragment = MapContainerFragment.newInstance(binding.mainBottomSheet.minimumHeight)
        childFragmentManager.beginTransaction()
            .replace(R.id.map_container_frame, mapFragment)
            .commitNow()
    }
    private fun initObserver(){
        viewModel.destinations.observe(viewLifecycleOwner){ list ->
            destinationAdapter.submitList(list)
        }
    }
    private fun initListener(){
        binding.btnAddDest.setOnClickListener{
            openSearch()
        }
    }
    private fun openSearch(){
        findNavController().navigate(R.id.action_mainFragment_to_searchAddressFragment)
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        initListener()
        initObserver()
    }
}