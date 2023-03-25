package ie.healthylunch.app.data.viewModel

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.annotation.RequiresApi
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.Navigation
import androidx.navigation.fragment.NavHostFragment
import com.google.gson.JsonObject
import ie.healthylunch.app.R
import ie.healthylunch.app.data.model.loginModel.LoginResponse
import ie.healthylunch.app.data.model.removeProductsHavingMayAllergenModel.RemoveProductsHavingMayAllergenResponse
import ie.healthylunch.app.data.network.Resource
import ie.healthylunch.app.data.repository.RemoveProductsHavingMayAllergenRepository
import ie.healthylunch.app.fragment.registration.AllergenProductRemoveConfirmationFragment
import ie.healthylunch.app.ui.RegistrationActivity
import ie.healthylunch.app.utils.*
import ie.healthylunch.app.utils.Constants.Companion.LAST_ADDED_STUDENT_NAME
import ie.healthylunch.app.utils.Constants.Companion.NO
import ie.healthylunch.app.utils.Constants.Companion.YES
import kotlinx.coroutines.launch

class RemoveProductsHavingMayAllergenViewModel(
    private val repository: RemoveProductsHavingMayAllergenRepository
) : ViewModel() {
    var promotionAlert: MutableLiveData<String> = MutableLiveData()
    var yesRadioButton: MutableLiveData<Boolean> = MutableLiveData(false)
    var noRadioButton: MutableLiveData<Boolean> = MutableLiveData(false)
    var promotionAlertErrorVisible: MutableLiveData<Boolean> = MutableLiveData(false)
    var studentId: MutableLiveData<Int> = MutableLiveData(0)
    var userType: MutableLiveData<String> = MutableLiveData()
    var page: MutableLiveData<String> = MutableLiveData()
    var isRemoveProduct: String? =null

    //var studentFirstName: MutableLiveData<String> = MutableLiveData("")
    var promotionAlertError: MutableLiveData<String> = MutableLiveData("")
    //lateinit var allergenProductRemoveConfirmationFragment: AllergenProductRemoveConfirmationFragment
    var isSubmitEnabled: MutableLiveData<Boolean> = MutableLiveData(true)

//    @SuppressLint("StaticFieldLeak")
//    lateinit var activity: RegistrationActivity


    //Remove Product May Allergen Api
    val _removeProductMayAllergenResponse: SingleLiveEvent<Resource<RemoveProductsHavingMayAllergenResponse>?> =
        SingleLiveEvent()
    var removeProductMayAllergenResponse: SingleLiveEvent<Resource<RemoveProductsHavingMayAllergenResponse>?>? =
        null
        get() = _removeProductMayAllergenResponse

    fun removeProductMayAllergen(
        jsonObject: JsonObject,
        token: String
    ) = viewModelScope.launch {
        _removeProductMayAllergenResponse.value =
            repository.removeProductMayAllergen(jsonObject, token)
    }

}