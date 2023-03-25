package com.merkaaz.app.data.viewModel

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.os.CountDownTimer
import android.text.Editable
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.*
import com.google.gson.JsonObject
import com.merkaaz.app.BuildConfig
import com.merkaaz.app.R
import com.merkaaz.app.adapter.TAG
import com.merkaaz.app.data.genericmodel.BaseResponse
import com.merkaaz.app.data.model.JsonobjectModel
import com.merkaaz.app.data.model.LoginModel
import com.merkaaz.app.network.Response
import com.merkaaz.app.repository.LoginRepository
import com.merkaaz.app.repository.OtpRepository
import com.merkaaz.app.utils.CommonFunctions
import com.merkaaz.app.utils.Constants.COUNTDOWN_TIMER
import com.merkaaz.app.utils.Constants.DEVICE_OS
import com.merkaaz.app.utils.SharedPreff
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.launch
import java.util.*
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@HiltViewModel
class OtpViewModel @Inject constructor(
    private val repository: OtpRepository,
    private val loginrepository: LoginRepository,
    private val sharedPreff: SharedPreff,
    private val commonFunctions: CommonFunctions,
    @SuppressLint("StaticFieldLeak") @ApplicationContext private val context: Context
) : ViewModel() {

    var countdownTimer: MutableLiveData<String> = MutableLiveData("00 sec")
    val pinlivedata = MutableLiveData<Int>()
    val phnlivedata = MutableLiveData<String>()
    var isClearPinData: MutableLiveData<Boolean> = MutableLiveData(false)
    val resendValidation = MutableLiveData<Boolean>()
    val otplivedata: LiveData<Response<JsonobjectModel>>
        get() = repository.otp
    val resendOTPdata: LiveData<Response<BaseResponse<LoginModel>>>
        get() = loginrepository.login
    //Count Down Timer function
    private val countDownTimer = object : CountDownTimer(60000, 1000) {

        override fun onTick(millis: Long) {
            val hms:String = String.format("%02d Sec",TimeUnit.MILLISECONDS.toSeconds(millis))
            countdownTimer.value= hms

        }

        override fun onFinish() {
            countdownTimer.value= COUNTDOWN_TIMER
            resendValidation.value = true
        }
    }

    init {
        countDownTimer.start()
        resendValidation.value = false
    }
    fun getPin(edtOTP: Editable?) {
        if (edtOTP.toString().trim().isNotEmpty())
            pinlivedata.postValue(edtOTP.toString().trim().toInt())
    }

    fun resend(){
        if (countdownTimer.value.equals(COUNTDOWN_TIMER)){
            countDownTimer.cancel()
            countDownTimer.start()
            resendValidation.value = false

            isClearPinData.value = true

            getLoginData()
        }
    }

    fun getLoginData() {
        val json = commonFunctions.commonJsonData()
        json.addProperty("phone", sharedPreff.getPhone())
        viewModelScope.launch {
            loginrepository.getLoginResponse(json)
        }
    }

    fun verifyOtp(){

        val pin = pinlivedata.value
        if (pinlivedata.value !=null && pinlivedata.value.toString().trim().length ==4){
            val tz: TimeZone = TimeZone.getDefault()

            val json = JsonObject()
            json.addProperty("phone",sharedPreff.getPhone())
            json.addProperty("otp", pinlivedata.value.toString())
            json.addProperty("device_version", Build.VERSION.SDK_INT)
            json.addProperty("device_name",Build.DEVICE)
            json.addProperty("device_model", Build.MODEL)
            json.addProperty("appname", context.resources.getString(R.string.app_name))
            json.addProperty("device_token",sharedPreff.getfirebase_token())
            json.addProperty("appversion", BuildConfig.VERSION_NAME)
            json.addProperty("device_os", DEVICE_OS)
            json.addProperty("timezone", tz.id)
            Log.d(TAG, "verifyotp: test token "+sharedPreff.getfirebase_token())

            viewModelScope.launch {
                repository.getOTPResponse(json)
            }
        }else{
            Toast.makeText(context,context.getString(R.string.please_enter_valid_oyp), Toast.LENGTH_LONG).show()
        }


    }
}