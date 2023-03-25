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
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ResetPasswordViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val requestBodyHelper: RequestBodyHelper
): ViewModel(), TextView.OnEditorActionListener {

    var email = MutableLiveData("")
    var newPassword = MutableLiveData("")
    var confirmPassword = MutableLiveData("")

    //for validation
    var newPasswordError: MutableLiveData<Pair<Boolean, String>> = MutableLiveData(Pair(false, ""))
    var confirmPasswordError: MutableLiveData<Pair<Boolean, String>> = MutableLiveData(Pair(false, ""))


    //LiveData subclass which may observe other LiveData objects and react on OnChanged events from them
    var errorResetObserver: MediatorLiveData<String> = MediatorLiveData()

    val resetPasswordResponseLiveData: LiveData<ResponseState<BaseResponse<JsonElement>>>
        get() = authRepository.resetPasswordResponseLiveData

    init {

        errorResetObserver.addSource(newPassword) {
            newPasswordError.value = Pair(false, "")
        }
        errorResetObserver.addSource(confirmPassword) {
            confirmPasswordError.value = Pair(false, "")
        }

    }

    fun resetButtonClick(view: View) {
        if (resetPasswordValidation(view)) {
            UtilsCommon.hideKeyboard(view)
            resetPassword(requestBodyHelper.getResetPasswordRequest(email.value?.trim(), newPassword.value?.trim()))
        }
    }

    private fun resetPassword(resetPasswordRequest: JsonObject) {
        viewModelScope.launch {
            authRepository.resetPassword(resetPasswordRequest)
        }
    }


    private fun resetPasswordValidation(view: View): Boolean {
        val result: Boolean
        if (newPassword.value.isNullOrEmpty()) {
           newPasswordError.value =
               Pair(true, view.context.resources.getString(R.string.please_set_password))
           result = false
       } else if ((newPassword.value?.length?:0)<3) {
           newPasswordError.value = Pair(true, view.context.resources.getString(R.string.password_minimum_characters_validation))
           result = false
       } else if (confirmPassword.value.isNullOrEmpty()) {
           confirmPasswordError.value =
               Pair(true, view.context.resources.getString(R.string.please_set_confirm_password))
           result = false
       }else if (newPassword.value!=confirmPassword.value) {
           confirmPasswordError.value =
               Pair(true, view.context.resources.getString(R.string.password_not_match))
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
                    return  !resetPasswordValidation(it)
                }
                EditorInfo.IME_ACTION_DONE -> {
                    it.clearFocus()
                    return !resetPasswordValidation(it)

                }
                else -> {
                    return false
                }
            }
        }

        return false
    }

}