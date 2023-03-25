package ie.healthylunch.app.ui.base

import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider


abstract class BaseFragmentForViewModelOnly<VM : ViewModel> : Fragment() {
    protected lateinit var viewModel: VM

    abstract fun getViewModel(): Class<VM>


    fun setUpViewModel() {

        //get viewModel without a viewModel factory
        viewModel = ViewModelProvider(this).get(getViewModel())
    }
}