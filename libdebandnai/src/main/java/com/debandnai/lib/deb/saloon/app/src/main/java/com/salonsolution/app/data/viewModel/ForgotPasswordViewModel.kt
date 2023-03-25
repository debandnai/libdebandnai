package com.salonsolution.app.data.viewModel

import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.TextView
import androidx.lifecycle.*
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import com.salonsolution.app.R
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
class ForgotPasswordViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val requestBodyHelper: RequestBodyHelper
): ViewModel(), TextView.OnEditorActionListener {

    var email = MutableLiveData("")


    //for validation
    var emailError: MutableLiveData<Pair<Boolean, String>> = MutableLiveData(Pair(false, ""))


    //LiveData subclass which may observe other LiveData objects and react on OnChanged events from them
    var errorResetObserver: MediatorLiveData<String> = MediatorLiveData()

    val forgotPasswordResponseLiveData: LiveData<ResponseState<BaseResponse<JsonElement>>>
        get() = authRepository.forgotPasswordResponseLiveData

    init {

        errorResetObserver.addSource(email) {
            emailError.value = Pair(false, "")
        }

    }

    fun continueButtonClick(view: View) {
        if (forgotPasswordValidation(view)) {
            UtilsCommon.hideKeyboard(view)
            forgotPassword(requestBodyHelper.getForgotPasswordRequest(email.value?.trim()))
        }
    }

    private fun forgotPassword(forgotPasswordRequest: JsonObject) {
        viewModelScope.launch {
            authRepository.forgotPassword(forgotPasswordRequest)
        }
    }


    private fun forgotPasswordValidation(view: View): Boolean {
        val result: Boolean
       if (email.value.isNullOrEmpty()) {
            emailError.value =
                Pair(true, view.context.resources.getString(R.string.please_enter_a_registered_email_address))
            result = false
        } else if (!isValidEmail(email.value?.trim())) {
           emailError.value =
               Pair(true, view.context.resources.getString(R.string.please_enter_valid_email_address))
           result = false
       }else {
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
                    return  !forgotPasswordValidation(it)
                }
                EditorInfo.IME_ACTION_DONE -> {
                    it.clearFocus()
                    return !forgotPasswordValidation(it)

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