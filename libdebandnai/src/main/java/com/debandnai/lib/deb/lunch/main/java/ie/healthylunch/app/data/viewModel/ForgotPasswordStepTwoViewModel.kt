package ie.healthylunch.app.data.viewModel

import android.annotation.SuppressLint
import android.app.Activity
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
import ie.healthylunch.app.data.model.otpModel.OtpResponse
import ie.healthylunch.app.data.model.parentEmailVerificationModel.ParentemailverificationResponse
import ie.healthylunch.app.data.network.Resource
import ie.healthylunch.app.data.repository.ForgotPasswordRepository
import ie.healthylunch.app.ui.ForgotPasswordActivity
import ie.healthylunch.app.utils.AppController
import ie.healthylunch.app.utils.Constants.Companion.PARENT_EMAIL_VERIFICATION
import ie.healthylunch.app.utils.Constants.Companion.REFRESH_TOKEN
import ie.healthylunch.app.utils.Constants.Companion.SEND_OTP
import ie.healthylunch.app.utils.CustomDialog
import ie.healthylunch.app.utils.MethodClass
import ie.healthylunch.app.utils.SingleLiveEvent
import kotlinx.coroutines.launch

class ForgotPasswordStepTwoViewModel(private val repository: ForgotPasswordRepository) :
    ViewModel() {
    var email: MutableLiveData<String> = MutableLiveData("")
    var otp: MutableLiveData<String> = MutableLiveData()
    var otpError: MutableLiveData<String> = MutableLiveData()
    var otpErrorVisible: MutableLiveData<Boolean> = MutableLiveData(false)
    var isSubmitEnabled: MutableLiveData<Boolean> = MutableLiveData(true)
    val bundle = Bundle()

    @SuppressLint("StaticFieldLeak")
    lateinit var view: View

    //Otp verification
    private val _parentEmailVerificationResponse: SingleLiveEvent<Resource<ParentemailverificationResponse>?> =
        SingleLiveEvent()
    private var parentEmailVerificationResponse: LiveData<Resource<ParentemailverificationResponse>?>? =
        null
        get() = _parentEmailVerificationResponse

    private fun verifyOTP(
        jsonObject: JsonObject
    ) = viewModelScope.launch {
        _parentEmailVerificationResponse.value =
            repository.parentemailverificatiRepository(jsonObject)
    }

    //send OTP
    private val _parentOtpResponse: SingleLiveEvent<Resource<OtpResponse>?> =
        SingleLiveEvent()
    private var parentOtpResponse: LiveData<Resource<OtpResponse>?>? = null
        get() = _parentOtpResponse

    private fun sendOtpForReg(
        jsonObject: JsonObject
    ) = viewModelScope.launch {
        _parentOtpResponse.value = repository.parentRegistrationOtpRepository(jsonObject)
    }


    fun getStarted(activity: Activity, view: View) {
        activity as ForgotPasswordActivity
        if (otpValidation(activity)) {
            isSubmitEnabled.value = false
            verifyOtpApiCall(activity)
        }
    }

    fun verifyOtpApiCall(activity: Activity) {
        val jsonObject = MethodClass.getCommonJsonObject(activity)
        jsonObject.addProperty("onetimepassword", otp.value)
        jsonObject.addProperty("email", email.value)
        jsonObject.addProperty("otp_purpose", 1)

        MethodClass.showProgressDialog(activity)
        verifyOTP(jsonObject)
    }

    @RequiresApi(Build.VERSION_CODES.P)
    fun getVerifyOtpResponse(activity: Activity, view: View) {
        activity as ForgotPasswordActivity
        parentEmailVerificationResponse?.observe(activity) {
            when (it) {
                is Resource.Success -> {
                    bundle.putString("email", email.value)
                    Navigation.findNavController(view)
                        .navigate(
                            R.id.action_forgotPasswordStepTwoFragment_to_resetPasswordFragment,
                            bundle
                        )
                    isSubmitEnabled.value = true
                    MethodClass.hideProgressDialog(activity)
                    _parentEmailVerificationResponse.value = null
                    parentEmailVerificationResponse = _parentEmailVerificationResponse
                }
                is Resource.Failure -> {

                    if (it.errorBody != null) {

                        MethodClass.hideProgressDialog(activity)
                        it.errorString?.let { it1 ->
                            if (it.errorCode == 401)
                                AppController.getInstance()
                                    .refreshTokenResponse(
                                        activity,
                                        null,
                                        PARENT_EMAIL_VERIFICATION,
                                        REFRESH_TOKEN
                                    )
                            else {
                                MethodClass.showErrorDialog(
                                    activity,
                                    it1,
                                    it.errorCode
                                )

                            }
                        }


                    }
                    isSubmitEnabled.value = true
                    _parentEmailVerificationResponse.value = null
                    parentEmailVerificationResponse = _parentEmailVerificationResponse
                }
                else -> {}
            }

        }

    }

    fun info(activity: Activity) {
        CustomDialog.showInfoTypeDialog(activity)
    }

    //resend OTP
    fun reSendOtp(activity: Activity) {
        activity as ForgotPasswordActivity
        val jsonObject = MethodClass.getCommonJsonObject(activity)
        jsonObject.addProperty("email", email.value)
        jsonObject.addProperty("otp_purpose", 1)
        MethodClass.showProgressDialog(activity)
        sendOtpForReg(jsonObject)

    }

    @RequiresApi(Build.VERSION_CODES.P)
    fun getResendOtpResponse(activity: Activity) {
        activity as ForgotPasswordActivity
        parentOtpResponse?.observe(activity) {

            when (it) {

                is Resource.Success -> {

                    MethodClass.hideProgressDialog(activity)
                    CustomDialog.showInfoTypeDialogWithMsg(
                        activity,
                        it.value.response.raws.successMessage
                    )
                    _parentOtpResponse.value = null
                    parentOtpResponse = _parentOtpResponse

                }
                is Resource.Failure -> {
                    if (it.errorBody != null) {
                        MethodClass.hideProgressDialog(activity)
                        it.errorString?.let { it1 ->
                            it.errorString.let { _ ->
                                if (it.errorCode == 401)
                                    AppController.getInstance()
                                        .refreshTokenResponse(
                                            activity,
                                            null,
                                            SEND_OTP,
                                            REFRESH_TOKEN
                                        )
                                else {

                                    MethodClass.showErrorDialog(
                                        activity,
                                        it1,
                                        it.errorCode
                                    )
                                }
                            }
                        }

                        _parentOtpResponse.value = null
                        parentOtpResponse = _parentOtpResponse

                    }
                }
                else -> {}
            }

        }

    }


    //validation
    private fun otpValidation(activity: Activity): Boolean {
        invisibleErrorTexts()
        if (otp.value.isNullOrBlank()) {
            otpError.value = activity.getString(R.string.enter_otp)
            otpErrorVisible.value = true
            return false
        }
        if (otp.value?.length!! < 4) {
            otpError.value =
                activity.getString(R.string.enter_otp_which_has_been_sent_to_your_email)
            otpErrorVisible.value = true
            return false
        }
        return true
    }

    //error texts primary state
    fun invisibleErrorTexts() {
        otpErrorVisible.value = false
    }


}

