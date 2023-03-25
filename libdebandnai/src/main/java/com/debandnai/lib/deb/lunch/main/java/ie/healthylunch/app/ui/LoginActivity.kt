package ie.healthylunch.app.ui

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import ie.healthylunch.app.R
import ie.healthylunch.app.data.model.loginModel.LoginResponse
import ie.healthylunch.app.data.network.ApiInterface
import ie.healthylunch.app.data.network.Resource
import ie.healthylunch.app.data.repository.LoginRepository
import ie.healthylunch.app.data.viewModel.LoginViewModel
import ie.healthylunch.app.databinding.ActivityLoginBinding
import ie.healthylunch.app.ui.base.BaseActivity
import ie.healthylunch.app.utils.AppController
import ie.healthylunch.app.utils.Constants
import ie.healthylunch.app.utils.Constants.Companion.STATUS_TWO
import ie.healthylunch.app.utils.Constants.Companion.STATUS_ZERO
import ie.healthylunch.app.utils.MethodClass
import ie.healthylunch.app.utils.UserPreferences


class LoginActivity : BaseActivity<LoginViewModel, LoginRepository>() {
      
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setUpViewModel()
        window.statusBarColor = ContextCompat.getColor(this@LoginActivity, R.color.sky_bg_2)
        val binding: ActivityLoginBinding =
            DataBindingUtil.setContentView(this, R.layout.activity_login)

        //set the activity of activity variable in layout
        binding.activity = this
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

        //get Login Response
        getLoginResponse()

    }

    private fun getLoginResponse() {
        viewModel.loginResponse?.observe(this) { response->
            when (response) {
                is Resource.Success -> {
                    //clear all push notifications
                    NotificationManagerCompat.from(this).cancelAll()
                    UserPreferences.saveAsObject(this, response.value, Constants.USER_DETAILS)
                    response.value.response.raws.data?.let { userData ->
                        when {
                            userData.isActive == STATUS_TWO -> {
                                startActivity(
                                    Intent(this, RegistrationActivity::class.java)
                                        .putExtra("for", "email_verify")
                                        .putExtra("email", viewModel.userName.value?.trim())
                                )
                            }
                            userData.studentcount == STATUS_ZERO -> {
                                startActivity(
                                    Intent(this, RegistrationActivity::class.java)
                                        .putExtra("for", "add_student")
                                        .putExtra("email", viewModel.userName.value?.trim())
                                )
                            }
                            else -> {
                                startActivity(
                                    Intent(
                                        this,
                                        QuickViewForStudentActivity::class.java
                                    )
                                )
                                finish()
                            }

                        }
                    }
                    viewModel._loginResponse.value = null
                    viewModel.loginResponse = viewModel._loginResponse
                    MethodClass.hideProgressDialog(this)
                }
                is Resource.Failure -> {

                    if (response.errorBody != null) {
                        MethodClass.hideProgressDialog(this)
                        response.errorString?.let { _ ->

                            response.errorString.let { error ->
                                if (viewModel.loginButtonClicked)
                                    MethodClass.showErrorDialog(
                                        this,
                                        error,
                                        response.errorCode
                                    )
                                viewModel.loginButtonClicked = false
                            }


                        }


                    }
                    viewModel._loginResponse.value = null
                    viewModel.loginResponse = viewModel._loginResponse
                }
                else -> {}
            }

        }

    }


    companion object {
        @SuppressLint("StaticFieldLeak")
        lateinit var loginActivity: Activity
    }


    init {
        loginActivity = this
    }


    override fun getViewModel() = LoginViewModel::class.java
    override fun getRepository() =
        LoginRepository(remoteDataSource.buildApi(ApiInterface::class.java))


    override fun onBackPressed() {
        //isTaskRoot which return true if current activity is only activity in your stack
        if (isTaskRoot){
            val intent = Intent(this, LoginRegistrationActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }
        else {
            super.onBackPressed()
        }
    }
}