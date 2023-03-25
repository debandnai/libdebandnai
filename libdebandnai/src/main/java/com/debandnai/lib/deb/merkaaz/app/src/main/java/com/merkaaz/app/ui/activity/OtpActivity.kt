package com.merkaaz.app.ui.activity

import android.app.Dialog
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.util.Log
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.auth.api.phone.SmsRetriever
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.merkaaz.app.R
import com.merkaaz.app.adapter.TAG
import com.merkaaz.app.data.model.LoginModel
import com.merkaaz.app.data.viewModel.OtpViewModel
import com.merkaaz.app.databinding.ActivityOtpBinding
import com.merkaaz.app.interfaces.SmsBroadcastReceiverListener
import com.merkaaz.app.network.Response
import com.merkaaz.app.ui.base.BaseActivity
import com.merkaaz.app.utils.CommonFunctions
import com.merkaaz.app.utils.Constants.PAGE_TYPE
import com.merkaaz.app.utils.Constants.SMS_SENDER_NUMBER
import com.merkaaz.app.utils.MethodClass
import com.merkaaz.app.utils.OtpRetriever
import com.merkaaz.app.utils.SharedPreff
import dagger.hilt.android.AndroidEntryPoint
import java.util.*
import java.util.regex.Matcher
import java.util.regex.Pattern
import javax.inject.Inject


@AndroidEntryPoint
class OtpActivity : BaseActivity(), SmsBroadcastReceiverListener {
    private lateinit var binding: ActivityOtpBinding
    private lateinit var otpViewModel: OtpViewModel
    private lateinit var loader: Dialog
    private lateinit var oTORetriever: OtpRetriever


    var pageType = ""
    var resultLauncher: ActivityResultLauncher<Intent> = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()) { result ->
        getActivityResult(result)
    }


    @Inject
    lateinit var commonFunctions: CommonFunctions

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_otp)

        //set the activity of activity variable in layout
//        binding.activity = this
        otpViewModel = ViewModelProvider(this)[OtpViewModel::class.java]
        binding.viewModel = otpViewModel
        binding.lifecycleOwner = this
        initialise()
        setData()
        observeData()
        getIntentData()
        startSmartUserConsent()
    }





    private fun getIntentData() {
        if (intent.extras != null) {
            pageType = intent.getStringExtra(PAGE_TYPE).toString()
            Log.d(
                TAG,
                "getIntentData: otp " + pageType + " " + Objects.equals(
                    pageType,
                    getString(R.string.sign_up)
                )
            )
            if (Objects.equals(pageType, getString(R.string.sign_up)))
                otpViewModel.getLoginData()
        }
    }

    private fun initialise() {
        loader = MethodClass.custom_loader(this, getString(R.string.please_wait))!!
        oTORetriever=OtpRetriever()
        oTORetriever.setListener(this)
    }

    private fun setData() {
        otpViewModel.phnlivedata.value =
            resources.getString(R.string.a_one_time_password_has_been_sent_to_244_1238329147) + sharedPreff.getPhone()

    }

    private fun observeData() {

        otpViewModel.otplivedata.observe(this) {

            when (it) {
                is Response.Loading -> {
                    loader.show()
                }
                is Response.Success -> {
                    it.data?.let { data ->
                        val res = data.response?.status?.msg

                        loader.dismiss()
                        if (data.response?.status?.actionStatus == true) {
                            val logindata = data.response?.data
                            val gson = Gson()
                            val loginType = object : TypeToken<LoginModel>() {}.type
                            val login: LoginModel = gson.fromJson(logindata, loginType)
                            println("login data.. test ${login.isActive}")
                            sharedPreff.store_logindata(login)

                            if (pageType == getString(R.string.sign_up)) {
                                startActivity(
                                    Intent(this, CongratulationsActivity::class.java)
                                        .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                        .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                                        .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                                )
                            } else {
                                startActivity(
                                    Intent(this, DashBoardActivity::class.java)
                                        .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                        .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
                                        .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                                )
                            }
                            finish()
                        } else {
                            MethodClass.custom_msg_dialog(this, res)?.show()
                            //binding.pinView.setText("")
                            otpViewModel.isClearPinData.value = true

                        }

                    }

                }
                is Response.Error -> {
                    loader.dismiss()
                    commonFunctions.showErrorMsg(this, it.errorCode, it.errorMessage)
                    //binding.pinView.setText("")
                    otpViewModel.isClearPinData.value = true
                }
            }
        }

//        otpViewModel.resendValidation.observe(this){
//            binding.txtResend.isEnabled = it
//            binding.txtResend.alpha = if (it) 1f else 0.5f
////            if (it == false){
////                binding.txtResend.setTextColor(ContextCompat.getColor(this,R.color.gray))
////            }else{
////                binding.txtResend.setTextColor(ContextCompat.getColor(this,R.color.resend_otp_color))
////            }
//        }

        otpViewModel.resendOTPdata.observe(this) {
            when (it) {
                is Response.Loading -> {
                    loader.show()
                }
                is Response.Success -> {
                    loader.dismiss()
                }
                is Response.Error -> {
                    loader.dismiss()
                    commonFunctions.showErrorMsg(this, it.errorCode, it.errorMessage)
                }
            }
        }
    }

    override fun onStart() {
        super.onStart()
        val intentFilter = IntentFilter(SmsRetriever.SMS_RETRIEVED_ACTION)
        registerReceiver(oTORetriever, intentFilter)
    }

    override fun onStop() {
        super.onStop()
        unregisterReceiver(oTORetriever)
    }
    private fun startSmartUserConsent() {
        val client = SmsRetriever.getClient(this)
        client.startSmsUserConsent(SMS_SENDER_NUMBER)
    }
    private fun getActivityResult(result: ActivityResult?) {
        if (result?.resultCode == RESULT_OK) {
            val message: String? = result.data?.getStringExtra(SmsRetriever.EXTRA_SMS_MESSAGE)
            message?.let{getOtpFromMessage(it)}
        }
    }

    private fun getOtpFromMessage(message: String) {
        val otpPattern: Pattern = Pattern.compile("(|^)\\d{4}")
        val matcher: Matcher = otpPattern.matcher(message)
        if (matcher.find()) {
            try {
                otpViewModel.pinlivedata.value= matcher.group(0)?.toInt()
                binding.pinView.setText(matcher.group(0))
                otpViewModel.verifyOtp()
            }catch (e:Exception){}

        }
    }

    override fun onSuccess(intent: Intent?) {
       resultLauncher.launch(intent)
    }

    override fun onFailure() {
        Log.d("otp failure","Time out")
    }

}