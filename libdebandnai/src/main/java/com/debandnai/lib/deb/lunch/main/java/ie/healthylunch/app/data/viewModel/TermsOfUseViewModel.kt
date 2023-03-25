package ie.healthylunch.app.data.viewModel

import android.annotation.SuppressLint
import android.app.Activity
import android.os.Build
import android.text.Html
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.JsonObject
import ie.healthylunch.app.data.model.loginModel.LoginResponse
import ie.healthylunch.app.data.model.privacyPolicyModel.Privacypolicy
import ie.healthylunch.app.data.network.Resource
import ie.healthylunch.app.data.repository.PrivacyPolicyViewRepository
import ie.healthylunch.app.ui.TermsOfUseActivity
import ie.healthylunch.app.utils.*
import ie.healthylunch.app.utils.Constants.Companion.REFRESH_TOKEN
import ie.healthylunch.app.utils.Constants.Companion.TERMS_OF_USE
import kotlinx.coroutines.launch

class TermsOfUseViewModel(private val repository: PrivacyPolicyViewRepository) : ViewModel() {

    var bool: Boolean = true

    //TERMS  OF USE
    private val _privacyPolicyResponse: SingleLiveEvent<Resource<Privacypolicy>> =
        SingleLiveEvent()
    val privacyPolicyResponse: SingleLiveEvent<Resource<Privacypolicy>>
        get() = _privacyPolicyResponse


    fun privacyPolicy(
        jsonObject: JsonObject,
        token: String
    ) = viewModelScope.launch {
        _privacyPolicyResponse.value = repository.privacyPolicyRepository(jsonObject, token)
    }


}