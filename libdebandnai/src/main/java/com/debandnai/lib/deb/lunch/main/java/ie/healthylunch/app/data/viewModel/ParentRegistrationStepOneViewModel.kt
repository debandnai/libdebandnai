package ie.healthylunch.app.data.viewModel

import android.app.Activity
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Patterns
import android.view.View
import androidx.annotation.RequiresApi
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.Navigation
import com.google.gson.JsonObject
import ie.healthylunch.app.R
import ie.healthylunch.app.data.model.checkEmailModel.CheckemailExistResponse
import ie.healthylunch.app.data.network.Resource
import ie.healthylunch.app.data.repository.ParentRegistrationRepository
import ie.healthylunch.app.ui.LoginActivity
import ie.healthylunch.app.ui.LoginActivity.Companion.loginActivity
import ie.healthylunch.app.ui.LoginRegistrationActivity
import ie.healthylunch.app.ui.RegistrationActivity
import ie.healthylunch.app.utils.AppController
import ie.healthylunch.app.utils.Constants.Companion.CHECK_EMAIL_EXIST
import ie.healthylunch.app.utils.Constants.Companion.REFRESH_TOKEN
import ie.healthylunch.app.utils.MethodClass
import ie.healthylunch.app.utils.SingleLiveEvent
import kotlinx.coroutines.launch
import java.lang.Exception

class ParentRegistrationStepOneViewModel(private val repository: ParentRegistrationRepository) :
    ViewModel() {
    var firstName: MutableLiveData<String> = MutableLiveData()
    var lastName: MutableLiveData<String> = MutableLiveData()
    var email: MutableLiveData<String> = MutableLiveData()
    var phNumber: MutableLiveData<String> = MutableLiveData()

    var firstNameError: MutableLiveData<String> = MutableLiveData()
    var lastNameError: MutableLiveData<String> = MutableLiveData()
    var emailError: MutableLiveData<String> = MutableLiveData()
    var phNumberError: MutableLiveData<String> = MutableLiveData()

    var firstNameErrorVisible: MutableLiveData<Boolean> = MutableLiveData(false)
    var lastNameErrorVisible: MutableLiveData<Boolean> = MutableLiveData(false)
    var emailErrorVisible: MutableLiveData<Boolean> = MutableLiveData(false)
    var phNumberErrorVisible: MutableLiveData<Boolean> = MutableLiveData(false)
    var isSubmitEnabled: MutableLiveData<Boolean> = MutableLiveData(true)

    val bundle = Bundle()

    //Check Email
    private val _checkEmailExistResponse: SingleLiveEvent<Resource<CheckemailExistResponse>?> =
        SingleLiveEvent()
    private var checkEmailExistResponse: LiveData<Resource<CheckemailExistResponse>?>? = null
        get() = _checkEmailExistResponse

    private fun checkEmail(
        jsonObject: JsonObject

    ) = viewModelScope.launch {
        _checkEmailExistResponse.value = repository.checkEmailExistRepository(jsonObject)
    }

      
    fun checkEmailApiCall(activity: Activity) {
        activity as RegistrationActivity

        if (registrationValidation(activity)) {
            isSubmitEnabled.value = false
            bundle.putString("f_name", firstName.value)
            bundle.putString("l_name", lastName.value)
            bundle.putString("email", email.value)
            bundle.putString("ph_no", phNumber.value)
            MethodClass.showProgressDialog(activity)
            val jsonObject = MethodClass.getCommonJsonObject(activity)
            jsonObject.addProperty("email", email.value)
            checkEmail(jsonObject)

        }

    }

      
    fun getSubmitResponse(activity: Activity, view: View) {
        activity as RegistrationActivity
        checkEmailExistResponse?.observe(activity) {

            when (it) {
                is Resource.Success -> {
                    Navigation.findNavController(view)
                        .navigate(
                            R.id.action_parentRegistrationStepOneFragment_to_parentRegistrationStepTwoFragment,
                            bundle
                        )
                    isSubmitEnabled.value = true
                    MethodClass.hideProgressDialog(activity)

                    _checkEmailExistResponse.value = null
                    checkEmailExistResponse = _checkEmailExistResponse


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
                                        CHECK_EMAIL_EXIST,
                                        REFRESH_TOKEN
                                    )
                            else if (it.errorCode == 307 || it.errorCode == 426)
                                MethodClass.showErrorDialog(activity, it1, it.errorCode)
                            else {
                                MethodClass.showErrorDialog(
                                    activity,
                                    it1,
                                    it.errorCode
                                )
                            }


                        }


                    }
                    isSubmitEnabled.value = true

                    _checkEmailExistResponse.value = null
                    checkEmailExistResponse = _checkEmailExistResponse
                }
                else -> {}
            }

        }

    }

    fun back(activity: Activity) {
        //isTaskRoot which return true if current activity is only activity in your stack
        if (activity.isTaskRoot){
            val intent = Intent(activity, LoginRegistrationActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            activity.startActivity(intent)
        }
        else
            activity.finish()


    }


    fun login(activity: Activity) {

        activity.startActivity(Intent(activity, LoginActivity::class.java))
        try {
            loginActivity.finish()
        } catch (e: Exception) {
            e.printStackTrace()
        }


    }

    //validation
    private fun registrationValidation(activity: Activity): Boolean {

        invisibleErrorTexts()

        if (firstName.value.isNullOrBlank()) {
            firstNameError.value = activity.getString(R.string.please_enter_first_name)
            firstNameErrorVisible.value = true
            return false
        }
        if (lastName.value.isNullOrBlank()) {
            lastNameError.value = activity.getString(R.string.please_enter_last_name)
            lastNameErrorVisible.value = true
            return false
        }
        if (email.value.isNullOrBlank()) {
            emailError.value = activity.getString(R.string.please_enter_email_address)
            emailErrorVisible.value = true
            return false
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email.value!!).matches()) {
            emailError.value = activity.getString(R.string.please_enter_valid_email_address)
            emailErrorVisible.value = true
            return false
        }
        if (phNumber.value.isNullOrBlank()) {
            phNumberError.value = activity.getString(R.string.please_enter_phone_number)
            phNumberErrorVisible.value = true
            return false
        }
        return true
    }

    //error texts primary state
    fun invisibleErrorTexts() {
        firstNameErrorVisible.value = false
        lastNameErrorVisible.value = false
        emailErrorVisible.value = false
        phNumberErrorVisible.value = false

    }


}