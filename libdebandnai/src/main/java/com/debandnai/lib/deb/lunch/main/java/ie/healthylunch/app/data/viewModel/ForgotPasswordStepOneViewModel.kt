package ie.healthylunch.app.data.viewModel

import android.app.Activity
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Patterns
import android.view.View
import androidx.annotation.RequiresApi
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.Navigation
import com.google.gson.JsonObject
import ie.healthylunch.app.R
import ie.healthylunch.app.data.model.otpModel.OtpResponse
import ie.healthylunch.app.data.network.Resource
import ie.healthylunch.app.data.repository.ForgotPasswordRepository
import ie.healthylunch.app.ui.ForgotPasswordActivity
import ie.healthylunch.app.ui.LoginActivity
import ie.healthylunch.app.utils.AppController
import ie.healthylunch.app.utils.Constants.Companion.REFRESH_TOKEN
import ie.healthylunch.app.utils.Constants.Companion.SEND_OTP
import ie.healthylunch.app.utils.MethodClass
import ie.healthylunch.app.utils.SingleLiveEvent
import kotlinx.coroutines.launch

class ForgotPasswordStepOneViewModel(private val repository: ForgotPasswordRepository) :
    ViewModel() {
    var email: MutableLiveData<String> = MutableLiveData()
    var emailError: MutableLiveData<String> = MutableLiveData()
    var emailErrorVisible: MutableLiveData<Boolean> = MutableLiveData(false)
    var isSubmitEnabled: MutableLiveData<Boolean> = MutableLiveData(true)


    //send OTP
    val _parentOtpResponse: SingleLiveEvent<Resource<OtpResponse>?> =
        SingleLiveEvent()
    var parentOtpResponse: LiveData<Resource<OtpResponse>?>? = null
        get() = _parentOtpResponse

    fun sendOtpForReg(
        jsonObject: JsonObject
    ) = viewModelScope.launch {
        _parentOtpResponse.value = repository.parentRegistrationOtpRepository(jsonObject)
    }

      





    //error texts primary state
    fun invisibleErrorTexts() {
        emailErrorVisible.value = false
    }

    fun login(activity: Activity) {
        activity.startActivity(Intent(activity, LoginActivity::class.java))
        try {
            LoginActivity.loginActivity.finish()
        } catch (e: Exception) {
            e.printStackTrace()

        }
        activity.finish()
    }






      

}