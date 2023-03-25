package ie.healthylunch.app.data.viewModel

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import android.view.View
import androidx.core.app.NotificationManagerCompat
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.JsonObject
import ie.healthylunch.app.R
import ie.healthylunch.app.data.model.loginModel.LoginResponse
import ie.healthylunch.app.data.network.Resource
import ie.healthylunch.app.data.repository.LoginRepository
import ie.healthylunch.app.ui.*
import ie.healthylunch.app.ui.RegistrationActivity.Companion.registrationActivity
import ie.healthylunch.app.utils.*
import ie.healthylunch.app.utils.Constants.Companion.USER_DETAILS
import kotlinx.coroutines.launch

class LoginViewModel(private val repository: LoginRepository) : ViewModel() {
    var userName: MutableLiveData<String> = MutableLiveData()
    var password: MutableLiveData<String> = MutableLiveData()
    var usernameError: MutableLiveData<String> = MutableLiveData()
    var passwordError: MutableLiveData<String> = MutableLiveData()
    var userNameErrorVisible: MutableLiveData<Boolean> = MutableLiveData(false)
    var passwordErrorVisible: MutableLiveData<Boolean> = MutableLiveData(false)
    var loginButtonClicked: Boolean = true
    val bundle = Bundle()

    //Authenticate
    val _loginResponse: SingleLiveEvent<Resource<LoginResponse>?> =
        SingleLiveEvent()
    var loginResponse: LiveData<Resource<LoginResponse>?>? = null
        get() = _loginResponse

    fun login(
        jsonObject: JsonObject
    ) = viewModelScope.launch {
        _loginResponse.value = repository.login(jsonObject)
    }

    fun login(activity: Activity, view: View) {
        if (loginValidation(view)) {
            activity as LoginActivity

            MethodClass.showProgressDialog(activity)
            val jsonObject = MethodClass.getCommonJsonObject(activity)
            jsonObject.addProperty("username", userName.value?.trim())
            jsonObject.addProperty("password", password.value)
            loginButtonClicked = true
            login(jsonObject)
        }

    }



    fun forgotPassword(view: View) {

        view.context.startActivity(Intent(view.context, ForgotPasswordActivity::class.java))

    }

    fun registration(view: View) {

        view.context.startActivity(Intent(view.context, RegistrationActivity::class.java))
        try {
            registrationActivity.finish()
        } catch (e: Exception) {
            e.printStackTrace()
        }


    }


    //validation
    private fun loginValidation(view: View): Boolean {

        invisibleErrorTexts()

        if (userName.value?.trim().isNullOrBlank()) {
            usernameError.value = view.context.getString(R.string.please_enter_username)
            userNameErrorVisible.value = true
            return false
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(userName.value?.trim()!!).matches()) {
            usernameError.value = view.context.getString(R.string.please_enter_valid_email_address)
            userNameErrorVisible.value = true
            return false
        }
        if (password.value.isNullOrBlank()) {
            passwordError.value = view.context.getString(R.string.please_enter_password)
            passwordErrorVisible.value = true
            return false
        }
        /*if (password.value!!.length < 6) {
            passwordError.value =
                view.context.getString(R.string.password_must_be_at_least_6_characters)
            passwordErrorVisible.value = true
            return false
        }*/
        return true
    }

    //error texts primary state
    fun invisibleErrorTexts() {
        userNameErrorVisible.value = false
        passwordErrorVisible.value = false
    }

}