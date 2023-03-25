package ie.healthylunch.app.ui

import android.content.IntentFilter
import android.net.ConnectivityManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import ie.healthylunch.app.R
import ie.healthylunch.app.utils.Constants.Companion.FROM_NETWORK_ERROR_PAGE
import ie.healthylunch.app.utils.NoNetworkReceiver
import ie.healthylunch.app.utils.NetworkConnectionListener

class NetworkActivity : AppCompatActivity(), NetworkConnectionListener {
    private lateinit var myReceiver: NoNetworkReceiver
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_network)

        myReceiver = NoNetworkReceiver(this)
    }

    override fun onNetworkConnection(notConnected: Boolean) {
        if (!notConnected) {
            FROM_NETWORK_ERROR_PAGE = true
            finish()
        }
    }

    override fun onResume() {
        super.onResume()
        registerReceiver(myReceiver, IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION))

    }

    override fun onPause() {
        super.onPause()
        unregisterReceiver(myReceiver)
    }

    override fun onBackPressed() {
        //super.onBackPressed()
    }
}