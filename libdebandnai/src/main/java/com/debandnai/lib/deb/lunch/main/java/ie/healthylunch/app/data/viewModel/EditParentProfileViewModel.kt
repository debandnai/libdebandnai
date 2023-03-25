package ie.healthylunch.app.data.viewModel

import android.app.Activity
import android.os.Build
import android.util.Patterns
import android.view.View
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.JsonObject
import ie.healthylunch.app.R
import ie.healthylunch.app.data.model.editProfileModel.EditProfileResponse
import ie.healthylunch.app.data.model.loginModel.LoginResponse
import ie.healthylunch.app.data.model.parentDetailsModel.ParentDetailsResponse
import ie.healthylunch.app.data.network.Resource
import ie.healthylunch.app.data.repository.EditParentProfileRepository
import ie.healthylunch.app.ui.EditParentProfileActivity
import ie.healthylunch.app.utils.*
import ie.healthylunch.app.utils.Constants.Companion.EDIT_PROFILE
import ie.healthylunch.app.utils.Constants.Companion.REFRESH_TOKEN
import ie.healthylunch.app.utils.Constants.Companion.TOKEN
import kotlinx.coroutines.launch


class EditParentProfileViewModel(private val repository: EditParentProfileRepository) :
    ViewModel() {


    var firstNameTv: MutableLiveData<String> = MutableLiveData()
    var lastNameTv: MutableLiveData<String> = MutableLiveData()
    var emailTv: MutableLiveData<String> = MutableLiveData()
    var phoneTv: MutableLiveData<String> = MutableLiveData()
    var parentDetails: ParentDetailsResponse? = null


    var firstNameError: MutableLiveData<String> = MutableLiveData()
    var lastNameError: MutableLiveData<String> = MutableLiveData()
    var emailError: MutableLiveData<String> = MutableLiveData()
    var phoneError: MutableLiveData<String> = MutableLiveData()


    var firstNameErrorVisible: MutableLiveData<Boolean> = MutableLiveData(false)
    var lastNameErrorVisible: MutableLiveData<Boolean> = MutableLiveData(false)
    var emailErrorVisible: MutableLiveData<Boolean> = MutableLiveData(false)
    var phoneErrorVisible: MutableLiveData<Boolean> = MutableLiveData(true)


    //Edit Profile
    private val _editProfileResponse: SingleLiveEvent<Resource<EditProfileResponse>?> =
        SingleLiveEvent()
    private var editProfileResponse: LiveData<Resource<EditProfileResponse>?>? = null
        get() = _editProfileResponse

    fun editProfile(
        jsonObject: JsonObject,
        token: String
    ) = viewModelScope.launch {
        _editProfileResponse.value = repository.editProfileApiRepository(jsonObject, token)
    }




      
    fun getSaveProfileResponse(activity: Activity) {
        activity as EditParentProfileActivity
        editProfileResponse?.observe(activity) {

            when (it) {

                is Resource.Success -> {
                    MethodClass.hideProgressDialog(activity)
                    Toast.makeText(
                        activity,
                        it.value.response.raws.successMessage,
                        Toast.LENGTH_SHORT
                    ).show()
                    activity.finish()
                    _editProfileResponse.value = null
                    editProfileResponse = _editProfileResponse

                }
                is Resource.Failure -> {
                    if (it.errorBody != null) {
                        MethodClass.hideProgressDialog(activity)
                        it.errorString?.let { it1 ->
                            if (it.errorCode == 401)
                                AppController.getInstance()
                                    .refreshTokenResponse(
                                        activity,
                                        null,
                                        EDIT_PROFILE,
                                        REFRESH_TOKEN
                                    )
                            else {
                                MethodClass.showErrorDialog(
                                    activity,
                                    it1,
                                    it.errorCode
                                )
                            }

                        }


                    }
                    _editProfileResponse.value = null
                    editProfileResponse = _editProfileResponse
                }
                else -> {}
            }

        }

    }

    //validation


    //error texts primary state
    fun invisibleErrorTexts() {
        firstNameErrorVisible.value = false
        lastNameErrorVisible.value = false
        emailErrorVisible.value = false
        phoneErrorVisible.value = false
    }
}
