package ie.healthylunch.app.data.viewModel

import android.annotation.SuppressLint
import android.app.Activity
import android.os.Bundle
import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.Navigation
import com.google.gson.JsonObject
import ie.healthylunch.app.R
import ie.healthylunch.app.data.model.loginModel.LoginResponse
import ie.healthylunch.app.data.model.otpModel.OtpResponse
import ie.healthylunch.app.data.network.Resource
import ie.healthylunch.app.data.repository.ParentRegistrationRepository
import ie.healthylunch.app.ui.RegistrationActivity
import ie.healthylunch.app.utils.*
import kotlinx.coroutines.launch
import java.util.*

class ParentRegistrationStepTwoViewModel(private val repository: ParentRegistrationRepository) :
    ViewModel() {
    var email: MutableLiveData<String> = MutableLiveData("")
    var password: MutableLiveData<String> = MutableLiveData()
    var confirmPassword: MutableLiveData<String> = MutableLiveData()
    private var promotionAlert: MutableLiveData<String> = MutableLiveData()
    var firstName: MutableLiveData<String> = MutableLiveData()
    var lastName: MutableLiveData<String> = MutableLiveData()
    var phNumber: MutableLiveData<String> = MutableLiveData()

    var passwordError: MutableLiveData<String> = MutableLiveData()
    var confirmPasswordError: MutableLiveData<String> = MutableLiveData()
    var promotionAlertError: MutableLiveData<String> = MutableLiveData()

    var passwordErrorVisible: MutableLiveData<Boolean> = MutableLiveData(false)
    var confirmPasswordErrorVisible: MutableLiveData<Boolean> = MutableLiveData(false)
    var promotionAlertErrorVisible: MutableLiveData<Boolean> = MutableLiveData(false)
    var isPromotionYes: MutableLiveData<Boolean> = MutableLiveData(false)
    var isPromotionNo: MutableLiveData<Boolean> = MutableLiveData(false)
    val bundle = Bundle()
    var isSubmitEnabled: MutableLiveData<Boolean> = MutableLiveData(true)
    var isBackEnabled: MutableLiveData<Boolean> = MutableLiveData(true)

    //parent reg.
    private val _parentRegistrationResponse: SingleLiveEvent<Resource<LoginResponse>?> =
        SingleLiveEvent()
    private var parentRegistrationResponse: LiveData<Resource<LoginResponse>?>? = null
        get() = _parentRegistrationResponse

    private fun parentRegistration(
        jsonObject: JsonObject
    ) = viewModelScope.launch {
        _parentRegistrationResponse.value = repository.parentRegistration(jsonObject)
    }

    //send OTP
    private val _parentOtpResponse: SingleLiveEvent<Resource<OtpResponse>?> =
        SingleLiveEvent()
    private var parentOtpResponse: LiveData<Resource<OtpResponse>?>? = null
        get() = _parentOtpResponse

    private fun sendOtpForReg(
        jsonObject: JsonObject
    ) = viewModelScope.launch {
        _parentOtpResponse.value = repository.parentRegistrationOtp(jsonObject)
    }

    @SuppressLint("RestrictedApi")
    fun getStarted(activity: Activity, view: View) {
        activity as RegistrationActivity
        if (registrationValidation(activity)) {
            isSubmitEnabled.value = false

            val jsonObject = MethodClass.getCommonJsonObject(activity)
            jsonObject.addProperty("password", password.value)
            jsonObject.addProperty("firstname", firstName.value)
            jsonObject.addProperty("phone", phNumber.value)
            jsonObject.addProperty("lastname", lastName.value)
            jsonObject.addProperty("email", email.value)
            jsonObject.addProperty("username", email.value)
            jsonObject.addProperty("gdpr_value", promotionAlert.value)
            MethodClass.showProgressDialog(activity)
            parentRegistration(jsonObject)

        }

    }

    fun getParentRegistrationResponse(activity: Activity, view: View) {
        activity as RegistrationActivity
        parentRegistrationResponse?.observe(activity) {

            when (it) {
                is Resource.Success -> {
                    UserPreferences.saveAsObject(
                        activity,
                        it.value,
                        Constants.USER_DETAILS
                    )

                    bundle.putString("email", email.value)
                    bundle.putBoolean("check", false)
                    sendOtp(activity, view)
                    _parentRegistrationResponse.value = null
                    parentRegistrationResponse = _parentRegistrationResponse


                }
                is Resource.Failure -> {
                    if (it.errorBody != null) {
                        it.errorString?.let { it1 ->
                            MethodClass.showErrorDialog(
                                activity,
                                it1,
                                it.errorCode
                            )

                        }


                    }
                    MethodClass.hideProgressDialog(activity)
                    _parentRegistrationResponse.value = null
                    parentRegistrationResponse = _parentRegistrationResponse
                }
                else -> {}
            }

        }

    }


    private fun sendOtp(activity: RegistrationActivity, view: View) {

        val jsonObject = MethodClass.getCommonJsonObject(activity)
        jsonObject.addProperty("email", email.value)
        jsonObject.addProperty("otp_purpose", "2")
        sendOtpForReg(jsonObject)
        parentOtpResponse?.observe(activity) {

            when (it) {

                is Resource.Success -> {
                    MethodClass.hideProgressDialog(activity)
                    Navigation.findNavController(view)
                        .navigate(
                            R.id.action_parentRegistrationStepTwoFragment_to_registrationOtpFragment,
                            bundle
                        )
                    isSubmitEnabled.value = true
                    _parentOtpResponse.value = null
                    parentOtpResponse = _parentOtpResponse
                }
                is Resource.Failure -> {
                    if (it.errorBody != null) {
                        MethodClass.hideProgressDialog(activity)
                        it.errorString?.let { it1 ->
                            MethodClass.showErrorDialog(
                                activity,
                                it1,
                                it.errorCode
                            )
                        }


                    }
                    isSubmitEnabled.value = true
                    _parentOtpResponse.value = null
                    parentOtpResponse = _parentOtpResponse
                }
                else -> {}
            }

        }
    }


    fun infoClick(activity: Activity) {
        CustomDialog.showInfoTypeDialog(activity)

    }


    fun promotionOnClick(promotionAlertInt: Int) {
        invisibleErrorTexts()
        if (promotionAlertInt == 1) {
            promotionAlert.value = "y"
            isPromotionYes.value = true
            isPromotionNo.value = false
        } else {
            promotionAlert.value = "n"
            isPromotionYes.value = false
            isPromotionNo.value = true
        }

    }

    fun back(view: View) {
        isBackEnabled.value = false
        Navigation.findNavController(view)
            .navigate(
                R.id.action_parentRegistrationStepTwoFragment_to_parentRegistrationStepOneFragment,
                bundle
            )
    }

    //validation
    private fun registrationValidation(activity: Activity): Boolean {

        invisibleErrorTexts()

        if (password.value.isNullOrBlank()) {
            passwordError.value = activity.getString(R.string.please_enter_password)
            passwordErrorVisible.value = true
            return false
        }
        if (password.value!!.length < 6) {
            passwordError.value =
                activity.getString(R.string.password_must_be_at_least_6_characters)
            passwordErrorVisible.value = true
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
        if (!Objects.equals(confirmPassword.value, password.value)) {
            confirmPasswordError.value = activity.getString(R.string.password_did_not_match)
            confirmPasswordErrorVisible.value = true
            return false
        }
        if (promotionAlert.value.isNullOrBlank()) {
            promotionAlertError.value = activity.getString(R.string.please_select_promotion_alert)
            promotionAlertErrorVisible.value = true
            return false
        }
        return true
    }

    //error texts primary state
    fun invisibleErrorTexts() {
        passwordErrorVisible.value = false
        confirmPasswordErrorVisible.value = false
        promotionAlertErrorVisible.value = false
    }


}