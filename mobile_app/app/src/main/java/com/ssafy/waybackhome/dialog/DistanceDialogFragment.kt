package com.ssafy.waybackhome.dialog

import android.app.Dialog
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import com.ssafy.waybackhome.databinding.DialogFragmentDistanceBinding
import com.ssafy.waybackhome.main.MainViewModel
import com.ssafy.waybackhome.util.BaseDialogFragment
import com.ssafy.waybackhome.util.formatMeter

class DistanceDialogFragment : BaseDialogFragment<DialogFragmentDistanceBinding>(DialogFragmentDistanceBinding::inflate) {

    private val viewModel : MainViewModel by viewModels(
        ownerProducer = { requireParentFragment() }
    )
    private fun setRange(){
        viewModel.setRange(binding.sbDistance.value)
    }
    fun initView(){
        binding.apply {
            tvDistance.text = viewModel.range.value?.formatMeter()
            sbDistance.apply {
                valueFrom = 500f
                valueTo = 5000f
                value = viewModel.range.value ?: 1000f
                addOnChangeListener { slider, value, fromUser ->
                    tvDistance.text = value.formatMeter()
                }
            }
            btnSubmit.setOnClickListener {
                setRange()
                dismiss()
            }
        }
    }
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return super.onCreateDialog(savedInstanceState).apply {
            setStyle(STYLE_NO_TITLE, android.R.style.Theme_DeviceDefault_Light_Dialog_MinWidth)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
    }
}