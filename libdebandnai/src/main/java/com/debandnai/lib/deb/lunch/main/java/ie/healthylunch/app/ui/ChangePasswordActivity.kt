package ie.healthylunch.app.ui

import android.os.Build
import android.os.Bundle
import androidx.activity.OnBackPressedCallback
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import ie.healthylunch.app.R
import ie.healthylunch.app.data.network.ApiInterface
import ie.healthylunch.app.data.repository.ChangePasswordActivityRepository
import ie.healthylunch.app.data.viewModel.ChangePasswordActivityViewModel
import ie.healthylunch.app.databinding.ActivityChangePasswordBinding
import ie.healthylunch.app.ui.base.BaseActivity


class ChangePasswordActivity :
    BaseActivity<ChangePasswordActivityViewModel, ChangePasswordActivityRepository>() {
    lateinit var activityChangePasswordBinding: ActivityChangePasswordBinding
      
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setUpViewModel()
        window.statusBarColor = ContextCompat.getColor(this@ChangePasswordActivity, R.color.sky_bg_2)
        activityChangePasswordBinding =
            DataBindingUtil.setContentView(this, R.layout.activity_change_password)

        activityChangePasswordBinding.context = this
        activityChangePasswordBinding.viewModel = viewModel
        activityChangePasswordBinding.lifecycleOwner = this
        //Change Password Response
        viewModel.getChangePasswordResponse(this)

        onBackPressedDispatcher.addCallback(this,onBackPressedCallback)
        viewOnClick()
    }

    private fun viewOnClick() {
        activityChangePasswordBinding.rlTopNew.setOnClickListener{
            onBackPressedDispatcher.onBackPressed()
        }
    }


    override fun getViewModel() = ChangePasswordActivityViewModel::class.java
    override fun getRepository() =
        ChangePasswordActivityRepository(remoteDataSource.buildApi(ApiInterface::class.java))

    private val onBackPressedCallback=object: OnBackPressedCallback(true){
        override fun handleOnBackPressed() {
           finish()
        }

    }
    }
