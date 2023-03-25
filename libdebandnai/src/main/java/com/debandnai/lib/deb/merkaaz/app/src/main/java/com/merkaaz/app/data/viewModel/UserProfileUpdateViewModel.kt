package com.merkaaz.app.data.viewModel


import android.annotation.SuppressLint
import android.content.Context
import android.view.View
import android.widget.Toast
import androidx.lifecycle.*
import com.merkaaz.app.BuildConfig
import com.merkaaz.app.R
import com.merkaaz.app.data.model.JsonobjectModel
import com.merkaaz.app.data.model.LoginModel
import com.merkaaz.app.network.Response
import com.merkaaz.app.repository.UserProfileUpdateRepository
import com.merkaaz.app.utils.CommonFunctions
import com.merkaaz.app.utils.Constants.DEVICE_OS
import com.merkaaz.app.utils.SharedPreff
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File
import javax.inject.Inject

@HiltViewModel
class UserProfileUpdateViewModel @Inject constructor(
    private val repository: UserProfileUpdateRepository,
    private val sharedPreff: SharedPreff,
    private val commonFunctions: CommonFunctions,
    @SuppressLint("StaticFieldLeak") @ApplicationContext private  val context: Context
) : ViewModel() {
    val updateUserProfileLiveData: LiveData<Response<JsonobjectModel>>
        get() = repository.updateUserProfile
    var loginModel : LoginModel = sharedPreff.get_logindata()

    var mobileNo: MutableLiveData<String> = MutableLiveData(loginModel.phone)
    var vendorName: MutableLiveData<String> = MutableLiveData(loginModel.vendorName)
    var shopName: MutableLiveData<String> = MutableLiveData(loginModel.shopName)
    var email: MutableLiveData<String> = MutableLiveData(loginModel.email)
    var tax: MutableLiveData<String> = MutableLiveData(loginModel.taxId)
    var image: MutableLiveData<String> = MutableLiveData(loginModel.profileImage)
    var photoFile: File? = null

    //Update User Profile
    fun saveChangeClick(view: View){
        if (isValidate(view.context)) {
            val appVersion: String = BuildConfig.VERSION_NAME
            val shopName: RequestBody = shopName.value!!.toRequestBody("multipart/form-data".toMediaTypeOrNull())
            val vendorName: RequestBody = vendorName.value!!.toRequestBody("multipart/form-data".toMediaTypeOrNull())
            val phone: RequestBody = mobileNo.value!!.toRequestBody("multipart/form-data".toMediaTypeOrNull())
            val email: RequestBody = email.value!!.toRequestBody("multipart/form-data".toMediaTypeOrNull())
            val tax: RequestBody = tax.value!!.toRequestBody("multipart/form-data".toMediaTypeOrNull())
            val language: RequestBody = sharedPreff.getlanguage_id()!!.toRequestBody("multipart/form-data".toMediaTypeOrNull())
            val devOs: RequestBody = DEVICE_OS.toRequestBody("multipart/form-data".toMediaTypeOrNull())
            val appversion: RequestBody = appVersion.toRequestBody("multipart/form-data".toMediaTypeOrNull())
//            val image: RequestBody = shopName.value!!.toRequestBody("multipart/form-data".toMediaTypeOrNull())
            var image: MultipartBody.Part? = null
            image = if ( photoFile != null) {
                // create RequestBody instance from file
                val requestFile: RequestBody? = photoFile?.asRequestBody("multipart/form-data".toMediaTypeOrNull())

                requestFile?.let {
                    MultipartBody.Part.createFormData("image",photoFile?.name,
                        it
                    )
                }
            } else {
                val requestFile: RequestBody = "".toRequestBody("multipart/form-data".toMediaTypeOrNull())
                MultipartBody.Part.createFormData("image", "", requestFile)
            }
            viewModelScope.launch {
                image?.let {
                    repository.updateUserProfile(
                        commonFunctions.commonHeader(),
                        shopName,vendorName,phone,email,tax,language,devOs,appversion, it
                    )
                }
            }
        }

    }
    //Validation
    private fun isValidate(context: Context): Boolean {

        if (shopName.value.isNullOrEmpty()) {
            Toast.makeText(context, context.getString(R.string.please_enter_shop_name), Toast.LENGTH_SHORT).show()
            return false
        }else if (vendorName.value.isNullOrEmpty()) {
            Toast.makeText(context, context.getString(R.string.please_enter_your_name), Toast.LENGTH_SHORT).show()
            return false
        }else if (mobileNo.value.isNullOrEmpty()) {
            Toast.makeText(context, context.getString(R.string.please_phone_number), Toast.LENGTH_SHORT).show()
            return false
        }else if (mobileNo.value.toString().length != 9) {
            Toast.makeText(context, context.getString(R.string.phone_number_limit_msg), Toast.LENGTH_SHORT).show()
            return false
        }
        return true
    }

}