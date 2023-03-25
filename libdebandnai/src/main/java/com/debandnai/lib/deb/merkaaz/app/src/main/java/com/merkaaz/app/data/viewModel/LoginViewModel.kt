package com.merkaaz.app.data.viewModel


import android.annotation.SuppressLint
import android.app.Application
import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.View
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.*
import com.merkaaz.app.BuildConfig
import com.merkaaz.app.R
import com.merkaaz.app.data.genericmodel.BaseResponse
import com.merkaaz.app.network.Response
import com.merkaaz.app.data.model.JsonobjectModel
import com.merkaaz.app.data.model.LoginModel
import com.merkaaz.app.repository.LoginRepository
import com.merkaaz.app.ui.activity.SignUpActivity
import com.merkaaz.app.utils.CommonFunctions
import com.merkaaz.app.utils.MethodClass
import com.merkaaz.app.utils.SharedPreff
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.launch
import javax.inject.Inject


//val bottomSheet = registrationActivity?.let { BottomSheetDialog(it) }

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val repository: LoginRepository,
    private val commonFunctions: CommonFunctions,
    @SuppressLint("StaticFieldLeak") @ApplicationContext private  val context: Context?
) : ViewModel() {
    var mobileNumber: MutableLiveData<String> = MutableLiveData()
    //var countryArrayList: ArrayList<CountryListModel> = ArrayList()
    val loginText: MutableLiveData<String> = MutableLiveData()
//    val loginLiveData: LiveData<Response<JsonobjectModel>>
//        get() = repository.login

    val loginLiveData: LiveData<Response<BaseResponse<LoginModel>>>
        get() = repository.login

    /*init {
        countryArrayList = MethodClass.flag_data()
    }*/


    private lateinit var fragmentManager: FragmentManager


    fun setLanguage(view: View) {
        /*  val bottomSheet=BottomSheetDialog(view.context)
          bottomSheet.show(
              fragmentManager,
              "ModalBottomSheet"
          )*/
    }


    //Send otp
    fun sendOtp() {
        if (validationMobileno()) {
            val appVersion: String = BuildConfig.VERSION_NAME
            val json = commonFunctions.commonJsonData()
            json.addProperty("phone", mobileNumber.value.toString().trim())
//            json.addProperty("device_os", DEVICE_OS)
//            json.addProperty("appversion", appVersion)
            viewModelScope.launch {
                repository.getLoginResponse(json)
            }

        } else {
            context?.let { MethodClass.showToastMsg(it,it.getString(R.string.please_enter_valid_mobile_no)) }
        }
    }

    //SignUp
    fun signUp(view: View) {

        view.context.apply {
            startActivity(
                Intent(this, SignUpActivity::class.java)
            )
        }
    }


    private fun validationMobileno(): Boolean {
        invisibleErrorTexts()
        if (mobileNumber.value.isNullOrBlank()) {
            return false
        } else if (mobileNumber.value.isNullOrEmpty()) {
            return false
        } else if (mobileNumber.value?.length == 0) {
            return false
        }
        return true
    }

    //Get Current Location
    fun getCurrentLocation() {

//        viewModelScope.launch(Dispatchers.IO) {
//            registrationActivity?.let {
//                if (ActivityCompat.checkSelfPermission(
//                        it,
//                        Manifest.permission.ACCESS_FINE_LOCATION
//                    ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
//                        it, Manifest.permission.ACCESS_COARSE_LOCATION
//                    ) != PackageManager.PERMISSION_GRANTED
//                ) {
//                    logInFragment?.requestPermissionLauncher?.launch(Manifest.permission.ACCESS_FINE_LOCATION)
//                }
//            }
//
//            registrationActivity?.let { it ->
//                LocationServices.getFusedLocationProviderClient(it).lastLocation.addOnSuccessListener { location: Location? ->
//                    location.let {
////                        LATITUDE = it?.latitude?.let { it1 -> doubleValueFormat(it1) }
////                        LONGITUDE = it?.longitude?.let { it1 -> doubleValueFormat(it1) }
//
//                    }
//                }
//            }
//
//        }
    }


    //For error message showing
    fun invisibleErrorTexts() {

    }
}