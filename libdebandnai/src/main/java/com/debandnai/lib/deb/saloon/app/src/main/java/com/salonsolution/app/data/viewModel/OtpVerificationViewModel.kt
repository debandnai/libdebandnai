package com.salonsolution.app.data.viewModel

import android.os.CountDownTimer
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.TextView
import androidx.lifecycle.*
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.salonsolution.app.R
import com.salonsolution.app.data.model.genericModel.BaseResponse
import com.salonsolution.app.data.network.RequestBodyHelper
import com.salonsolution.app.data.network.ResponseState
import com.salonsolution.app.data.repository.AuthRepository
import com.salonsolution.app.utils.Constants
import com.salonsolution.app.utils.UtilsCommon
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OtpVerificationViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val requestBodyHelper: RequestBodyHelper
) : ViewModel(), TextView.OnEditorActionListener {

    var resendTimerInSec = Constants.OTP_RESEND_TIMER_IN_SEC
    var counter = MutableLiveData(Constants.OTP_RESEND_TIMER_IN_SEC)
    var isTimerRunning = MutableLiveData(false)
    var otp = MutableLiveData("")
    var email = MutableLiveData("")
    var isRegister = true


    //for validation
    var otpError: MutableLiveData<Pair<Boolean, String>> = MutableLiveData(Pair(false, ""))


    //LiveData subclass which may observe other LiveData objects and react on OnChanged events from them
    var errorResetObserver: MediatorLiveData<String> = MediatorLiveData()

    val verifyOtpResponseLiveData: LiveData<ResponseState<BaseResponse<JsonElement>>>
        get() = authRepository.verifyOtpResponseLiveData

    val resendOtpResponseLiveData: LiveData<ResponseState<BaseResponse<JsonElement>>>
        get() = authRepository.resendOtpResponseLiveData

    //Count Down Timer function
    private val countDownTimer = object : CountDownTimer(((counter.value?:0) * 1000).toLong(), 1000) {

        override fun onTick(millis: Long) {
            counter.value = resendTimerInSec--
            isTimerRunning.value = true

        }

        override fun onFinish() {
            isTimerRunning.value = false
            resendTimerInSec= Constants.OTP_RESEND_TIMER_IN_SEC
        }
    }

    init {
        countDownTimer.start()
        isTimerRunning.value = true
        errorResetObserver.addSource(otp) {
            otpError.value = Pair(false, "")
        }

    }

    fun submitButtonClick(view: View) {
        if (otpValidation(view)) {
            UtilsCommon.hideKeyboard(view)
            verifyOtp(
                requestBodyHelper.getOtpRequest(
                    if (isRegister) Constants.OTP_TYPE_REGISTER else Constants.OTP_TYPE_FORGOT,
                    email.value?.trim(),
                    otp.value?.trim()
                )
            )
        }
    }

    fun resendOtp(view: View){
        UtilsCommon.hideKeyboard(view)
        viewModelScope.launch {
            authRepository.resendOtp(requestBodyHelper.getResendOtpRequest(email.value?.trim()))
        }
    }
    fun startTimer(){
        countDownTimer.cancel()
        resendTimerInSec = Constants.OTP_RESEND_TIMER_IN_SEC
        countDownTimer.start()
        otp.value = ""
        otpError.value = Pair(false,"")
    }

    private fun verifyOtp(registerRequest: JsonObject) {
        viewModelScope.launch {
            authRepository.verifyOtp(registerRequest)
        }
    }


    private fun otpValidation(view: View): Boolean {
        val result: Boolean
        if (otp.value.isNullOrEmpty()) {
            otpError.value =
                Pair(true, view.context.resources.getString(R.string.please_enter_otp))
            result = false
        } else {
            result = true
            // UtilsCommon.hideKeyboard(view)
        }

        return result

    }


    override fun onEditorAction(v: TextView?, actionId: Int, event: KeyEvent?): Boolean {
        v?.let {
            Log.d("tag", "id: ${it.id}")
            when (actionId) {
                EditorInfo.IME_ACTION_NEXT -> {
                    it.clearFocus()
                    return !otpValidation(it)
                }
                EditorInfo.IME_ACTION_DONE -> {
                    it.clearFocus()
                    return !otpValidation(it)

                }
                else -> {
                    return false
                }
            }
        }

        return false
    }

    fun clearUpdateState() {
        authRepository.clearUpdateState()
    }

}