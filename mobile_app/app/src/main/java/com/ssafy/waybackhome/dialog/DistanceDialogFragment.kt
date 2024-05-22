package com.ssafy.waybackhome.dialog

import android.app.Dialog
import android.os.Bundle
import com.ssafy.waybackhome.databinding.DialogFragmentDistanceBinding
import com.ssafy.waybackhome.util.BaseDialogFragment

class DistanceDialogFragment : BaseDialogFragment<DialogFragmentDistanceBinding>(DialogFragmentDistanceBinding::inflate) {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return super.onCreateDialog(savedInstanceState).apply {
            setStyle(STYLE_NO_TITLE, android.R.style.Theme_DeviceDefault_Light_Dialog_MinWidth)
        }
    }
}