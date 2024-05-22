package com.ssafy.waybackhome.dialog

import android.app.Dialog
import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import com.ssafy.waybackhome.databinding.DialogFragmentProfileBinding
import com.ssafy.waybackhome.util.BaseDialogFragment

class ProfileDialogFragment : BaseDialogFragment<DialogFragmentProfileBinding>(DialogFragmentProfileBinding::inflate) {

    private var isLoading = true
    private val viewModel: DialogViewModel by viewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews()
        initObserver()
    }

    private fun initObserver() {
        viewModel.getSafeTip()
        viewModel.safeTip.observe(viewLifecycleOwner){ tip ->
            binding.dialogMainTv.text = tip
            isLoading = false
            showProgress(isLoading)
        }

    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return super.onCreateDialog(savedInstanceState).apply {
            setStyle(STYLE_NO_TITLE, android.R.style.Theme_DeviceDefault_Light_Dialog_MinWidth)
        }
    }

    private fun initViews() {
        showProgress(isLoading)
        binding.profileBtn.setOnClickListener {
            dismiss()
        }
    }

    private fun showProgress(isShow: Boolean) {
        binding.apply {
            if (isShow) {
                progressBar.visibility = View.VISIBLE
                progressLoadingTv.visibility = View.VISIBLE
                dialogMainTv.visibility = View.GONE
            } else {
                progressBar.visibility = View.GONE
                progressLoadingTv.visibility = View.GONE
                dialogMainTv.visibility = View.VISIBLE
            }
        }
    }
}
