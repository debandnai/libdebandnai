package ie.healthylunch.app.data.viewModel

import android.app.Activity
import android.content.Intent
import android.view.View
import androidx.lifecycle.*
import com.google.gson.JsonObject
import ie.healthylunch.app.data.model.isLoginModel.IsLogin
import ie.healthylunch.app.data.model.loginModel.LoginResponse
import ie.healthylunch.app.data.model.logoutModel.LogoutResponse
import ie.healthylunch.app.data.network.Resource
import ie.healthylunch.app.data.repository.LoginRegistrationRepository
import ie.healthylunch.app.ui.LoginActivity
import ie.healthylunch.app.ui.LoginRegistrationActivity
import ie.healthylunch.app.ui.RegistrationActivity
import ie.healthylunch.app.utils.Constants
import ie.healthylunch.app.utils.Constants.Companion.REFRESH_TOKEN
import ie.healthylunch.app.utils.Constants.Companion.TOKEN
import ie.healthylunch.app.utils.MethodClass
import ie.healthylunch.app.utils.UserPreferences
import kotlinx.coroutines.launch

class LoginRegistrationViewModel(private val repository: LoginRegistrationRepository) :
    ViewModel() {

    // Logout
    private val _logOutResponse: MutableLiveData<Resource<LogoutResponse>> =
        MutableLiveData()
    private val logOutResponse: LiveData<Resource<LogoutResponse>>
        get() = _logOutResponse

    fun logout(
        jsonObject: JsonObject,
        token: String
    ) = viewModelScope.launch {
        _logOutResponse.value = repository.logout(jsonObject, token)
    }


    fun logoutApiCall(activity: Activity) {
        MethodClass.showProgressDialog(activity)
        val loginResponse: LoginResponse? =
            UserPreferences.getAsObject<LoginResponse>(
                activity,
                Constants.USER_DETAILS
            )

        TOKEN = loginResponse?.response?.raws?.data?.token.toString()
        REFRESH_TOKEN = loginResponse?.response?.raws?.data?.refreshToken.toString()

        val jsonObject = MethodClass.getCommonJsonObject(activity)
        logout(
            jsonObject,
            TOKEN
        )
    }


    fun getLogoutResponse(activity: Activity) {
        activity as LoginRegistrationActivity
        logOutResponse.observe(activity) {
            when (it) {
                is Resource.Success -> {
                    UserPreferences.saveAsObject(activity, IsLogin(0), Constants.LOGIN_CHECK)
                    UserPreferences.saveAsObject(activity, null, Constants.USER_DETAILS)
                    activity.callPlayStoreAppUpdateFun()
                    MethodClass.hideProgressDialog(activity)

                }
                is Resource.Failure -> {
                     if (it.errorCode == 307 || it.errorCode == 426) {
                         it.errorString?.let { it1 ->
                             MethodClass.showErrorDialog(
                                 activity,
                                 it1, it.errorCode
                             )
                         }
                         return@observe
                     }
                    UserPreferences.saveAsObject(activity, IsLogin(0), Constants.LOGIN_CHECK)
                    UserPreferences.saveAsObject(activity, null, Constants.USER_DETAILS)
                    activity.callPlayStoreAppUpdateFun()
                    MethodClass.hideProgressDialog(activity)

                }
            }
        }
    }


    fun loginClick(view: View) {

        view.context.startActivity(Intent(view.context, LoginActivity::class.java))
    }

    fun registerClick(view: View) {

        view.context.startActivity(Intent(view.context, RegistrationActivity::class.java))

    }
}