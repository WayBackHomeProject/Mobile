package com.ssafy.waybackhome.destination

import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.ssafy.waybackhome.R
import com.ssafy.waybackhome.data.geo.GeoAddress
import com.ssafy.waybackhome.databinding.FragmentDestinationBinding
import com.ssafy.waybackhome.util.BaseFragment

class DestinationFragment : BaseFragment<FragmentDestinationBinding>(FragmentDestinationBinding::inflate) {

    private val viewModel : DestinationViewModel by viewModels()

    fun initData(){
        val address = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            arguments?.getParcelable(ARG_ADDRESS, GeoAddress::class.java)
        } else {
            arguments?.getParcelable(ARG_ADDRESS)
        }

        if (address != null) {
            viewModel.setDestinationAddress(address)
        }
    }
    fun initView(){

    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initData()
    }
    companion object{
        fun newInstance(address : GeoAddress) = DestinationFragment().apply {
            arguments = Bundle().apply {
                putParcelable(ARG_ADDRESS, address)
            }
        }
    }
}

private val ARG_ADDRESS = "ARG_ADDRESS"