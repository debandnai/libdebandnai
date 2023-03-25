package ie.healthylunch.app.data.viewModel

import android.os.Bundle
import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.Navigation
import com.google.gson.JsonObject
import ie.healthylunch.app.data.model.addNewStudent.AddNewStudent
import ie.healthylunch.app.data.model.studentClassModel.StudentClassResponse
import ie.healthylunch.app.data.model.studentListModel.StudenListResponse
import ie.healthylunch.app.data.network.Resource
import ie.healthylunch.app.data.repository.StudentRepository
import ie.healthylunch.app.fragment.registration.AddStudentNewStepTwoFragment
import ie.healthylunch.app.fragment.registration.AddStudentNewStepTwoFragment.Companion.classBottomDialog
import ie.healthylunch.app.utils.*
import kotlinx.coroutines.launch

class AddNewStudentStepTwoViewModel(private val repository: StudentRepository) : ViewModel() {
    var schoolName: MutableLiveData<String> = MutableLiveData("")
    var schoolId: MutableLiveData<Int> = MutableLiveData(0)
    var className = MutableLiveData("Which class")
    var classId: MutableLiveData<Int> = MutableLiveData()
    var studentFirstName: MutableLiveData<String> = MutableLiveData()
    var studentSurName: MutableLiveData<String> = MutableLiveData()
    lateinit var addStudentNewStepTwoFragment: AddStudentNewStepTwoFragment
    var studentFirstNameError: MutableLiveData<String> = MutableLiveData()
    var studentSurNameError: MutableLiveData<String> = MutableLiveData()
    var classNameError: MutableLiveData<String> = MutableLiveData()
    var studentFirstNameErrorVisible: MutableLiveData<Boolean> = MutableLiveData(false)
    var studentSurNameErrorVisible: MutableLiveData<Boolean> = MutableLiveData(false)
    var classNameErrorVisible = MutableLiveData(false)
    var isSubmitEnabled: MutableLiveData<Boolean> = MutableLiveData(true)

    val bundle = Bundle()

    // Student  list
    val _studentListResponse: SingleLiveEvent<Resource<StudenListResponse>?> =
        SingleLiveEvent()
    var studentListResponse: LiveData<Resource<StudenListResponse>?>? = null
        get() = _studentListResponse

    fun studentList(
        jsonObject: JsonObject,
        token: String
    ) = viewModelScope.launch {
        _studentListResponse.value = repository.studentList(jsonObject, token)
    }

    //StudentClassList
    private val _schoolClassListResponse: SingleLiveEvent<Resource<StudentClassResponse>?> =
        SingleLiveEvent()
    var schoolClassListResponse: SingleLiveEvent<Resource<StudentClassResponse>?>? = null
        get() = _schoolClassListResponse

    fun studentClassList(
        jsonObject: JsonObject,
        token: String
    ) = viewModelScope.launch {
        _schoolClassListResponse.value = repository.studentClassList(jsonObject, token)
    }

    //AddNewStudent
    val _addNewStudentResponse: SingleLiveEvent<Resource<AddNewStudent>?> =
        SingleLiveEvent()
    var addNewStudentResponse: SingleLiveEvent<Resource<AddNewStudent>?>? = null
        get() = _addNewStudentResponse

    fun getAddNewStudent(
        jsonObject: JsonObject,
        token: String
    ) = viewModelScope.launch {
        _addNewStudentResponse.value = repository.addNewStudentApi(jsonObject, token)
    }


    fun className() {

        classBottomDialog?.show()

    }

    fun back(view: View) {
        Navigation.findNavController(view).popBackStack()
    }


    fun invisibleErrorTexts() {
        studentFirstNameErrorVisible.value = false
        studentSurNameErrorVisible.value = false
        classNameErrorVisible.value = false
    }


}