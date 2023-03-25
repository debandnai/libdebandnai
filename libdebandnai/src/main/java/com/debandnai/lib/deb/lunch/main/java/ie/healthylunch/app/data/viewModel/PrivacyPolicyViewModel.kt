package ie.healthylunch.app.data.viewModel

import android.annotation.SuppressLint
import android.app.Activity
import android.os.Build
import android.text.Html
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.JsonObject
import ie.healthylunch.app.data.model.loginModel.LoginResponse
import ie.healthylunch.app.data.model.privacyPolicyModel.Privacypolicy
import ie.healthylunch.app.data.network.Resource
import ie.healthylunch.app.data.repository.PrivacyPolicyViewRepository
import ie.healthylunch.app.ui.PrivacyPolicyActivity
import ie.healthylunch.app.utils.*
import ie.healthylunch.app.utils.Constants.Companion.PRIVACY_POLICY
import ie.healthylunch.app.utils.Constants.Companion.REFRESH_TOKEN
import ie.healthylunch.app.utils.Constants.Companion.TOKEN
import kotlinx.coroutines.launch

class PrivacyPolicyViewModel(private val repository: PrivacyPolicyViewRepository) : ViewModel() {

    //Privacy policy
    val _privacyPolicyResponse: SingleLiveEvent<Resource<Privacypolicy>?> =
        SingleLiveEvent()
    var privacyPolicyResponse: LiveData<Resource<Privacypolicy>?>? = null
        get() = _privacyPolicyResponse

    @SuppressLint("StaticFieldLeak")
    lateinit var activity: PrivacyPolicyActivity
    fun privacyPolicy(
        jsonObject: JsonObject,
        token: String
    ) = viewModelScope.launch {
        _privacyPolicyResponse.value = repository.privacyPolicyRepository(jsonObject, token)
    }

    fun back(activity: Activity) {
        activity.finish()
    }


}