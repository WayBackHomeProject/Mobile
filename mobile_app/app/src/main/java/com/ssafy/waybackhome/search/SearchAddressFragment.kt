package com.ssafy.waybackhome.search

import android.os.Bundle
import android.view.View
import com.ssafy.waybackhome.databinding.FragmentSearchAddressBinding
import com.ssafy.waybackhome.util.BaseFragment

class SearchAddressFragment : BaseFragment<FragmentSearchAddressBinding>(FragmentSearchAddressBinding::inflate) {
    private fun initView(){

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
    }
}