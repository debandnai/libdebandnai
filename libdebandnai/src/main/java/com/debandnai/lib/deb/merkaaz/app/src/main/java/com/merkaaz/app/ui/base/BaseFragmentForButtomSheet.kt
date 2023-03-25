package com.merkaaz.app.ui.base

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.merkaaz.app.R


abstract class BaseFragmentForButtomSheet<VM : ViewModel> : BottomSheetDialogFragment()  {
    protected lateinit var viewModel: VM

    abstract fun getViewModel(): Class<VM>

    override fun getTheme() = R.style.SheetDialog
    fun setUpViewModel() {

        //get viewModel without a viewModel factory
        viewModel = ViewModelProvider(this)[getViewModel()]
    }
}