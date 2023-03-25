package ie.healthylunch.app.ui

import android.os.Bundle
import androidx.databinding.DataBindingUtil
import ie.healthylunch.app.R
import ie.healthylunch.app.data.viewModel.HelpViewModel
import ie.healthylunch.app.databinding.ActivityHelpBinding
import ie.healthylunch.app.ui.base.BaseActivityForViewModelOnly

class HelpActivity : BaseActivityForViewModelOnly<HelpViewModel>() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setUpViewModel()
        val binding: ActivityHelpBinding =
            DataBindingUtil.setContentView(this, R.layout.activity_help)
        binding.activity = this
        binding.viewModel = viewModel
        binding.lifecycleOwner = this


    }

    override fun getViewModel() = HelpViewModel::class.java

}