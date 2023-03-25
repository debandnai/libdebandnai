package com.merkaaz.app.ui.activity

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import androidx.databinding.DataBindingUtil.setContentView
import androidx.lifecycle.ViewModelProvider
import com.merkaaz.app.R
import com.merkaaz.app.network.Response
import com.merkaaz.app.data.viewModel.LoginViewModel
import com.merkaaz.app.databinding.ActivityLoginBinding
import com.merkaaz.app.ui.base.BaseActivity
import com.merkaaz.app.utils.CommonFunctions
import com.merkaaz.app.utils.Constants

import com.merkaaz.app.utils.MethodClass
import com.merkaaz.app.utils.SharedPreff
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class LoginActivity : BaseActivity() {
    private lateinit var loginViewModel: LoginViewModel
    var binding: ActivityLoginBinding? = null
    private var loader: Dialog? = null

    @Inject
    lateinit var commonFunctions: CommonFunctions

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = setContentView(this, R.layout.activity_login)

        loginViewModel = ViewModelProvider(this)[LoginViewModel::class.java]
        binding?.viewModel = loginViewModel
        binding?.lifecycleOwner = this

        initialise()
        observeData()
        onViewClick()
        setLanguageCode()
    }


    private fun setLanguageCode() {
        //set flag icon dynamically
        if (sharedPreff.getlanguage_code().equals(Constants.ENGLISH_CODE,true))
            binding?.ivFlagIcon?.setImageResource(R.drawable.english_flag)
        else
            binding?.ivFlagIcon?.setImageResource(R.drawable.flag_icon_portuguese)

    }


    private fun onViewClick() {
        binding?.ivFlagIcon?.setOnClickListener {
            MethodClass.showBottomSheetDialog(this, getString(R.string.please_wait))
        }
    }

    private fun initialise() {
        loader = MethodClass.custom_loader(this, getString(R.string.please_wait))
    }

    private fun observeData() {
        loginViewModel.loginLiveData.observe(this) {

            when (it) {
                is Response.Loading -> {
                    loader?.show()
                }
                is Response.Success -> {
                    it.data?.let { data ->
                        val res = data.response?.status?.msg
                        loader?.dismiss()
                        if (data.response?.status?.actionStatus == true) {
                            sharedPreff.setPhone(loginViewModel.mobileNumber.value?.toString())


                            startActivity(Intent(this, OtpActivity::class.java).putExtra(Constants.PAGE_TYPE,getString(R.string.login)))
                            finish()
                        } else {
                            MethodClass.custom_msg_dialog(this, it.errorMessage)?.show()
                        }
                    }
                }
                is Response.Error -> {
                    loader?.dismiss()
                    commonFunctions.showErrorMsg(this, it.errorCode,it.errorMessage)

                }
            }
        }
    }

}