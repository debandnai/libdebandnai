package ie.healthylunch.app.ui.base

import android.content.Intent
import android.content.IntentFilter
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import ie.healthylunch.app.data.network.RemoteDataSource
import ie.healthylunch.app.data.repository.BaseRepository
import ie.healthylunch.app.utils.NoNetworkReceiver
import ie.healthylunch.app.utils.NetworkConnectionListener
import ie.healthylunch.app.ui.NetworkActivity

abstract class BaseFragment<VM : ViewModel, R : BaseRepository> : Fragment(),
    NetworkConnectionListener {

    protected val remoteDataSource = RemoteDataSource()
    lateinit var viewModel: VM

    abstract fun getViewModel(): Class<VM>

    abstract fun getRepository(): R

    private lateinit var myReceiver: NoNetworkReceiver

    fun setUpViewModel() {
        val factory = ViewModelFactory(getRepository())
        viewModel = ViewModelProvider(this, factory).get(getViewModel())

        //initialize broadcast receiver on onCreate method of any activity. Because this method is called in onCreate
        myReceiver = NoNetworkReceiver(this)
    }


    @Suppress("DEPRECATION")
    override fun onStart() {
        super.onStart()
        requireActivity().registerReceiver(
            myReceiver,
            IntentFilter("android.net.conn.CONNECTIVITY_CHANGE")
        )

    }

    override fun onStop() {
        super.onStop()
        requireActivity().unregisterReceiver(myReceiver)
    }


    //on Network connection implement method
    override fun onNetworkConnection(notConnected: Boolean) {

        if (notConnected)
            requireActivity().startActivity(Intent(requireActivity(), NetworkActivity::class.java))
        else {
            when (this) {


            }

        }

    }
}