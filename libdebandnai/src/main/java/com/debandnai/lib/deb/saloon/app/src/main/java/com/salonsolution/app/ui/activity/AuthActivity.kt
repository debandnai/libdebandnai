package com.salonsolution.app.ui.activity

import android.os.Bundle
import android.util.Log
import androidx.activity.OnBackPressedCallback
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.salonsolution.app.R
import com.salonsolution.app.data.preferences.AppSettingsPref
import com.salonsolution.app.data.viewModel.AuthViewModel
import com.salonsolution.app.databinding.ActivityAuthBinding
import com.salonsolution.app.ui.base.BaseActivity
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class AuthActivity : BaseActivity() {
    private lateinit var binding: ActivityAuthBinding
    private lateinit var viewModel: AuthViewModel
    private lateinit var navController: NavController

    @Inject
    lateinit var appSettingsPref: AppSettingsPref
    private var pressedTime: Long = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_auth)
        viewModel = ViewModelProvider(this)[AuthViewModel::class.java]

        //navigation component
        val navHostFragment = supportFragmentManager
            .findFragmentById(R.id.fragmentContainerView) as NavHostFragment
        navController = navHostFragment.navController

        //setObserver()
        addBackPressCallBack()

    }


    override fun onResume() {
        super.onResume()
    }

//    fun changeLocale(langCode: String) {
//        appSettingsPref.setLanguage(langCode)
//       // recreate()
//    }

    private fun addBackPressCallBack() {
        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                Log.d("tag", "back button click")
                backButtonClick()
            }
        }
        onBackPressedDispatcher.addCallback(this, callback)
    }

    private fun backButtonClick() {
        when (navController.currentDestination?.id) {
            R.id.loginFragment, R.id.chooseLanguageFragment, R.id.passwordResetSuccessFragment, R.id.otpVerificationFragment -> {
                finish()
            }
            else -> {
                navController.popBackStack()
            }
        }
    }
}