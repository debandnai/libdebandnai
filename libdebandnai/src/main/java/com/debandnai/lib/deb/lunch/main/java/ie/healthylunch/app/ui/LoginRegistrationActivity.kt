package ie.healthylunch.app.ui

import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.widget.AppCompatButton
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.install.model.ActivityResult
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.UpdateAvailability
import ie.healthylunch.app.BuildConfig
import ie.healthylunch.app.R
import ie.healthylunch.app.data.model.isLoginModel.IsLogin
import ie.healthylunch.app.data.model.loginModel.LoginResponse
import ie.healthylunch.app.data.network.ApiInterface
import ie.healthylunch.app.data.repository.LoginRegistrationRepository
import ie.healthylunch.app.data.viewModel.LoginRegistrationViewModel
import ie.healthylunch.app.databinding.ActivityLoginRegistrationBinding
import ie.healthylunch.app.ui.base.BaseActivity
import ie.healthylunch.app.utils.AppController
import ie.healthylunch.app.utils.Constants
import ie.healthylunch.app.utils.Constants.Companion.APP_UPDATE_REQUEST
import ie.healthylunch.app.utils.Constants.Companion.LOGIN_CHECK
import ie.healthylunch.app.utils.MethodClass
import ie.healthylunch.app.utils.UserPreferences
import org.json.JSONException
import org.json.JSONObject
import java.util.*

class LoginRegistrationActivity :
    BaseActivity<LoginRegistrationViewModel, LoginRegistrationRepository>() {
    private var errorString: String = ""
    private lateinit var appUpdateDialog: Dialog

      
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setUpViewModel()
        window.statusBarColor = ContextCompat.getColor(this@LoginRegistrationActivity, R.color.sky_bg_2)
        val binding: ActivityLoginRegistrationBinding =
            DataBindingUtil.setContentView(this, R.layout.activity_login_registration)

        binding.viewModel = viewModel
        binding.lifecycleOwner = this

        AppController.getInstance().isDeActivatedAllStudents = false
        //check if previously logged in then logged out silently
        val loginResponse: LoginResponse? =
            UserPreferences.getAsObject<LoginResponse>(
                this,
                Constants.USER_DETAILS
            )
        loginResponse?.response?.raws?.data?.token?.let {
            AppController.getInstance()
                .logoutAppController(
                    this, false
                )
        }


        //get Logout Response
        viewModel.getLogoutResponse(this)

        if (intent.extras != null && Objects.equals(
                intent.getStringExtra("from"),
                "appUpdate"
            )
        ) {
            errorString = intent.getStringExtra("errorString").toString()
            showAppUpdateDialog(this, errorString)
        }
    }

    override fun getViewModel() = LoginRegistrationViewModel::class.java

    override fun getRepository() =
        LoginRegistrationRepository(remoteDataSource.buildApi(ApiInterface::class.java))


    fun callPlayStoreAppUpdateFun() {
        val appUpdateManager = AppUpdateManagerFactory.create(this)
        val appUpdateInfoTask = appUpdateManager.appUpdateInfo

        appUpdateInfoTask.addOnSuccessListener { it1 ->
            if (it1.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE && it1.isUpdateTypeAllowed(
                    AppUpdateType.IMMEDIATE
                )
            ) {
                appUpdateManager.startUpdateFlowForResult(
                    // Pass the intent that is returned by 'getAppUpdateInfo()'.
                    it1,
                    // Or 'AppUpdateType.FLEXIBLE' for flexible updates.
                    AppUpdateType.IMMEDIATE,
                    // The current activity making the update request.
                    this,
                    // Include a request code to later monitor this update request.
                    APP_UPDATE_REQUEST
                )
                appUpdateDialog.dismiss()
            } else {
                    startActivity(
                        Intent(
                            Intent.ACTION_VIEW, Uri.parse(
                                "https://play.google.com/store/apps/details?id=$packageName"
                            )
                        )
                    )

            }
        }

        appUpdateManager.appUpdateInfo.addOnFailureListener {
                startActivity(
                    Intent(
                        Intent.ACTION_VIEW, Uri.parse(
                            "https://play.google.com/store/apps/details?id=$packageName"
                        )
                    )
                )

        }

    }

    private fun showAppUpdateDialog(activity: Activity, errorString: String) {
        appUpdateDialog = Dialog(activity)
        appUpdateDialog.window?.setBackgroundDrawableResource(R.color.transparent)

        appUpdateDialog.setCancelable(false)
        //Fullscreen
        appUpdateDialog.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )
        appUpdateDialog.setContentView(R.layout.app_update_popup)

        val versionNameTv = appUpdateDialog.findViewById<TextView>(R.id.versionNameTv)
        val versionMessageTv =
            appUpdateDialog.findViewById<TextView>(R.id.versionMessageTv)
        val updateBtn = appUpdateDialog.findViewById<AppCompatButton>(R.id.updateBtn)

        versionNameTv.text = BuildConfig.VERSION_NAME
        try {
            val jsonObject = JSONObject(errorString)

            versionMessageTv.text =
                jsonObject.getJSONObject("response").getJSONObject("status").getString("msg")
                    ?: activity.resources.getString(R.string.update_msg)

        } catch (e: JSONException) {
            e.printStackTrace()
            versionMessageTv.text = activity.resources.getString(R.string.update_msg)

        }

        updateBtn.setOnClickListener {

            val isLogin: IsLogin? = UserPreferences.getAsObject<IsLogin>(
                this,
                LOGIN_CHECK
            )

            if (isLogin?.isLogin == 1)
                viewModel.logoutApiCall(this)
            else
                callPlayStoreAppUpdateFun()
        }
        appUpdateDialog.show()
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            APP_UPDATE_REQUEST -> when (resultCode) {
                RESULT_OK -> {}
                RESULT_CANCELED -> {
                    Toast.makeText(
                        this,
                        "You can not use the app without updating it",
                        Toast.LENGTH_SHORT
                    ).show()
                    showAppUpdateDialog(this, errorString)
                }
                ActivityResult.RESULT_IN_APP_UPDATE_FAILED -> callPlayStoreAppUpdateFun()
            }
        }
    }

    // Checks that the update is not stalled during 'onResume()'.
    // However, you should execute this check at all entry points into the app.
    override fun onResume() {
        super.onResume()
        //clear all push notifications
        //NotificationManagerCompat.from(this).cancelAll()

        val appUpdateManager = AppUpdateManagerFactory.create(this)
        appUpdateManager
            .appUpdateInfo
            .addOnSuccessListener { appUpdateInfo ->
                if (appUpdateInfo.updateAvailability()
                    == UpdateAvailability.DEVELOPER_TRIGGERED_UPDATE_IN_PROGRESS
                ) {
                    // If an in-app update is already running, resume the update.
                    appUpdateManager.startUpdateFlowForResult(
                        appUpdateInfo,
                        AppUpdateType.IMMEDIATE,
                        this,
                        APP_UPDATE_REQUEST
                    )
                }
            }
    }

}