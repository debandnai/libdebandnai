package ie.healthylunch.app.data.viewModel

import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.JsonObject
import ie.healthylunch.app.R
import ie.healthylunch.app.data.model.editStudentModel.EditStudentResponse
import ie.healthylunch.app.data.network.Resource
import ie.healthylunch.app.data.repository.DeisStudentSchoolDetailsRepository
import ie.healthylunch.app.utils.Constants.Companion.STUDENT
import ie.healthylunch.app.utils.SingleLiveEvent
import kotlinx.coroutines.launch

class DeisStudentSchoolDetailsViewModel(private val repository: DeisStudentSchoolDetailsRepository) :
    ViewModel() {
    var schoolName: MutableLiveData<String> = MutableLiveData()
    var schoolAddress: MutableLiveData<String> = MutableLiveData()
    var className: MutableLiveData<String> = MutableLiveData()
    var studentFirstName: MutableLiveData<String> = MutableLiveData()
    var studentLastName: MutableLiveData<String> = MutableLiveData()
    var studentFirstNameError: MutableLiveData<String> = MutableLiveData()
    var studentLastNameError: MutableLiveData<String> = MutableLiveData()
    var userType: MutableLiveData<String> = MutableLiveData(STUDENT)
    var studentFirstNameErrorVisible: MutableLiveData<Boolean> = MutableLiveData(false)
    var studentLastNameErrorVisible: MutableLiveData<Boolean> = MutableLiveData(false)
    var studentId: Int? = 0
    var classId: Int? = 0

    var isContinueEnabled: MutableLiveData<Boolean> = MutableLiveData(true)


    //Edit Student
    val _editStudentResponse: SingleLiveEvent<Resource<EditStudentResponse>?> =
        SingleLiveEvent()
    var editStudentResponse: LiveData<Resource<EditStudentResponse>?>? = null
        get() = _editStudentResponse

    fun editStudent(
        jsonObject: JsonObject,
        token: String
    ) = viewModelScope.launch {
        _editStudentResponse.value = repository.editStudent(jsonObject, token)
    }

    //validation
    fun validation(view: View): Boolean {
        invisibleErrorTexts()
        if (studentFirstName.value.isNullOrBlank()) {
            studentFirstNameError.value = view.context.getString(R.string.please_enter_first_name)
            studentFirstNameErrorVisible.value = true
            return false
        }
        if (studentLastName.value.isNullOrBlank()) {
            studentLastNameError.value = view.context.getString(R.string.please_enter_last_name)
            studentLastNameErrorVisible.value = true
            return false
        }

        return true
    }

    //error texts primary state
    fun invisibleErrorTexts() {
        studentFirstNameErrorVisible.value = false
        studentLastNameErrorVisible.value = false
    }
}