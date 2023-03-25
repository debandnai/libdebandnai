package com.movie.myapplication.ui.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.movie.myapplication.R
import com.movie.myapplication.data.viewModel.AuthViewModel
import com.movie.myapplication.databinding.ActivityAuthenticationBinding
import com.movie.myapplication.utils.Constants.TAG
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AuthenticationActivity : AppCompatActivity() {

    private lateinit var viewModel: AuthViewModel
    private lateinit var binding: ActivityAuthenticationBinding
    private lateinit var navController: NavController
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(this)[AuthViewModel::class.java]
        binding = DataBindingUtil.setContentView(this, R.layout.activity_authentication)
        binding.viewModel=viewModel
        isLogin()
        observer()
    }

    private fun observer() {
        viewModel.titleTag.observe(this){
            Log.d(TAG, "observer: $it")
            binding.tvTitleTag.text = it
        }
    }


    fun isLogin() {
            viewModel.sessionDetailsLiveData.observe(this) { user ->
                if (!user.isNullOrEmpty()) {
                    startActivity(
                        Intent(
                            this,
                            DashboardActivity::class.java
                        ).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)

                    )
                    finish()
                }
                else{
                    setNavigation()
                }
            }


    }
    private fun setNavigation() {
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.fragmentContainerView) as NavHostFragment
        navController= navHostFragment.navController
        navController.navigate(R.id.loginFragment)
    }
}