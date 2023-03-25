package ie.healthylunch.app.ui.base

import android.content.Intent
import android.content.IntentFilter
import android.net.Uri
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.dynamiclinks.ktx.dynamicLinks
import com.google.firebase.ktx.Firebase
import ie.healthylunch.app.data.model.isLoginModel.IsLogin
import ie.healthylunch.app.data.network.RemoteDataSource
import ie.healthylunch.app.data.repository.BaseRepository
import ie.healthylunch.app.ui.*
import ie.healthylunch.app.utils.Constants
import ie.healthylunch.app.utils.NoNetworkReceiver
import ie.healthylunch.app.utils.NetworkConnectionListener
import ie.healthylunch.app.utils.UserPreferences

abstract class BaseActivity<VM : ViewModel, R : BaseRepository> :
    AppCompatActivity(), NetworkConnectionListener {

    protected val remoteDataSource = RemoteDataSource()
    lateinit var viewModel: VM
    private lateinit var myReceiver: NoNetworkReceiver


    abstract fun getViewModel(): Class<VM>

    abstract fun getRepository(): R

    fun setUpViewModel() {
        val factory = ViewModelFactory(getRepository())
        viewModel = ViewModelProvider(this, factory)[getViewModel()]

        //initialize broadcast receiver on onCreate method of any activity. Because this method is called in onCreate
        myReceiver = NoNetworkReceiver(this)

    }


    override fun onStart() {
        super.onStart()
        registerReceiver(myReceiver, IntentFilter("android.net.conn.CONNECTIVITY_CHANGE"))
        Firebase.dynamicLinks
            .getDynamicLink(intent)
            .addOnSuccessListener(this) { pendingDynamicLinkData ->
                // Get deep link from result (may be null if no link is found)
                if (pendingDynamicLinkData != null) {
                    val deepLink = pendingDynamicLinkData.link

                    Log.e("deepLink", deepLink.toString())
                    val parameters = deepLink?.pathSegments
                    val lastParameter = parameters?.get(parameters.size - 1) ?: "none"
                    Log.e("deepLink: ", lastParameter)

                    val isLogin: IsLogin? = UserPreferences.getAsObject<IsLogin>(
                        this,
                        Constants.LOGIN_CHECK
                    )
                    if (isLogin?.isLogin == 1) {
                        if (lastParameter.equals("add_student", true)) {
                            val intent = Intent().apply {
                                data = Uri.parse("lunchbag://lunchbag.com/add_student")
                            }
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                            startActivity(intent)
                        } else if (lastParameter.equals("wallet", true)) {
                            val intent = Intent(this, WalletViewActivity::class.java)
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                            startActivity(intent)
                        } else if (lastParameter.equals("add_update_order", true)) {
                            val intent = Intent(this, DashBoardActivity::class.java)
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                            startActivity(intent)
                        } else if (lastParameter.equals("dashboard", true)) {
                            val intent = Intent(this, DashBoardActivity::class.java)
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                            startActivity(intent)
                        } else if (lastParameter.equals("notification", true)) {
                            val intent = Intent(this, NotificationActivity::class.java)
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                            startActivity(intent)
                        } else {
                            val intent = Intent(this, DashBoardActivity::class.java)
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                            startActivity(intent)
                        }


                    } else {
                        if (lastParameter.equals("parent_registration", true)) {
                            val intent = Intent(this, RegistrationActivity::class.java)
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                            startActivity(intent)
                        } else if (lastParameter.equals("login", true)) {
                            val intent = Intent(this, LoginActivity::class.java)
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                            startActivity(intent)
                        }
                    }
                }


            }
            .addOnFailureListener(this) { e -> Log.w("TAG", "getDynamicLink:onFailure", e) }


    }

    override fun onStop() {
        super.onStop()
        unregisterReceiver(myReceiver)
    }

    //on Network connection implement method
    override fun onNetworkConnection(notConnected: Boolean) {

        if (notConnected)
            startActivity(Intent(this, NetworkActivity::class.java))
        else {
            when (this) {


            }

        }

    }


    @Suppress("DEPRECATION")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == Constants.APP_UPDATE_REQUEST) {
            if (resultCode != RESULT_OK) {
                Log.e("MY_APP", "Update flow failed! Result code: $resultCode")
                // If the update is cancelled or fails,
                // you can request to start the update again.
            }
        }
    }
}