package com.salonsolution.app.ui.activity

import android.annotation.SuppressLint
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.lifecycleScope
import com.salonsolution.app.BuildConfig
import com.salonsolution.app.R
import com.salonsolution.app.data.preferences.AppSettingsPref
import com.salonsolution.app.data.preferences.UserSharedPref
import com.salonsolution.app.databinding.ActivitySplashBinding
import com.salonsolution.app.utils.Constants
import com.salonsolution.app.utils.ContextUtils
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.*
import javax.inject.Inject

@SuppressLint("CustomSplashScreen")
@AndroidEntryPoint
class SplashActivity : AppCompatActivity() {
    private val delayTime: Long = 2000L
    private lateinit var binding: ActivitySplashBinding

    @Inject
    lateinit var appSettingsPref: AppSettingsPref
    @Inject
    lateinit var userSharedPref: UserSharedPref
//    private lateinit var appSettingsPref: AppSettingsPref
    private var from :String? = ""
    private var notificationType :String? = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this,R.layout.activity_splash)
        binding.tvVersion.text = getString(R.string.version,BuildConfig.VERSION_NAME)
        getNotificationData()
        gotoNextScreen()
    }

    private fun getNotificationData() {
        intent?.let {
            if (it.hasExtra(Constants.FROM) || it.hasExtra(Constants.NOTIFICATION_TYPE)) {
                handleNotificationData(it)
            }
        }
    }

//    override fun attachBaseContext(context: Context) {
        // get chosen language from shared preference
//        appSettingsPref = AppSettingsPref(context)
//        val localeCode = appSettingsPref.getLanguage()
//        val localeToSwitchTo = Locale(localeCode)
//        val localeUpdatedContext: ContextWrapper = ContextUtils.updateLocale(context, localeToSwitchTo)
//        super.attachBaseContext(localeUpdatedContext)
//    }

    private fun gotoNextScreen() {
        lifecycleScope.launch {
            delay(delayTime)
            Log.d("token","token: ${userSharedPref.getUserToken()}")
            Log.d("token","FCM token: ${appSettingsPref.getFCMToken()}")
            Log.d("tag","isActive: ${userSharedPref.getIsActive()}")
            if (userSharedPref.getUserToken() !== "" && userSharedPref.getIsActive()==1) {
                val intent = Intent(this@SplashActivity, DashboardActivity::class.java)
                intent.putExtra(Constants.FROM,from)
                intent.putExtra(Constants.NOTIFICATION_TYPE,notificationType)
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                startActivity(intent)
                finish()
            } else {
                val intent =
                    Intent(this@SplashActivity, AuthActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                startActivity(intent)
                finish()
            }

        }
    }

    private fun handleNotificationData(intent: Intent) {
       Log.d("tag","Order Id: ${intent.getStringExtra(Constants.NOTIFICATION_TYPE)}")
        from = intent.getStringExtra(Constants.FROM)
        notificationType = intent.getStringExtra(Constants.NOTIFICATION_TYPE)

    }
}