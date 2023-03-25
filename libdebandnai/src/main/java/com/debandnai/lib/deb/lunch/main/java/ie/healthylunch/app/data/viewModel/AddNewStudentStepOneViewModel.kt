package ie.healthylunch.app.data.viewModel

import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavOptions
import androidx.navigation.Navigation
import com.google.gson.JsonObject
import ie.healthylunch.app.R
import ie.healthylunch.app.data.model.countryModel.CountyResponse
import ie.healthylunch.app.data.model.isLoginModel.IsLogin
import ie.healthylunch.app.data.model.schoolListModel.SchoolListResponse
import ie.healthylunch.app.data.network.Resource
import ie.healthylunch.app.data.repository.StudentRepository
import ie.healthylunch.app.fragment.registration.AddStudentNewStepOneFragment.Companion.countyBottomDialog
import ie.healthylunch.app.fragment.registration.AddStudentNewStepOneFragment.Companion.schoolBottomDialog
import ie.healthylunch.app.ui.DashBoardActivity
import ie.healthylunch.app.ui.LoginActivity
import ie.healthylunch.app.utils.*
import ie.healthylunch.app.utils.Constants.Companion.ADD_ANOTHER_STUDENT
import ie.healthylunch.app.utils.Constants.Companion.HAS_STUDENT_ADDED
import ie.healthylunch.app.utils.Constants.Companion.LAST_ADDED_STUDENT_NAME
import kotlinx.coroutines.launch

class AddNewStudentStepOneViewModel(private val repository: StudentRepository) : ViewModel(),
    DialogYesNoListener {
    var errorVisibleSchoolName: MutableLiveData<Boolean> = MutableLiveData(false)
    var errorVisibleCountyName: MutableLiveData<Boolean> = MutableLiveData(false)
    var errorSchoolName: MutableLiveData<String> = MutableLiveData("")
    var errorCountyName: MutableLiveData<String> = MutableLiveData("")
    var county: MutableLiveData<String> = MutableLiveData("County")
    var schoolName: MutableLiveData<String> = MutableLiveData("School")
    var schoolType: MutableLiveData<Int> = MutableLiveData()
    var schoolId: MutableLiveData<Int> = MutableLiveData(0)
    var countyId: Int = 0

    var isSubmitEnabled: MutableLiveData<Boolean> = MutableLiveData(true)

    //School List
    val _schoolListResponse: SingleLiveEvent<Resource<SchoolListResponse>> =
        SingleLiveEvent()
    var schoolListResponse: SingleLiveEvent<Resource<SchoolListResponse>>? = null
        get() = _schoolListResponse


    fun schoolList(
        jsonObject: JsonObject,
        token: String
    ) = viewModelScope.launch {

        _schoolListResponse.value = repository.schoolList(jsonObject, token)
    }


    //get Country
    val _countyListResponse: SingleLiveEvent<Resource<CountyResponse>> =
        SingleLiveEvent()
    var countyListResponse: SingleLiveEvent<Resource<CountyResponse>>? = null
        get() = _countyListResponse

    fun countryListApiCall(
        jsonObject: JsonObject,
        token: String
    ) = viewModelScope.launch {
        _countyListResponse.value = repository.countryList(jsonObject, token)
    }

    fun countyOnClick() {
        countyBottomDialog?.show()

        errorVisibleCountyName.value = false
    }


    fun back(activity: Activity, view: View) {
        if (ADD_ANOTHER_STUDENT) {
            ADD_ANOTHER_STUDENT = false
            val bundle = Bundle()

            bundle.putString("studentName", LAST_ADDED_STUDENT_NAME)
            bundle.putString("studentFirstName", LAST_ADDED_STUDENT_NAME)

            if (!HAS_STUDENT_ADDED) {
                val navOptions: NavOptions =
                    NavOptions.Builder().setPopUpTo(R.id.addStudentSuccessFragment, true).build()

                Navigation.findNavController(view)
                    .navigate(
                        R.id.action_addStudentNewStepOneFragment_to_addStudentSuccessFragment,
                        bundle, navOptions
                    )
            } else {
                val intent = Intent(view.context, DashBoardActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                view.context.startActivity(intent)
                HAS_STUDENT_ADDED = false
            }
        } else {
            val isLogin: IsLogin? = UserPreferences.getAsObject<IsLogin>(
                activity,
                Constants.LOGIN_CHECK
            )
            if (isLogin?.isLogin != 1) {
                CustomDialog.showYesNoTypeDialog(
                    activity,
                    activity.resources.getString(R.string.do_you_want_to_exit_this_page_),
                    this
                )
            } else {
                val intent = Intent(view.context, DashBoardActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                view.context.startActivity(intent)
                HAS_STUDENT_ADDED = false

            }

        }


    }

    fun schoolOnClick() {

        schoolBottomDialog?.show()
        errorVisibleSchoolName.value = false
    }

    fun getStarted(activity: Activity, view: View) {
        val bundle = Bundle()
        bundle.putString("schoolName", schoolName.value)
        schoolId.value?.let { bundle.putInt("schoolId", it) }
        if (validate(activity)) {
            isSubmitEnabled.value = false
            if (schoolType.value == 1) {

                Navigation.findNavController(view)
                    .navigate(
                        R.id.action_addStudentNewStepOneFragment_to_addStudentNewStepTwoFragment,
                        bundle
                    )
            } else {
                Navigation.findNavController(view)
                    .navigate(
                        R.id.action_addStudentNewStepOneFragment_to_deisStudentUniqueCodeFragment,
                        bundle
                    )
            }
        }
    }

    fun validate(activity: Activity): Boolean {
        if (county.value == "County") {
            errorVisibleCountyName.value = true
            errorCountyName.value = activity.getString(R.string.please_select_your_county)
            return false
        } else {
            errorVisibleCountyName.value = false
        }

        if (county.value.isNullOrBlank()) {
            errorVisibleCountyName.value = true
            errorCountyName.value = activity.getString(R.string.please_select_your_county)
            return false
        } else
            errorVisibleSchoolName.value = false

        if (schoolName.value.isNullOrBlank()) {
            errorVisibleSchoolName.value = true
            errorSchoolName.value = activity.getString(R.string.please_select_your_school)
            return false
        } else
            errorVisibleSchoolName.value = false
        if (schoolName.value == "School") {
            errorSchoolName.value = activity.getString(R.string.please_select_your_school)
            errorVisibleSchoolName.value = true
            return false
        } else
            errorVisibleSchoolName.value = false
        return true
    }

    override fun yesOnClick(dialog: Dialog, activity: Activity) {
        dialog.dismiss()
        ADD_ANOTHER_STUDENT = false
        if (AppController.getInstance().isDeActivatedAllStudents) { //checking if all students is deleted or not 'isDeActivatedAllStudents' is the variable in application class
            val intent = Intent(activity, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            activity.startActivity(intent)
        } else
            activity.finish()
    }

    override fun noOnClick(dialog: Dialog, activity: Activity) {
        dialog.dismiss()
    }


}
