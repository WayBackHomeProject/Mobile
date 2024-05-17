package com.ssafy.waybackhome.main

import MapContainerFragment
import android.os.Bundle
import android.view.View
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.ssafy.waybackhome.R
import com.ssafy.waybackhome.databinding.FragmentMainBinding
import com.ssafy.waybackhome.util.BaseFragment

class MainFragment : BaseFragment<FragmentMainBinding>(FragmentMainBinding::inflate) {

    private val viewModel: MainViewModel by lazy {
        ViewModelProvider(this)[MainViewModel::class.java]
    }

    private lateinit var destinationAdapter : DestinationListAdapter
    private lateinit var bottomSheetBehavior: BottomSheetBehavior<View>
    private fun initView(){
        destinationAdapter = DestinationListAdapter()
        binding.rvDestinations.adapter = destinationAdapter
        binding.rvDestinations.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)

        bottomSheetBehavior = BottomSheetBehavior.from(binding.mainBottomSheet)
    }
    fun initObserver(){

        childFragmentManager.beginTransaction()
            .replace(R.id.map_container_frame, MapContainerFragment())
            .commitNow()

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