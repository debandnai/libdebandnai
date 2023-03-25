package ie.healthylunch.app.data.viewModel

import android.app.Activity
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.annotation.RequiresApi
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.Navigation
import com.google.gson.JsonObject
import ie.healthylunch.app.R
import ie.healthylunch.app.data.model.passwordResetModel.PasswordresetResponse
import ie.healthylunch.app.data.network.Resource
import ie.healthylunch.app.data.repository.ForgotPasswordRepository
import ie.healthylunch.app.ui.ForgotPasswordActivity
import ie.healthylunch.app.ui.LoginActivity
import ie.healthylunch.app.utils.MethodClass
import ie.healthylunch.app.utils.SingleLiveEvent
import kotlinx.coroutines.launch
import java.util.*

class ResetPasswordViewModel(private val repository: ForgotPasswordRepository) : ViewModel() {
    var email: MutableLiveData<String> = MutableLiveData()

    var newPassword: MutableLiveData<String> = MutableLiveData()
    var confirmPassword: MutableLiveData<String> = MutableLiveData()

    var newPasswordError: MutableLiveData<String> = MutableLiveData()
    var confirmPasswordError: MutableLiveData<String> = MutableLiveData()

    var newPasswordErrorVisible: MutableLiveData<Boolean> = MutableLiveData(false)
    var confirmPasswordErrorVisible: MutableLiveData<Boolean> = MutableLiveData(false)

    var isResetPasswordButtonEnabled: MutableLiveData<Boolean> = MutableLiveData(true)


    //reset password
    private val _resetPasswordResponse: SingleLiveEvent<Resource<PasswordresetResponse>?> =
        SingleLiveEvent()
    private var resetPasswordResponse: LiveData<Resource<PasswordresetResponse>?>? = null
        get() = _resetPasswordResponse

    private fun resetPassword(
        jsonObject: JsonObject
    ) = viewModelScope.launch {
        _resetPasswordResponse.value = repository.ResetPasswordRepository(jsonObject)
    }

    //code



    fun resetPasswordApiCall(activity: Activity) {
        isResetPasswordButtonEnabled.value = false
        MethodClass.showProgressDialog(activity)
        val jsonObject = MethodClass.getCommonJsonObject(activity)
        jsonObject.addProperty("email", email.value)
        jsonObject.addProperty("password", newPassword.value)
        jsonObject.addProperty("confirmpassword", confirmPassword.value)
        resetPassword(jsonObject)
    }


      
    fun getResetPasswordResponse(activity: Activity, view: View) {
        activity as ForgotPasswordActivity
        resetPasswordResponse?.observe(activity) {
            when (it) {
                is Resource.Success -> {
                    MethodClass.hideProgressDialog(activity)
                    isResetPasswordButtonEnabled.value = true
                    val bundle = Bundle()
                    bundle.putString("email", email.value)
                    Navigation.findNavController(view)
                        .navigate(
                            R.id.action_resetPasswordFragment_to_forgotPasswordSuccessFragment,
                            bundle
                        )

                    _resetPasswordResponse.value = null
                    resetPasswordResponse = _resetPasswordResponse

                }
                is Resource.Failure -> {
                    MethodClass.hideProgressDialog(activity)
                    isResetPasswordButtonEnabled.value = true
                    if (it.errorBody != null) {
                        it.errorString?.let { it1 ->
                            MethodClass.showErrorDialog(
                                activity,
                                it1,
                                it.errorCode
                            )
                        }
                    }
                    _resetPasswordResponse.value = null
                    resetPasswordResponse = _resetPasswordResponse
                }
                else -> {
                    MethodClass.hideProgressDialog(activity)
                    isResetPasswordButtonEnabled.value = true
                }
            }

        }

    }

      
    fun resetPasswordClick(activity: Activity) {
        if (validation(activity))
            resetPasswordApiCall(activity)
    }

    private fun validation(activity: Activity): Boolean {
        invisibleErrorTexts()
        if (newPassword.value.isNullOrBlank()) {
            newPasswordError.value = activity.getString(R.string.enter_a_new_password_to_reset)
            newPasswordErrorVisible.value = true
            return false
        }
        if (newPassword.value?.length!! < 6) {
            newPasswordError.value =
                activity.getString(R.string.password_must_be_at_least_6_characters)
            newPasswordErrorVisible.value = true
            return false
        }

        if (confirmPassword.value.isNullOrBlank()) {
            confirmPasswordError.value = activity.getString(R.string.please_enter_confirm_password)
            confirmPasswordErrorVisible.value = true
            return false
        }
        if (confirmPassword.value!!.length < 6) {
            confirmPasswordError.value =
                activity.getString(R.string.password_must_be_at_least_6_characters)
            confirmPasswordErrorVisible.value = true
            return false
        }
        if (!Objects.equals(confirmPassword.value, newPassword.value)) {
            confirmPasswordError.value = activity.getString(R.string.password_did_not_match)
            confirmPasswordErrorVisible.value = true
            return false
        }
        return true
    }

    fun login(activity: Activity) {
        activity.startActivity(Intent(activity, LoginActivity::class.java))
        try {
            LoginActivity.loginActivity.finish()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    //error texts primary state
    fun invisibleErrorTexts() {
        newPasswordErrorVisible.value = false
        confirmPasswordErrorVisible.value = false
    }
}
