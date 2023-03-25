package com.merkaaz.app.ui.base

import android.content.IntentFilter
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.merkaaz.app.data.model.LoginModel
import com.merkaaz.app.interfaces.UserApprovedCallBack
import com.merkaaz.app.ui.activity.DashBoardActivity
import com.merkaaz.app.ui.fragments.HomeFragment
import com.merkaaz.app.utils.CommonFunctions
import com.merkaaz.app.utils.Constants
import com.merkaaz.app.utils.SharedPreff
import com.merkaaz.app.utils.UserApprovedBroadcastReceiver
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
open class BaseFragment : Fragment(), UserApprovedCallBack {
    lateinit var loginModel: LoginModel
    lateinit var language_id: String
    lateinit var userApprovedBroadcastReceiver: UserApprovedBroadcastReceiver

    @Inject
    lateinit var sharedPreff: SharedPreff

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        init()

    }

    private fun init() {
        context?.let {
            language_id = sharedPreff.getlanguage_id()!!
            loginModel = sharedPreff.get_logindata()
        }
        userApprovedBroadcastReceiver= UserApprovedBroadcastReceiver()
        UserApprovedBroadcastReceiver.listener=this@BaseFragment
    }

    override fun onResume() {
        super.onResume()
       /* context?.let {  LocalBroadcastManager.getInstance(it)
            .registerReceiver(userApprovedBroadcastReceiver, IntentFilter(Constants.APPROVED_USER)) }*/
        context?.registerReceiver(userApprovedBroadcastReceiver, IntentFilter(Constants.APPROVED_USER))
    }
    override fun onPause() {
        super.onPause()
        //context?.let { LocalBroadcastManager.getInstance(it).unregisterReceiver(userApprovedBroadcastReceiver) }
        context?.unregisterReceiver(userApprovedBroadcastReceiver)
    }

    override fun userApprovedStatus() {
        //Set user status as active
        loginModel.isActive=1
        context?.let { sharedPreff.store_logindata(loginModel) }

        if (this is HomeFragment){
            this.refreshAdapter()
        }
        activity?.let {
            (it as? DashBoardActivity)?.check_user_active(loginModel)
        }
    }

}