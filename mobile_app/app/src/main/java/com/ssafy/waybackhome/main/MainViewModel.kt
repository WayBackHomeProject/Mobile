package com.ssafy.waybackhome.main

import androidx.lifecycle.ViewModel
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.ssafy.waybackhome.data.LocalRepository

class MainViewModel : ViewModel() {

    private val repo = LocalRepository.instance

    val destinations = repo.selectAllDestination()

    var bottomSheetState : Int = BottomSheetBehavior.STATE_COLLAPSED
}