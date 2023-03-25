package ie.healthylunch.app.utils

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import ie.healthylunch.app.ui.NetworkActivity
import ie.healthylunch.app.utils.Constants.Companion.FROM_NETWORK_ERROR_PAGE

class NoNetworkReceiver(var listener: NetworkConnectionListener) : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        // This method is called when the BroadcastReceiver is receiving an Intent broadcast.
     //   val notConnected = intent.getBooleanExtra(ConnectivityManager.EXTRA_NO_CONNECTIVITY, false)
        val notConnected :Boolean= !MethodClass.checkNetworkConnection(context)
        if (notConnected) {
            listener.onNetworkConnection(notConnected)
        } else {
            if (listener is NetworkActivity)
                listener.onNetworkConnection(notConnected)
            else if (FROM_NETWORK_ERROR_PAGE) {
                FROM_NETWORK_ERROR_PAGE = false
                listener.onNetworkConnection(notConnected)
            }
        }
    }
}