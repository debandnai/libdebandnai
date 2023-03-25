package com.salonsolution.app.ui.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.activity.OnBackPressedCallback
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.lifecycleScope
import com.salonsolution.app.R
import com.salonsolution.app.databinding.ActivityNoInternetBinding
import com.salonsolution.app.utils.NetWorkUtils.checkNetworkConnection

class NoInternetActivity : AppCompatActivity() {
    private lateinit var binding: ActivityNoInternetBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_no_internet)

        addBackPressCallBack()

        //network connection callback
        lifecycleScope.launchWhenCreated {
            checkNetworkConnection().collect { con ->
                onNetworkConnectionChange(con)
            }
        }
    }

    private fun addBackPressCallBack() {
        val callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                Log.d("tag", "back button click")
                //nothing to do
            }
        }
        onBackPressedDispatcher.addCallback(this, callback)
    }

    private fun onNetworkConnectionChange(connection: Boolean) {
        if (connection) {
            finish()
        }
    }
}