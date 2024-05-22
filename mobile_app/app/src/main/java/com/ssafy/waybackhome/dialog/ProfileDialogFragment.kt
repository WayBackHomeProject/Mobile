package com.ssafy.waybackhome.dialog

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import com.ssafy.waybackhome.databinding.FragmentProfileDialogBinding

class ProfileDialogFragment : DialogFragment() {
    private var _binding: FragmentProfileDialogBinding? = null
    private val binding get() = _binding!!

    private var isLoading = true
    private val viewModel: DialogViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileDialogBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
