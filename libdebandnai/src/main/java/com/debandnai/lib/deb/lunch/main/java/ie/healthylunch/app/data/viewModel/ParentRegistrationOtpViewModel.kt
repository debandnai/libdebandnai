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
import ie.healthylunch.app.data.model.checkEmailModel.CheckemailExistResponse
import ie.healthylunch.app.data.model.editParentEmailModel.EditParentEmail
import ie.healthylunch.app.data.model.otpModel.OtpResponse
import ie.healthylunch.app.data.model.parentEmailVerificationModel.ParentemailverificationResponse
import ie.healthylunch.app.data.network.Resource
import ie.healthylunch.app.data.repository.ParentRegistrationRepository
import ie.healthylunch.app.fragment.registration.RegistrationOtpFragment
import ie.healthylunch.app.ui.RegistrationActivity
import ie.healthylunch.app.utils.*
import kotlinx.coroutines.launch

class ParentRegistrationOtpViewModel(val repository: ParentRegistrationRepository) : ViewModel() {
    var email: MutableLiveData<String> = MutableLiveData("")
    var editEmailTv: MutableLiveData<String> = MutableLiveData("Edit Email")
    var check: MutableLiveData<String> = MutableLiveData("")
    var emailValue: MutableLiveData<String> = MutableLiveData("")
    var otp: MutableLiveData<String> = MutableLiveData()
    private var isOtpPopUpShowing: Boolean = false
    lateinit var registrationOtpFragment: RegistrationOtpFragment

    @SuppressLint("StaticFieldLeak")
    lateinit var view: View
    var otpError: MutableLiveData<String> = MutableLiveData()
    val bundle = Bundle()
    var otpErrorVisible: MutableLiveData<Boolean> = MutableLiveData(false)
    var isSubmitEnabled: MutableLiveData<Boolean> = MutableLiveData(true)

    //Edit Email
    val _editParentEmail: SingleLiveEvent<Resource<EditParentEmail>> =
        SingleLiveEvent()


    var getNewParentEmail: LiveData<Resource<EditParentEmail>>? = null
        get() = _editParentEmail

    fun setParentEmail(
        jsonObject: JsonObject,
        token: String
    ) = viewModelScope.launch {
        _editParentEmail.value = repository.editParentEmail(jsonObject, token)
    }

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
            repository.parentEmailVerification(jsonObject)
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


    //Check Email
    val _checkEmailExistResponse: SingleLiveEvent<Resource<CheckemailExistResponse>> =
        SingleLiveEvent()

    var checkEmailExistResponse: LiveData<Resource<CheckemailExistResponse>>? = null
        get() = _checkEmailExistResponse

    fun checkEmail(
        jsonObject: JsonObject

    ) = viewModelScope.launch {
        _checkEmailExistResponse.value = repository.checkEmailExistRepository(jsonObject)
    }

    fun continueOnClick(activity: Activity, view: View) {
        activity as RegistrationActivity
        otpValidation(activity)
        if (otpValidation(activity)) {
            isSubmitEnabled.value = false
            MethodClass.showProgressDialog(activity)
            val jsonObjelct = MethodClass.getCommonJsonObject(activity)
            jsonObjelct.addProperty("onetimepassword", otp.value)
            jsonObjelct.addProperty("email", emailValue.value)
            jsonObjelct.addProperty("otp_purpose", 2)

            verifyOTP(jsonObjelct)

        }

    }

    fun getVerifyOtpResponse(activity: Activity, view: View) {
        activity as RegistrationActivity
        parentEmailVerificationResponse?.observe(activity) {

            when (it) {
                is Resource.Success -> {
                    MethodClass.hideProgressDialog(activity)


                    Navigation.findNavController(view)
                        .navigate(R.id.action_registrationOtpFragment_to_addStudentNewStepOneFragment)
                    isSubmitEnabled.value = true
                    MethodClass.hideProgressDialog(activity)
                    _parentEmailVerificationResponse.value = null
                    parentEmailVerificationResponse = _parentEmailVerificationResponse


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
                    _parentEmailVerificationResponse.value = null
                    parentEmailVerificationResponse = _parentEmailVerificationResponse
                    isSubmitEnabled.value = true
                }
                else -> {}
            }

        }

    }

    fun resendOtpClick(activity: Activity, otp_purpose: Int) {
        MethodClass.showProgressDialog(activity)
        activity as RegistrationActivity
        val jsonObject = MethodClass.getCommonJsonObject(activity)
        jsonObject.addProperty("email", emailValue.value)
        jsonObject.addProperty("otp_purpose", otp_purpose.toString())
        sendOtpForReg(jsonObject)
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
                            MethodClass.showErrorDialog(
                                activity,
                                it1,
                                it.errorCode
                            )
                        }
                        _parentOtpResponse.value = null
                        parentOtpResponse = _parentOtpResponse

                    }
                }
                else -> {}
            }

        }

    }

    fun infoClick(activity: Activity) {
        CustomDialog.showInfoTypeDialog(activity)

    }


    //validation
    private fun otpValidation(activity: Activity): Boolean {
        invisibleErrorTexts()

        if (otp.value.isNullOrBlank()) {
            otpError.value = activity.getString(R.string.please_enter_otp)
            otpErrorVisible.value = true
            return false
        }
        if (otp.value!!.length < 4) {
            otpError.value = activity.getString(R.string.please_enter_4_digits_otp)
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