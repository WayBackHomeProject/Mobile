package com.ssafy.waybackhome.search

import android.os.Bundle
import android.view.View
import android.widget.SearchView.OnQueryTextListener
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.navigation.navGraphViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.ssafy.waybackhome.LocationViewModel
import com.ssafy.waybackhome.R
import com.ssafy.waybackhome.data.Destination
import com.ssafy.waybackhome.data.geo.GeoAddress
import com.ssafy.waybackhome.databinding.FragmentSearchAddressBinding
import com.ssafy.waybackhome.destination.DestinationViewModel
import com.ssafy.waybackhome.util.BaseFragment

class SearchAddressFragment : BaseFragment<FragmentSearchAddressBinding>(FragmentSearchAddressBinding::inflate) {
    private val searchViewModel : SearchAddressViewModel by viewModels()
    private val locationViewModel : LocationViewModel by activityViewModels()
    private val destinationViewModel : DestinationViewModel by navGraphViewModels(R.id.nav_graph)
    private val args : SearchAddressFragmentArgs by navArgs()

    private lateinit var adapter: SearchListAdapter
    private fun makeNewDestination(address: GeoAddress){
        val destination = Destination(
            address = address.jibunAddress,
            road = address.roadAddress,
            lat = address.lat.toDouble(),
            lng = address.lon.toDouble(),
        )
        destinationViewModel.setDestination(destination)
    }
    private fun editExistingDestination(address: GeoAddress){
        destinationViewModel.changeAddress(address)
    }
    private fun openDestinationPage(address : GeoAddress){
        if(args.address.isNullOrBlank() || !destinationViewModel.destination.isInitialized){
            makeNewDestination(address)
        } else {
            editExistingDestination(address)
        }
        val action = SearchAddressFragmentDirections.actionSearchAddressFragmentToDestinationFragment()
        findNavController().navigate(action)
    }
    private fun searchWith(keyword : String?) : Boolean{
        if(!keyword.isNullOrEmpty() && locationViewModel.currentLocation.value != null){
            searchViewModel.search(keyword, locationViewModel.currentLocation.value!!)
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
        searchViewModel.addressList.observe(viewLifecycleOwner){ list ->
            adapter.submitList(list)
        }
    }
    private fun initListener(){
        binding.svAddress.setOnQueryTextListener(object : OnQueryTextListener{
            override fun onQueryTextSubmit(query: String?): Boolean {
                searchWith(query)
                if(!query.isNullOrEmpty() && locationViewModel.currentLocation.value != null){
                    searchViewModel.search(query, locationViewModel.currentLocation.value!!)
                    return true
                }
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                return false
            }
        })
        binding.tbDestination.setNavigationOnClickListener {
            findNavController().popBackStack()
        }
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        initListener()
        initObserver()
        initData()
    }
}

