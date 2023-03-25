package com.merkaaz.app.ui.activity

import android.content.IntentFilter
import android.net.ConnectivityManager
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.merkaaz.app.R
import com.merkaaz.app.interfaces.NetworkConnectionListener
import com.merkaaz.app.utils.NoNetworkReceiver

class NetworkActivity : AppCompatActivity(), NetworkConnectionListener {
    lateinit var nonetwork : NoNetworkReceiver
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_network)
        nonetwork = NoNetworkReceiver(this)
    }

    override fun onNetworkConnection(connectionStatus: Boolean) {
        if (connectionStatus) {
          finish()
        }
    }

    override fun onResume() {
        super.onResume()
        registerReceiver(nonetwork, IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION))
    }

    override fun onPause() {
        super.onPause()
        unregisterReceiver(nonetwork)
    }
}