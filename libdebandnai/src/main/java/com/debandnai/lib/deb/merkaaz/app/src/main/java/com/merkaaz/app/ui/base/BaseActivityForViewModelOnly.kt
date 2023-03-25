package com.merkaaz.app.ui.base

import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider


abstract class BaseActivityForViewModelOnly<VM : ViewModel> : AppCompatActivity() {
    protected lateinit var viewModel: VM

    abstract fun getViewModel(): Class<VM>


    fun setUpViewModel() {

        //get viewModel without a viewModel factory
        viewModel = ViewModelProvider(this).get(getViewModel())
    }


}