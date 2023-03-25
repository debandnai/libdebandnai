package ie.healthylunch.app.data.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.JsonObject
import ie.healthylunch.app.data.model.logoutModel.LogoutResponse
import ie.healthylunch.app.data.model.refreshTokenModel.RefreshTokenResponse
import ie.healthylunch.app.data.network.Resource
import ie.healthylunch.app.data.repository.RefreshTokenRepository
import ie.healthylunch.app.data.viewModel.userDeleteAccount.UserDeleteAccountResponse
import ie.healthylunch.app.utils.SingleLiveEvent
import kotlinx.coroutines.launch

class AppControllerRefreshTokenViewModel(
    private val repository: RefreshTokenRepository
) : ViewModel() {

    //refreshToken
    private val _refreshTokenResponse: MutableLiveData<Resource<RefreshTokenResponse>> =
        MutableLiveData()
    val refreshTokenResponse: LiveData<Resource<RefreshTokenResponse>>
        get() = _refreshTokenResponse

    fun refreshToken(
        jsonObject: JsonObject,
        token: String?

    ) = viewModelScope.launch {
        if (!token.isNullOrBlank())
            _refreshTokenResponse.value = repository.refreshToken(jsonObject, token)
    }

    // Logout
    val _logoutResponse: SingleLiveEvent<Resource<LogoutResponse>?> =
        SingleLiveEvent()
    var logoutResponse: LiveData<Resource<LogoutResponse>?>? = null
        get() = _logoutResponse

    fun logout(
        jsonObject: JsonObject,
        token: String?
    ) = viewModelScope.launch {
        if (!token.isNullOrBlank())
            _logoutResponse.value = repository.logout(jsonObject, token)
    }

    // delete account
    val _deleteAccountResponse: SingleLiveEvent<Resource<UserDeleteAccountResponse>?> =
        SingleLiveEvent()
    var deleteAccountResponse: LiveData<Resource<UserDeleteAccountResponse>?>? = null
        get() = _deleteAccountResponse

    fun userDeleteAccount(
        jsonObject: JsonObject,
        token: String?
    ) = viewModelScope.launch {
        if (!token.isNullOrBlank())
            _deleteAccountResponse.value = repository.userDeleteAccount(jsonObject, token)
    }
}