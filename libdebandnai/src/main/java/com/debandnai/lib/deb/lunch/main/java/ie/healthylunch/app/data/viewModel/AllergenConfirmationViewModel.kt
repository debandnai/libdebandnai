package ie.healthylunch.app.data.viewModel

import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.navigation.Navigation
import ie.healthylunch.app.R
import ie.healthylunch.app.data.model.isLoginModel.IsLogin
import ie.healthylunch.app.fragment.registration.AllergenConfirmationFragment
import ie.healthylunch.app.ui.DashBoardActivity
import ie.healthylunch.app.utils.Constants
import ie.healthylunch.app.utils.Constants.Companion.ADD_ANOTHER_STUDENT
import ie.healthylunch.app.utils.Constants.Companion.CHECK_REGISTRATION
import ie.healthylunch.app.utils.Constants.Companion.HAS_STUDENT_ADDED
import ie.healthylunch.app.utils.Constants.Companion.LOGIN_CHECK
import ie.healthylunch.app.utils.CustomDialog
import ie.healthylunch.app.utils.DialogYesNoListener
import ie.healthylunch.app.utils.UserPreferences


class AllergenConfirmationViewModel() :
        ViewModel(){
    var promotionAlert: MutableLiveData<String> = MutableLiveData()
    var yesRadioButton: MutableLiveData<Boolean> = MutableLiveData(false)
    var noRadioButton: MutableLiveData<Boolean> = MutableLiveData(false)
    var promotionAlertErrorVisible: MutableLiveData<Boolean> = MutableLiveData(false)
    var studentId: MutableLiveData<Int> = MutableLiveData(0)
    var userType: MutableLiveData<String> = MutableLiveData()
    var studentFirstName: MutableLiveData<String> = MutableLiveData("")
    var pageFrom: MutableLiveData<String> = MutableLiveData("")
    var promotionAlertError: MutableLiveData<String> = MutableLiveData("")
    var isSubmitEnabled: MutableLiveData<Boolean> = MutableLiveData(true)
    lateinit var allergenConfirmationFragment: AllergenConfirmationFragment



    fun promotionOnClick(promotionAlertInt: Int) {
        invisibleErrorTexts()

        if (promotionAlertInt == 1) {
            promotionAlert.value = "y"
            yesRadioButton.value = true
            noRadioButton.value = false
        } else {
            promotionAlert.value = "n"
            yesRadioButton.value = false
            noRadioButton.value = true
        }


    }



    //error texts primary state
    fun invisibleErrorTexts() {

        promotionAlertErrorVisible.value = false
    }






}