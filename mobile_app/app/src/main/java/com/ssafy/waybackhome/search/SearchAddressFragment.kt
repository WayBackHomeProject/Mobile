package com.ssafy.waybackhome.search

import android.os.Bundle
import android.view.View
import android.widget.SearchView.OnQueryTextListener
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.ssafy.waybackhome.LocationViewModel
import com.ssafy.waybackhome.R
import com.ssafy.waybackhome.data.Destination
import com.ssafy.waybackhome.data.geo.GeoAddress
import com.ssafy.waybackhome.databinding.FragmentSearchAddressBinding
import com.ssafy.waybackhome.util.BaseFragment

class SearchAddressFragment : BaseFragment<FragmentSearchAddressBinding>(FragmentSearchAddressBinding::inflate) {
    private val viewModel : SearchAddressViewModel by viewModels()
    private val locationViewModel by activityViewModels<LocationViewModel>()
    private val args : SearchAddressFragmentArgs by navArgs()

    private lateinit var adapter: SearchListAdapter
    private fun openDestinationPage(address : GeoAddress){
        val destination = Destination(
            address = address.jibunAddress,
            road = address.roadAddress,
            lat = address.lat.toDouble(),
            lng = address.lon.toDouble(),
        )
        val action = SearchAddressFragmentDirections.actionSearchAddressFragmentToDestinationFragment(destination)
        findNavController().navigate(action)
    }
    private fun searchWith(keyword : String?) : Boolean{
        if(!keyword.isNullOrEmpty() && locationViewModel.currentLocation.value != null){
            viewModel.search(keyword, locationViewModel.currentLocation.value!!)
            return true
        }
        return false;
    }
    private fun initData(){
        searchWith(args.address)
    }
    private fun initView(){
        adapter = SearchListAdapter()
        adapter.setOnItemClickListener{address ->
            openDestinationPage(address)
        }
        binding.rvSearchResult.adapter = adapter
        binding.rvSearchResult.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
    }
    private fun initObserver(){
        viewModel.addressList.observe(viewLifecycleOwner){list ->
            adapter.submitList(list)
        }
    }
    private fun initListener(){
        binding.svAddress.setOnQueryTextListener(object : OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean {
                searchWith(query)
                if(!query.isNullOrEmpty() && locationViewModel.currentLocation.value != null){
                    viewModel.search(query, locationViewModel.currentLocation.value!!)
                    return true
                }
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                return false
            }
        })
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        initListener()
        initObserver()
        initData()
    }
}

