package com.merkaaz.app.data.viewModel

import android.content.Context
import android.content.Intent
import android.view.View
import android.widget.Toast
import androidx.lifecycle.*
import com.merkaaz.app.R
import com.merkaaz.app.network.Response
import com.merkaaz.app.data.model.JsonobjectModel
import com.merkaaz.app.repository.SignUpRepository
import com.merkaaz.app.ui.activity.*
import com.merkaaz.app.utils.CommonFunctions
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class SignUpViewModel @Inject constructor(
    private val repository: SignUpRepository,
    private val commonFunctions: CommonFunctions,
    @ApplicationContext private val context: Context
) : ViewModel() {

    var shopName: MutableLiveData<String> = MutableLiveData()
    var vendorName: MutableLiveData<String> = MutableLiveData()
    var phoneNo: MutableLiveData<String> = MutableLiveData()
    var emailId: MutableLiveData<String> = MutableLiveData("")
    var taxId: MutableLiveData<String> = MutableLiveData("")
    var address1: MutableLiveData<String> = MutableLiveData("")
    var address2: MutableLiveData<String> = MutableLiveData("")
    var latitude: MutableLiveData<String> = MutableLiveData("")
    var longitude: MutableLiveData<String> = MutableLiveData("")
    var address: MutableLiveData<String> = MutableLiveData()
    val signupLiveData: LiveData<Response<JsonobjectModel>>
        get() = repository.signup
    //var countryArrayList: ArrayList<CountryListModel> = ArrayList()

    var city: String = ""
    var state: String = ""
    var referencePoint: String = ""

    /*init {
        countryArrayList = commonFunctions.flag_data()
    }*/


    //navigate Login Activity
    fun login(view: View) {
        view.context.startActivity(Intent(view.context, LoginActivity::class.java))

    }


    //navigate to congratulations page
    fun signUpClick(view: View) {
        if (isValidate(view.context)) {
            val getJsonObject = commonFunctions.commonJsonData()
            getJsonObject.addProperty("vendor_name", vendorName.value)
            getJsonObject.addProperty("phone_no", phoneNo.value)
            getJsonObject.addProperty("email_id", emailId.value)
            getJsonObject.addProperty("city", view.context.getString(R.string.luanda))
            getJsonObject.addProperty("country",view.context.getString(R.string.angola) )
            getJsonObject.addProperty("address1", address1.value)
            getJsonObject.addProperty("address2", address2.value)
            getJsonObject.addProperty("latitude", latitude.value)
            getJsonObject.addProperty("longitude", longitude.value)
            getJsonObject.addProperty("referrence_point", referencePoint)
            getJsonObject.addProperty("tax_id", taxId.value)
            getJsonObject.addProperty("shop_name", shopName.value)


            viewModelScope.launch {
                repository.getsignup(getJsonObject)
            }
        }

    }


    private fun isValidate(context: Context): Boolean {

        if (shopName.value.isNullOrEmpty()) {
            Toast.makeText(context, context.getString(R.string.please_enter_shop_name), Toast.LENGTH_SHORT).show()
            return false
        }else if (address.value.isNullOrEmpty() or address.equals(context.resources.getString(R.string.press_here_to_set_shop_address))) {
            Toast.makeText(context, context.getString(R.string.please_pick_address_up_from_map), Toast.LENGTH_SHORT).show()
            return false
        }else if (vendorName.value.isNullOrEmpty()) {
            Toast.makeText(context, context.getString(R.string.please_enter_your_name), Toast.LENGTH_SHORT).show()
            return false
        }else if (phoneNo.value.isNullOrEmpty()) {
            Toast.makeText(context, context.getString(R.string.please_phone_number), Toast.LENGTH_SHORT).show()
            return false
        }else if (phoneNo.value.toString().length != 9) {
            Toast.makeText(context, context.getString(R.string.phone_number_limit_msg), Toast.LENGTH_SHORT).show()
            return false
        }
        return true
    }

}



