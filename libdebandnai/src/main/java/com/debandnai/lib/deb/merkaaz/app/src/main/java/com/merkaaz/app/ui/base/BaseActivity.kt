package com.merkaaz.app.ui.base

import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.content.IntentFilter
import android.net.ConnectivityManager
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.merkaaz.app.data.model.LoginModel
import com.merkaaz.app.interfaces.NetworkConnectionListener
import com.merkaaz.app.interfaces.UserApprovedCallBack
import com.merkaaz.app.ui.activity.DashBoardActivity
import com.merkaaz.app.ui.activity.NetworkActivity
import com.merkaaz.app.utils.*
import com.merkaaz.app.utils.Constants.APPROVED_USER
import dagger.hilt.android.AndroidEntryPoint
import java.util.*
import javax.inject.Inject

@AndroidEntryPoint
open class BaseActivity : AppCompatActivity(), NetworkConnectionListener/*,UserApprovedCallBack*/ {
    private  var oldPrefLocaleCode : String?= Constants.PORTUGUESE_CODE
    lateinit var nonetwork : NoNetworkReceiver
    lateinit var loginModel: LoginModel
    lateinit var userApprovedBroadcastReceiver:UserApprovedBroadcastReceiver

    @Inject
    lateinit var sharedPreff: SharedPreff

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        nonetwork = NoNetworkReceiver(this)
        loginModel = sharedPreff.get_logindata()
        userApprovedBroadcastReceiver= UserApprovedBroadcastReceiver()
//        UserApprovedBroadcastReceiver.listener=this@BaseActivity
    }

    override fun attachBaseContext(base: Context) {
        val shrdprf = SharedPreff(base)
        oldPrefLocaleCode = shrdprf.getlanguage_code()
        val localeToSwitchTo = Locale(oldPrefLocaleCode)
        val localeUpdatedContext: ContextWrapper = LocaleHelper.updateLocale(base, localeToSwitchTo)
        super.attachBaseContext(localeUpdatedContext)
    }

    override fun onResume() {
        val currentLocaleCode = sharedPreff.getlanguage_code()
        if(oldPrefLocaleCode != currentLocaleCode){
            recreate() //locale is changed, restart the activty to update
            oldPrefLocaleCode = currentLocaleCode
        }
        super.onResume()
        registerReceiver(nonetwork, IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION))
        registerReceiver(userApprovedBroadcastReceiver, IntentFilter(APPROVED_USER))
       /* LocalBroadcastManager.getInstance(this)
            .registerReceiver(userApprovedBroadcastReceiver, IntentFilter(Constants.APPROVED_USER))*/
    }

    override fun onPause() {
        super.onPause()
        unregisterReceiver(nonetwork)
        unregisterReceiver(userApprovedBroadcastReceiver)
       // LocalBroadcastManager.getInstance(this).unregisterReceiver(userApprovedBroadcastReceiver)
    }

    override fun onNetworkConnection(connectionStatus: Boolean) {
        Log.d("status network", connectionStatus.toString())
        if (!MethodClass.check_networkconnection(this))
            startActivity(Intent(this, NetworkActivity::class.java))
    }

    

//    override fun userApprovedStatus() {
//        //Set user status as active
//        loginModel.isActive=1
//        sharedPreff.store_logindata(loginModel)
//        if (this is DashBoardActivity){
//            this.check_user_active(loginModel)
//        }
//    }


}