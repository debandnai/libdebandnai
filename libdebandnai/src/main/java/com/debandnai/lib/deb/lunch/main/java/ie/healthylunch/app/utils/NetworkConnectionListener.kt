package ie.healthylunch.app.utils

interface NetworkConnectionListener {
    fun onNetworkConnection(notConnected: Boolean)
}