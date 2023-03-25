package com.salonsolution.app.data.viewModel

import android.net.Uri
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.TextView
import androidx.lifecycle.*
import com.google.gson.JsonObject
import com.salonsolution.app.BuildConfig
import com.salonsolution.app.R
import com.salonsolution.app.data.model.ProfileDetailsModel
import com.salonsolution.app.data.network.RequestBodyHelper
import com.salonsolution.app.data.repository.UserRepository
import com.salonsolution.app.utils.Constants
import com.salonsolution.app.utils.UtilsCommon
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

@HiltViewModel
class MyProfileViewModel @Inject constructor(
    private val repository: UserRepository,
    private val requestBodyHelper: RequestBodyHelper,
) : ViewModel(), TextView.OnEditorActionListener {


    var firstName = MutableLiveData("")
    var lastName = MutableLiveData("")
    var email = MutableLiveData("")
    var phone = MutableLiveData("")
    var address1 = MutableLiveData("")
    var address2 = MutableLiveData("")
    var profileImageUrl = MutableLiveData("")
    var profileImageUri: MutableLiveData<Uri?> = MutableLiveData(null)
    var path = MutableLiveData("")
    var size = MutableLiveData("")
    var isFromCamera = false // image select from camera or gallery
    var isShowLoader = MutableLiveData(false) //when file copy show loader
    var selectedCountryCode = MutableLiveData("")

    //for validation
    var firstNameError: MutableLiveData<Pair<Boolean, String>> = MutableLiveData(Pair(false, ""))
    var lastNameError: MutableLiveData<Pair<Boolean, String>> = MutableLiveData(Pair(false, ""))
    var emailError: MutableLiveData<Pair<Boolean, String>> = MutableLiveData(Pair(false, ""))
    var phoneError: MutableLiveData<Pair<Boolean, String>> = MutableLiveData(Pair(false, ""))
    var address1Error: MutableLiveData<Pair<Boolean, String>> = MutableLiveData(Pair(false, ""))
    var address2Error: MutableLiveData<Pair<Boolean, String>> = MutableLiveData(Pair(false, ""))
    var fileError: MutableLiveData<Pair<Boolean, String>> = MutableLiveData(Pair(false, ""))

    //LiveData subclass which may observe other LiveData objects and react on OnChanged events from them
    var errorResetObserver: MediatorLiveData<String> = MediatorLiveData()

    val updateProfileLiveData= repository.updateProfileLiveData
    val profileDetailsLiveData= repository.profileDetailsLiveData
    val countriesDetailsLiveData = repository.countriesDetailsLiveData

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
        errorResetObserver.addSource(address1) {
            address1Error.value = Pair(false, "")
        }
        errorResetObserver.addSource(address2) {
            address2Error.value = Pair(false, "")
        }
        errorResetObserver.addSource(size) {
            fileError.value = Pair(false, "")
        }

        loadCountries()
    }

    fun getProfileDetails(profileDetailsRequest:JsonObject){
        viewModelScope.launch {
            repository.profileDetails(profileDetailsRequest)
        }
    }

    private fun loadCountries() {
        viewModelScope.launch {
            repository.countries(requestBodyHelper.getCountriesRequest())
        }
    }

    fun saveButtonClick(view: View) {
        if (myProfileFormValidation(view)) {
            UtilsCommon.hideKeyboard(view)
            save()
        }
    }

    private fun save() {
        viewModelScope.launch {
            repository.updateProfile(
                firstName.value ?: "",
                lastName.value ?: "",
                phone.value ?: "",
                selectedCountryCode.value ?: "",
                email.value ?: "",
                address1.value ?: "",
                address2.value?:"",
                "and",
                BuildConfig.VERSION_NAME,
                path.value ?: ""
            )
        }
    }


    private fun myProfileFormValidation(view: View): Boolean {
        val file = File(path.value ?: "")
        val size = file.length() / 1024.0

        val result: Boolean
        if (firstName.value.isNullOrEmpty()) {
            firstNameError.value =
                Pair(true, view.context.resources.getString(R.string.please_enter_first_name))
            result = false
        } else  if (lastName.value.isNullOrEmpty()) {
            lastNameError.value =
                Pair(true, view.context.resources.getString(R.string.please_enter_last_name))
            result = false
        }
        else if (!phone.value.isNullOrEmpty() && !UtilsCommon.isValidPhone(phone.value?.trim())) {
            phoneError.value =
                Pair(
                    true,
                    view.context.resources.getString(R.string.please_enter_valid_phone_number)
                )
            result = false
        } else if (email.value.isNullOrEmpty()) {
            emailError.value =
                Pair(true, view.context.resources.getString(R.string.please_enter_email_address))
            result = false
        } else if (!UtilsCommon.isValidEmail(email.value?.trim())) {
            emailError.value =
                Pair(true, view.context.resources.getString(R.string.please_enter_valid_email_address))
            result = false
        }  else if (address1.value.isNullOrEmpty()) {
            address1Error.value =
                Pair(true, view.context.resources.getString(R.string.please_enter_address1))
            result = false
        } else if (size > Constants.FILE_UPLOAD_SIZE) {
            fileError.value =
                Pair(
                    true,
                    view.context.resources.getString(R.string.the_maximum_size_for_file_uploads_is_5mb)
                )
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
                    return !myProfileFormValidation(it)
                }
                EditorInfo.IME_ACTION_DONE -> {
                    it.clearFocus()
                    return !myProfileFormValidation(it)

                }
                else -> {
                    return false
                }
            }
        }

        return false
    }

    fun initProfileData(profileDetailsModel: ProfileDetailsModel?) {

        profileDetailsModel?.let {
            firstName.value = it.firstName
            lastName.value = it.lastName
            profileImageUrl.value = it.image
            phone.value = it.phone
            selectedCountryCode.value = it.countryCode
            email.value = it.email
            address1.value = it.address1
            address2.value = it.address2
            selectedCountryCode.value = it.countryCode
        }

    }

}