package com.salonsolution.app.data.viewModel

import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.TextView
import androidx.lifecycle.*
import com.google.gson.JsonObject
import com.salonsolution.app.R
import com.salonsolution.app.data.network.RequestBodyHelper
import com.salonsolution.app.data.repository.AuthRepository
import com.salonsolution.app.data.repository.UserRepository
import com.salonsolution.app.utils.Constants
import com.salonsolution.app.utils.UtilsCommon
import com.salonsolution.app.utils.UtilsCommon.isValidEmail
import com.salonsolution.app.utils.UtilsCommon.isValidPhone
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SignUpViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val userRepository: UserRepository,
    private val requestBodyHelper: RequestBodyHelper
) : ViewModel(), TextView.OnEditorActionListener {

    var firstName = MutableLiveData("")
    var lastName = MutableLiveData("")
    var email = MutableLiveData("")
    var countryCode = MutableLiveData("")
    var phone = MutableLiveData("")
    var password = MutableLiveData("")
    var confirmPassword = MutableLiveData("")



    //for validation
    var firstNameError: MutableLiveData<Pair<Boolean, String>> = MutableLiveData(Pair(false, ""))
    var lastNameError: MutableLiveData<Pair<Boolean, String>> = MutableLiveData(Pair(false, ""))
    var emailError: MutableLiveData<Pair<Boolean, String>> = MutableLiveData(Pair(false, ""))
    var phoneError: MutableLiveData<Pair<Boolean, String>> = MutableLiveData(Pair(false, ""))
    var passwordError: MutableLiveData<Pair<Boolean, String>> = MutableLiveData(Pair(false, ""))
    var confirmPasswordError: MutableLiveData<Pair<Boolean, String>> =
        MutableLiveData(Pair(false, ""))

    //LiveData subclass which may observe other LiveData objects and react on OnChanged events from them
    var errorResetObserver: MediatorLiveData<String> = MediatorLiveData()

    val registerResponseLiveData = authRepository.registerResponseLiveData
    val countriesDetailsLiveData = userRepository.countriesDetailsLiveData

    init {
        errorResetObserver.addSource(firstName) {
            firstNameError.value = Pair(false, "")
        }
        errorResetObserver.addSource(lastName) {
            lastNameError.value = Pair(false, "")
        }
        errorResetObserver.addSource(email) {
            emailError.value = Pair(false, "")
        }
        errorResetObserver.addSource(phone) {
            phoneError.value = Pair(false, "")
        }
        errorResetObserver.addSource(password) {
            passwordError.value = Pair(false, "")
        }
        errorResetObserver.addSource(confirmPassword) {
            confirmPasswordError.value = Pair(false, "")
        }
        loadCountries()
    }

    fun signUpButtonClick(view: View) {
        if (registerFormValidation(view)) {
            UtilsCommon.hideKeyboard(view)
            register(
                requestBodyHelper.getRegisterRequest(
                    firstName.value?.trim(),
                    lastName.value?.trim(),
                    email.value?.trim(),
                    phone.value?.trim(),
                    countryCode.value?.trim(),
                    password.value?.trim()
                )
            )
        }
    }

    private fun register(registerRequest: JsonObject) {
        viewModelScope.launch {
            authRepository.register(registerRequest)
        }
    }

    private fun loadCountries() {
        viewModelScope.launch {
            userRepository.countries(requestBodyHelper.getCountriesRequest())
        }
    }


    private fun registerFormValidation(view: View): Boolean {
        val result: Boolean
        if (firstName.value.isNullOrEmpty()) {
            firstNameError.value =
                Pair(true, view.context.resources.getString(R.string.please_enter_first_name))
            result = false
        } else if (lastName.value.isNullOrEmpty()) {
            lastNameError.value =
                Pair(true, view.context.resources.getString(R.string.please_enter_last_name))
            result = false
        } else if (email.value.isNullOrEmpty()) {
            emailError.value =
                Pair(true, view.context.resources.getString(R.string.please_enter_email_address))
            result = false
        } else if (!isValidEmail(email.value?.trim())) {
            emailError.value =
                Pair(true, view.context.resources.getString(R.string.please_enter_valid_email_address))
            result = false
        } else if (!phone.value.isNullOrEmpty() && !isValidPhone(phone.value?.trim())) {
            phoneError.value =
                Pair(
                    true,
                    view.context.resources.getString(R.string.please_enter_valid_phone_number)
                )
            result = false
        } else if (password.value.isNullOrEmpty()) {
            passwordError.value =
                Pair(true, view.context.resources.getString(R.string.please_set_password))
            result = false
        } else if ((password.value?.length ?: 0) < Constants.MINIMUM_PASSWORD) {
            passwordError.value = Pair(
                true,
                view.context.resources.getString(R.string.password_minimum_characters_validation)
            )
            result = false
        } else if (confirmPassword.value.isNullOrEmpty()) {
            confirmPasswordError.value =
                Pair(true, view.context.resources.getString(R.string.please_set_confirm_password))
            result = false
        } else if (password.value != confirmPassword.value) {
            confirmPasswordError.value =
                Pair(true, view.context.resources.getString(R.string.password_not_match))
            result = false
        } else {
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
                    return !registerFormValidation(it)
                }
                EditorInfo.IME_ACTION_DONE -> {
                    it.clearFocus()
                    return !registerFormValidation(it)

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