package com.movie.myapplication.ui.activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.movie.myapplication.R
import com.movie.myapplication.data.viewModel.HomeViewModel
import com.movie.myapplication.databinding.ActivityDashboardBinding
import com.movie.myapplication.interface_package.AdapterItemClickListener
import com.movie.myapplication.ui.base.BaseActivity

class DashboardActivity  : BaseActivity() , AdapterItemClickListener {
    private lateinit var viewModel: HomeViewModel
    private lateinit var binding: ActivityDashboardBinding
    private  var navController: NavController?= null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        viewModel = ViewModelProvider(this)[HomeViewModel::class.java]

        binding= DataBindingUtil.setContentView(this, R.layout.activity_dashboard)
        binding.viewModel1=viewModel
        init()
        viewOnclick()
    }

    fun init(){
        val navHostFragment: NavHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController = navHostFragment.navController
    }
    private fun viewOnclick() {
        binding.bottomNav.setOnItemSelectedListener{
            when (it.itemId) {
                R.id.home -> {

                    navController?.navigate(R.id.homeFragment)
                    true
                }
                R.id.message -> {
                    true
                }
                R.id.other -> {
                    true
                }
                R.id.logout -> {
                    viewModel.logoutAllUser()
                    val intent= Intent(this@DashboardActivity,AuthenticationActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                    startActivity(intent)
                    finish()
                    true
                }
                else -> {false}
            }
        }
    }

    override fun onAdapterItemClick(
        arrayList: List<Any?>?,
        position: Int,
        clickView: View?,
        tag: String
    ) {
    }
}