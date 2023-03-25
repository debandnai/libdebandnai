package ie.healthylunch.app.data.viewModel

import android.app.Activity
import android.content.Intent
import android.os.Build
import android.view.View
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.JsonObject
import ie.healthylunch.app.R
import ie.healthylunch.app.data.model.changePasswordModel.ParentchangePasswordResponse
import ie.healthylunch.app.data.model.loginModel.LoginResponse
import ie.healthylunch.app.data.network.Resource
import ie.healthylunch.app.data.repository.ChangePasswordActivityRepository
import ie.healthylunch.app.ui.ChangePasswordActivity
import ie.healthylunch.app.ui.DashBoardActivity
import ie.healthylunch.app.utils.AppController
import ie.healthylunch.app.utils.Constants
import ie.healthylunch.app.utils.Constants.Companion.PARENT_CHANGE_PASSWORD
import ie.healthylunch.app.utils.Constants.Companion.REFRESH_TOKEN
import ie.healthylunch.app.utils.Constants.Companion.TOKEN
import ie.healthylunch.app.utils.MethodClass
import ie.healthylunch.app.utils.UserPreferences
import ie.healthylunch.app.utils.SingleLiveEvent
import kotlinx.coroutines.launch
import java.util.*

class ChangePasswordActivityViewModel(private val repository: ChangePasswordActivityRepository) :
    ViewModel() {
    var oldPassWordTv: MutableLiveData<String> = MutableLiveData()
    var newPassWordTv: MutableLiveData<String> = MutableLiveData()
    var changePassWordTv: MutableLiveData<String> = MutableLiveData()


    var oldPassError: MutableLiveData<String> = MutableLiveData()
    var newPassError: MutableLiveData<String> = MutableLiveData()
    var changePassError: MutableLiveData<String> = MutableLiveData()


    var oldErrorVisible: MutableLiveData<Boolean> = MutableLiveData(false)
    var newErrorVisible: MutableLiveData<Boolean> = MutableLiveData(false)
    var changeErrorVisible: MutableLiveData<Boolean> = MutableLiveData(false)

    //Change Password
    private val _parentChangePasswordResponse: SingleLiveEvent<Resource<ParentchangePasswordResponse>?> =
        SingleLiveEvent()
    private var parentChangePasswordResponse: LiveData<Resource<ParentchangePasswordResponse>?>? =
        null
        get() = _parentChangePasswordResponse

    private fun parentChangePassword(
        jsonObject: JsonObject,
        token: String
    ) = viewModelScope.launch {
        _parentChangePasswordResponse.value =
            repository.parentChangePasswordRepository(jsonObject, token)
    }


    fun changePassWord(activity: Activity) {
        activity as ChangePasswordActivity
        val loginResponse: LoginResponse? =
            UserPreferences.getAsObject<LoginResponse>(
                activity,
                Constants.USER_DETAILS
            )

        loginResponse?.response?.raws?.data?.token?.let { TOKEN = it }
        REFRESH_TOKEN = loginResponse?.response?.raws?.data?.refreshToken.toString()
        val jsonObject = MethodClass.getCommonJsonObject(activity)
        jsonObject.addProperty("password", newPassWordTv.value)
        jsonObject.addProperty("oldpassword", oldPassWordTv.value)
        jsonObject.addProperty("confirmpassword", changePassWordTv.value)

        MethodClass.showProgressDialog(activity)
        parentChangePassword(jsonObject, TOKEN)

    }

      
    fun getChangePasswordResponse(activity: Activity) {
        activity as ChangePasswordActivity
        parentChangePasswordResponse?.observe(activity) {

            when (it) {

                is Resource.Success -> {
                    MethodClass.showProgressDialog(activity)
                    Toast.makeText(
                        activity,
                        it.value.response.raws.successMessage,
                        Toast.LENGTH_SHORT
                    ).show()

                    _parentChangePasswordResponse.value = null
                    parentChangePasswordResponse = _parentChangePasswordResponse
                    //cal logout api after change password completed
                    logoutUser(activity)

                }
                is Resource.Failure -> {
                    if (it.errorBody != null) {
                        MethodClass.hideProgressDialog(activity)
                        if (it.errorCode == 401)
                            AppController.getInstance()
                                .refreshTokenResponse(
                                    activity,
                                    null,
                                    PARENT_CHANGE_PASSWORD,
                                    REFRESH_TOKEN
                                )
                        else {
                            it.errorString?.let { it1 ->
                                MethodClass.showErrorDialog(
                                    activity,
                                    it1,
                                    it.errorCode
                                )
                            }


                        }
                    }
                    _parentChangePasswordResponse.value = null
                    parentChangePasswordResponse = _parentChangePasswordResponse
                }
                else -> {}
            }

        }

    }


    fun logoutUser(activity: ChangePasswordActivity) {
        AppController.getInstance()
            .logoutAppController(
                activity, true
            )

    }

    //validation
    private fun validation(view: View): Boolean {

        invisibleErrorTexts()

        if (oldPassWordTv.value.isNullOrBlank()) {
            oldPassError.value = view.context.getString(R.string.please_enter_old_password)
            oldErrorVisible.value = true
            return false
        }
        if (oldPassWordTv.value!!.length < 6) {
            oldPassError.value = view.context.getString(R.string.please_enter_minimum_6_characters)
            oldErrorVisible.value = true
            return false
        }

        if (newPassWordTv.value.isNullOrBlank()) {
            newPassError.value = view.context.getString(R.string.Please_enter_new_password)
            newErrorVisible.value = true
            return false
        }
        if (newPassWordTv.value!!.length < 6) {
            newPassError.value = view.context.getString(R.string.please_enter_minimum_6_characters)
            newErrorVisible.value = true
            return false
        }
        if (Objects.equals(newPassWordTv.value, oldPassWordTv.value)) {

            newPassError.value =
                view.context.getString(R.string.new_Password_can_not_be_same_as_Old_Password)
            newErrorVisible.value = true
            return false
        }

        if (changePassWordTv.value.isNullOrBlank()) {
            changePassError.value = view.context.getString(R.string.enter_confirm_password)
            changeErrorVisible.value = true
            return false
        }

        if (changePassWordTv.value!!.length < 6) {
            changePassError.value =
                view.context.getString(R.string.please_enter_minimum_6_characters)
            changeErrorVisible.value = true
            return false
        }
        if (!Objects.equals(changePassWordTv.value, newPassWordTv.value)) {

            changePassError.value = view.context.getString(R.string.Password_not_match)
            changeErrorVisible.value = true
            return false
        }
        return true
    }

    //error texts primary state
    fun invisibleErrorTexts() {
        oldErrorVisible.value = false
        newErrorVisible.value = false
        changeErrorVisible.value = false


    }

      
    fun save(activity: Activity, view: View) {
        activity as ChangePasswordActivity
        if (validation(view)) {
            changePassWord(activity)
        }
    }


}