package ie.healthylunch.app.ui

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.dynamiclinks.ktx.dynamicLinks
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.FirebaseMessaging
import ie.healthylunch.app.R
import ie.healthylunch.app.data.model.isLoginModel.IsLogin
import ie.healthylunch.app.utils.AppController
import ie.healthylunch.app.utils.Constants
import ie.healthylunch.app.utils.Constants.Companion.CAMPAIGN_ID
import ie.healthylunch.app.utils.Constants.Companion.FROM
import ie.healthylunch.app.utils.Constants.Companion.NOTIFICATION
import ie.healthylunch.app.utils.Constants.Companion.NOTIFICATION_TYPE
import ie.healthylunch.app.utils.MethodClass
import ie.healthylunch.app.utils.UserPreferences
import java.util.*

@SuppressLint("CustomSplashScreen")
class SplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        //setTheme(R.style.SplashTheme)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        window.statusBarColor = ContextCompat.getColor(this@SplashActivity, R.color.sky_bg_1)
        // make isDeActivatedAllStudents = false for ensuring that isDeActivatedAllStudents will not be checked in logout method  in AppController class
        AppController.getInstance().isDeActivatedAllStudents = false

        FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
            if (!task.isSuccessful) {
                Log.w("TAG", "Fetching FCM registration token failed", task.exception)
                return@OnCompleteListener
            }

            // Get new FCM registration token
            val token = task.result
            Log.e("firebase_token", token)

            UserPreferences.saveFirebaseToken(this, token)


        })


        if (intent.extras != null && Objects.equals(
                intent.getStringExtra(FROM),
                NOTIFICATION
            )
        ) {
            val isLogin: IsLogin? = UserPreferences.getAsObject<IsLogin>(
                this,
                Constants.LOGIN_CHECK
            )
            if (isLogin?.isLogin == 1) {
                val notificationType = intent.getStringExtra(NOTIFICATION_TYPE).toString()
                val campaignId = intent.getStringExtra(CAMPAIGN_ID).toString()
                when {
                    notificationType.equals("W", ignoreCase = true) or
                            notificationType.equals("B", ignoreCase = true) or
                            notificationType.equals("A", ignoreCase = true) or
                            notificationType.equals("WR", ignoreCase = true) -> {
                        val activityIntent = Intent(this, WalletViewActivity::class.java)
                        startActivity(activityIntent)
                        intent = null
                        finish()

                    }
                    /*notificationType.equals("OR", ignoreCase = true) or
                            notificationType.equals("CR", ignoreCase = true) -> {
                        val activityIntent = Intent(this, DashBoardActivity::class.java)
                        activityIntent.putExtra("from", intent.getStringExtra("from"))
                        startActivity(activityIntent)
                        intent = null
                        finish()
                    }*/
                    else -> {
                        val activityIntent = Intent(this, NotificationActivity::class.java)
                        activityIntent.putExtra(
                            CAMPAIGN_ID,
                            campaignId
                        )
                        startActivity(activityIntent)
                        intent = null
                        finish()
                    }

                }
            } else {
                startActivity(Intent(this, LoginRegistrationActivity::class.java))
                finish()
            }

        } else
            if (MethodClass.isGooglePlayServicesAvailable(this))
                checkFireBaseDynamicLink()


    }

    private fun startActivity() {
        val isLogin: IsLogin? = UserPreferences.getAsObject<IsLogin>(
            this,
            Constants.LOGIN_CHECK
        )
        if (isLogin?.isLogin == 1) {
            val intent = Intent(this, QuickViewForStudentActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
            finish()
        } else {
            val intent = Intent(this, LoginRegistrationActivity::class.java)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
            finish()
        }
    }

    private fun checkFireBaseDynamicLink() {
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
                        } else {
                            startActivity()
                        }
                    }
                } else {
                    Handler(Looper.getMainLooper()).postDelayed({
                        startActivity()
                    }, 2000)
                }


            }
            .addOnFailureListener(this) { e ->
                Log.w("TAG", "getDynamicLink:onFailure", e)
                Handler(Looper.getMainLooper()).postDelayed({
                    startActivity()
                }, 2000)
            }

    }

}