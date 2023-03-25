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
import com.salonsolution.app.data.repository.UserRepository
import com.salonsolution.app.utils.Constants
import com.salonsolution.app.utils.UtilsCommon
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChangePasswordViewModel @Inject constructor(
    private val repository: UserRepository,
    private val requestBodyHelper: RequestBodyHelper
) : ViewModel(), TextView.OnEditorActionListener {


    var oldPassword = MutableLiveData("")
    var newPassword = MutableLiveData("")
    var confirmPassword = MutableLiveData("")


    //for validation
    var oldPasswordError: MutableLiveData<Pair<Boolean, String>> = MutableLiveData(Pair(false, ""))
    var newPasswordError: MutableLiveData<Pair<Boolean, String>> = MutableLiveData(Pair(false, ""))
    var confirmPasswordError: MutableLiveData<Pair<Boolean, String>> = MutableLiveData(Pair(false, ""))


    //LiveData subclass which may observe other LiveData objects and react on OnChanged events from them
    var errorResetObserver: MediatorLiveData<String> = MediatorLiveData()

    val updatePasswordLiveData: LiveData<ResponseState<BaseResponse<JsonElement>>>
        get() = repository.updatePasswordLiveData

    init {
        errorResetObserver.addSource(oldPassword) {
            oldPasswordError.value = Pair(false, "")
        }
        errorResetObserver.addSource(newPassword) {
            newPasswordError.value = Pair(false, "")
        }
        errorResetObserver.addSource(confirmPassword) {
            confirmPasswordError.value = Pair(false, "")
        }
    }

    fun saveButtonClick(view: View) {
        if (myPasswordFormValidation(view)) {
            UtilsCommon.hideKeyboard(view)
            save(requestBodyHelper.updatePasswordRequest(oldPassword.value?.trim(), newPassword.value?.trim())
            )
        }
    }

    private fun save(updatePasswordRequest: JsonObject) {
        viewModelScope.launch {
            repository.updatePassword(updatePasswordRequest)
        }
    }


    private fun myPasswordFormValidation(view: View): Boolean {
        val result: Boolean
        if (oldPassword.value.isNullOrEmpty()) {
            oldPasswordError.value =
                Pair(true, view.context.resources.getString(R.string.please_enter_old_password))
            result = false
        } else if (newPassword.value.isNullOrEmpty()) {
            newPasswordError.value =
                Pair(true, view.context.resources.getString(R.string.please_set_password))
            result = false
        } else if ((newPassword.value?.length?:0)<Constants.MINIMUM_PASSWORD) {
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
            Log.d("tag", "id: ${it.id}")
            when (actionId) {
                EditorInfo.IME_ACTION_NEXT -> {
                    it.clearFocus()
                    return !myPasswordFormValidation(it)
                }
                EditorInfo.IME_ACTION_DONE -> {
                    it.clearFocus()
                    return !myPasswordFormValidation(it)

                }
                else -> {
                    return false
                }
            }
        }

        return false
    }

}