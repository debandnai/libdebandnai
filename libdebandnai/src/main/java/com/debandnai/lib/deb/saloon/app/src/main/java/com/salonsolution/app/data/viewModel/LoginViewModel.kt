package com.salonsolution.app.data.viewModel

import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.TextView
import androidx.lifecycle.*
import com.google.gson.JsonObject
import com.salonsolution.app.R
import com.salonsolution.app.data.model.LoginModel
import com.salonsolution.app.data.model.genericModel.BaseResponse
import com.salonsolution.app.data.network.RequestBodyHelper
import com.salonsolution.app.data.network.ResponseState
import com.salonsolution.app.data.repository.AuthRepository
import com.salonsolution.app.utils.UtilsCommon
import com.salonsolution.app.utils.UtilsCommon.isValidEmail
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val requestBodyHelper: RequestBodyHelper
) : ViewModel(), TextView.OnEditorActionListener {

    var email = MutableLiveData("")
    var password = MutableLiveData("")
    var rememberMe = MutableLiveData(false)

    val loginResponseLiveData: LiveData<ResponseState<BaseResponse<LoginModel>>>
        get() = authRepository.loginResponseLiveData

    //for validation
    var emailError: MutableLiveData<Pair<Boolean, String>> = MutableLiveData(Pair(false, ""))
    var passwordError: MutableLiveData<Pair<Boolean, String>> = MutableLiveData(Pair(false, ""))

    //LiveData subclass which may observe other LiveData objects and react on OnChanged events from them
    var errorResetObserver: MediatorLiveData<String> = MediatorLiveData()

    init {
        errorResetObserver.addSource(email) {
            emailError.value = Pair(false, "")
        }
        errorResetObserver.addSource(password) {
            passwordError.value = Pair(false, "")
        }

    }

    fun signInButtonClick(view: View) {
        if (loginFormValidation(view)) {
            UtilsCommon.hideKeyboard(view)
            login(requestBodyHelper.getLoginRequest(email.value?.trim(), password.value?.trim()))
        }
    }

    private fun login(loginRequest: JsonObject) {
        viewModelScope.launch {
            authRepository.login(loginRequest)
        }
    }

    private fun loginFormValidation(view: View): Boolean {
        val result: Boolean
        if (email.value.isNullOrEmpty()) {
            emailError.value =
                Pair(true, view.context.resources.getString(R.string.please_enter_email_address))
            result = false
        } else if (!isValidEmail(email.value?.trim())) {
            emailError.value =
                Pair(true, view.context.resources.getString(R.string.please_enter_valid_email_address))
            result = false
        } else if (password.value.isNullOrEmpty()) {
            passwordError.value =
                Pair(true, view.context.resources.getString(R.string.please_enter_password))
            result = false
        } else if ((password.value?.length ?: 0) <3) {
            passwordError.value =
                Pair(
                    true,
                    view.context.resources.getString(R.string.password_minimum_characters_validation)
                )
            result = false
        } else {
            result = true
            // UtilsCommon.hideKeyboard(view)
        }

        return result

    }

    override fun onEditorAction(v: TextView?, actionId: Int, event: KeyEvent?): Boolean {
        v?.let {
            Log.d("tag","id: ${it.id}")
            when (actionId) {
                EditorInfo.IME_ACTION_NEXT -> {
                    it.clearFocus()
                    return  !loginFormValidation(it)
                }
                EditorInfo.IME_ACTION_DONE -> {
                    it.clearFocus()
                    return !loginFormValidation(it)

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