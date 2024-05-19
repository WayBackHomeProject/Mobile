package com.ssafy.waybackhome.main

import MapContainerFragment
import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.navGraphViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetBehavior.BottomSheetCallback
import com.ssafy.waybackhome.R
import com.ssafy.waybackhome.data.Destination
import com.ssafy.waybackhome.databinding.FragmentMainBinding
import com.ssafy.waybackhome.destination.DestinationViewModel
import com.ssafy.waybackhome.util.BaseFragment

class MainFragment : BaseFragment<FragmentMainBinding>(FragmentMainBinding::inflate) {

    private val viewModel: MainViewModel by viewModels()
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
        destinationAdapter = DestinationListAdapter()
        destinationAdapter.setOnItemClickListener{dest ->
            editDestination(dest)
        }
        binding.rvDestinations.adapter = destinationAdapter
        binding.rvDestinations.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)

        bottomSheetBehavior = BottomSheetBehavior.from(binding.mainBottomSheet)
        bottomSheetBehavior.state = viewModel.bottomSheetState
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