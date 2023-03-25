package com.salonsolution.app.ui.base

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.salonsolution.app.interfaces.NetworkConnectionListener
import com.salonsolution.app.ui.activity.NoInternetActivity
import com.salonsolution.app.utils.NetWorkUtils.checkNetworkConnection

open class BaseFragment: Fragment(), NetworkConnectionListener {
    private lateinit var networkConnectionListener: NetworkConnectionListener


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        networkConnectionListener = this@BaseFragment

        //network connection callback
        lifecycleScope.launchWhenCreated {
            context?.let {
                it.checkNetworkConnection().collect{ con ->
                    onNetworkConnection(con)
                }
            }

        }

    }

    private fun onNetworkConnection(connection: Boolean) {
        Log.d("tag","Base Fragment Network connection: $connection")
        if (connection) {
            //reload data if error occurred due to no internet
            networkConnectionListener.onRefreshDataEvent()
           // Toast.makeText(context, "Back to online",Toast.LENGTH_LONG).show()

        }else {
            context?.let {
                val intent = Intent(it, NoInternetActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP)
                startActivity(intent)
              //  Toast.makeText(it, "No internet",Toast.LENGTH_LONG).show()
            }
        }
    }

    override fun onRefreshDataEvent() {
        // Override it on every fragment to refresh the data
    }
}