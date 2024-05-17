package com.ssafy.waybackhome.search

import android.os.Bundle
import android.view.View
import android.widget.SearchView.OnQueryTextListener
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.naver.maps.geometry.LatLng
import com.ssafy.waybackhome.LocationViewModel
import com.ssafy.waybackhome.databinding.FragmentSearchAddressBinding
import com.ssafy.waybackhome.util.BaseFragment

class SearchAddressFragment : BaseFragment<FragmentSearchAddressBinding>(FragmentSearchAddressBinding::inflate) {
    private val viewModel : SearchAddressViewModel by viewModels()
    private val locationViewModel by activityViewModels<LocationViewModel>()

    private lateinit var adapter: SearchListAdapter
    private fun initView(){
        adapter = SearchListAdapter()
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
    }
}