package ie.healthylunch.app.data.viewModel

import android.app.Activity
import android.os.Build
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavOptions
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.JsonObject
import com.orhanobut.dialogplus.DialogPlus
import ie.healthylunch.app.R
import ie.healthylunch.app.adapter.SchoolClassListAdapter
import ie.healthylunch.app.data.model.editStudentModel.EditStudentResponse
import ie.healthylunch.app.data.model.loginModel.LoginResponse
import ie.healthylunch.app.data.model.studentClassModel.StudentClassResponse
import ie.healthylunch.app.data.model.studentDetailsModel.StudentDetailsResponse
import ie.healthylunch.app.data.network.Resource
import ie.healthylunch.app.data.repository.StudentRepository
import ie.healthylunch.app.fragment.students.EditStudentProfileFragment
import ie.healthylunch.app.fragment.students.EditStudentProfileFragment.Companion.schoolClassBottomDialog
import ie.healthylunch.app.fragment.students.EditStudentProfileFragment.Companion.schoolClassListAdapter
import ie.healthylunch.app.ui.DashBoardActivity
import ie.healthylunch.app.utils.*
import ie.healthylunch.app.utils.Constants.Companion.EDIT_STUDENT
import ie.healthylunch.app.utils.Constants.Companion.REFRESH_TOKEN
import ie.healthylunch.app.utils.Constants.Companion.SCHOOL_CLASS_LIST
import ie.healthylunch.app.utils.Constants.Companion.SELECTED_CLASS_POSITION
import ie.healthylunch.app.utils.Constants.Companion.STUDENT
import ie.healthylunch.app.utils.Constants.Companion.STUDENT_DETAILS
import ie.healthylunch.app.utils.DialogPlusListener
import kotlinx.coroutines.launch

class EditStudentProfileViewModel(private val repository: StudentRepository) : ViewModel(),
    AdapterItemOnclickListener,
    DialogPlusListener {
    private lateinit var studentClassList: List<ie.healthylunch.app.data.model.studentClassModel.DataItem>
    lateinit var editStudentProfileFragment: EditStudentProfileFragment
    var studentSchoolName: MutableLiveData<String> = MutableLiveData()
    private var schoolId: MutableLiveData<Int> = MutableLiveData()
    var className: MutableLiveData<String> = MutableLiveData()
    var studentFirstName: MutableLiveData<String> = MutableLiveData()
    var studentLastName: MutableLiveData<String> = MutableLiveData()
    var studentId: MutableLiveData<Int> = MutableLiveData()
    var classId: MutableLiveData<Int> = MutableLiveData()
    var county: MutableLiveData<String> = MutableLiveData("County")
    private var countryId: MutableLiveData<Int> = MutableLiveData(-1)
    var studentFirstNameError: MutableLiveData<String> = MutableLiveData()
    var studentLastNameError: MutableLiveData<String> = MutableLiveData()
    var userType: MutableLiveData<String> = MutableLiveData(STUDENT)
    var studentFirstNameErrorVisible: MutableLiveData<Boolean> = MutableLiveData(false)
    var studentLastNameErrorVisible: MutableLiveData<Boolean> = MutableLiveData(false)
    var schoolType: Int? = 0

    var isSaveEnabled: MutableLiveData<Boolean> = MutableLiveData(true)
    private var isStudentClassListEmpty = false

    // Student  Details
    private val _studentDetailsResponse: SingleLiveEvent<Resource<StudentDetailsResponse>?> =
        SingleLiveEvent()
    private var studentDetailsResponse: LiveData<Resource<StudentDetailsResponse>?>? = null
        get() = _studentDetailsResponse

    private fun studentDetails(
        jsonObject: JsonObject,
        token: String
    ) = viewModelScope.launch {
        _studentDetailsResponse.value = repository.studentDetails(jsonObject, token)
    }

    //school class List
    private val _schoolClassListResponse: SingleLiveEvent<Resource<StudentClassResponse>?> =
        SingleLiveEvent()
    private var schoolClassListResponse: LiveData<Resource<StudentClassResponse>?>? = null
        get() = _schoolClassListResponse

    private fun studentClassList(
        jsonObject: JsonObject,
        token: String
    ) = viewModelScope.launch {
        _schoolClassListResponse.value = repository.studentClassList(jsonObject, token)
    }

    //Edit Student
    private val _editStudentResponse: SingleLiveEvent<Resource<EditStudentResponse>?> =
        SingleLiveEvent()
    private var editStudentResponse: LiveData<Resource<EditStudentResponse>?>? = null
        get() = _editStudentResponse

    private fun editStudent(
        jsonObject: JsonObject,
        token: String
    ) = viewModelScope.launch {
        _editStudentResponse.value = repository.editStudent(jsonObject, token)
    }


    //validation
    private fun validation(activity: Activity): Boolean {
        invisibleErrorTexts()
        if (studentFirstName.value.isNullOrBlank()) {
            studentFirstNameError.value = activity.getString(R.string.please_enter_first_name)
            studentFirstNameErrorVisible.value = true
            return false
        }
        if (studentLastName.value.isNullOrBlank()) {
            studentLastNameError.value = activity.getString(R.string.please_enter_last_name)
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

    fun back(v: View) {

        val navOptions: NavOptions =
            NavOptions.Builder().setPopUpTo(R.id.editStudentprofilefragment, true).build()

        Navigation.findNavController(v)
            .navigate(
                R.id.action_editStudentprofilefragment_to_viewaddedstudentprofilelistFragment,
                null,
                navOptions
            )
    }



    fun studentDetailsApiCall(activity: Activity) {
        activity as DashBoardActivity
        MethodClass.showProgressDialog(activity)
        val loginResponse: LoginResponse? =
            UserPreferences.getAsObject<LoginResponse>(
                activity,
                Constants.USER_DETAILS
            )

        loginResponse?.response?.raws?.data?.token?.let { Constants.TOKEN = it }
        REFRESH_TOKEN = loginResponse?.response?.raws?.data?.refreshToken.toString()
        val jsonObject = MethodClass.getCommonJsonObject(activity)
        jsonObject.addProperty("student_id", studentId.value)
        jsonObject.addProperty("user_type", userType.value)
        studentDetails(jsonObject, Constants.TOKEN)
    }

      
    fun getStudentDetailsResponse(activity: Activity) {
        activity as DashBoardActivity
        studentDetailsResponse?.observe(activity) {

            when (it) {
                is Resource.Success -> {

                    MethodClass.hideProgressDialog(activity)

                    classId.value = it.value.response.raws.data.classId
                    schoolId.value = it.value.response.raws.data.schoolId
                    className.value = it.value.response.raws.data.className

                    studentClassListApiCall(activity)
                }
                is Resource.Failure -> {
                    if (it.errorBody != null) {
                        it.errorString?.let { _ ->

                            it.errorString.let { it1 ->
                                if (it.errorCode == 401)
                                    AppController.getInstance()
                                        .refreshTokenResponse(
                                            activity,
                                            editStudentProfileFragment,
                                            STUDENT_DETAILS,
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


                    }

                    _studentDetailsResponse.value = null
                    studentDetailsResponse = _studentDetailsResponse
                    MethodClass.hideProgressDialog(activity)
                }

                else -> {}
            }


        }

    }


    fun studentClassListApiCall(activity: DashBoardActivity) {
        MethodClass.showProgressDialog(activity)
        val loginResponse: LoginResponse? =
            UserPreferences.getAsObject<LoginResponse>(
                activity,
                Constants.USER_DETAILS
            )

        loginResponse?.response?.raws?.data?.token?.let { Constants.TOKEN = it }
        REFRESH_TOKEN = loginResponse?.response?.raws?.data?.refreshToken.toString()
        val jsonObject = MethodClass.getCommonJsonObject(activity)
        jsonObject.addProperty("school_id", schoolId.value)
        studentClassList(jsonObject, Constants.TOKEN)

    }

      
    fun getStudentClassListResponse(activity: Activity) {
        activity as DashBoardActivity
        schoolClassListResponse?.observe(activity) {
            when (it) {
                is Resource.Success -> {
                    MethodClass.hideProgressDialog(activity)

                    if (it.value.response.raws.data?.isNotEmpty() == true) {
                        isStudentClassListEmpty = false
                        studentClassList = it.value.response.raws.data
                        for (i in studentClassList.indices) {
                            if (studentClassList[i].classId == classId.value)
                                SELECTED_CLASS_POSITION = i
                        }

                    } else
                        isStudentClassListEmpty = true

                    CustomDialog.dialogPlus(activity, "Class Name", this, "class")


                }
                is Resource.Failure -> {
                    MethodClass.hideProgressDialog(activity)
                    if (it.errorCode == 401)
                        AppController.getInstance()
                            .refreshTokenResponse(
                                activity,
                                editStudentProfileFragment,
                                SCHOOL_CLASS_LIST,
                                REFRESH_TOKEN
                            )
                    else if (it.errorCode == 307 || it.errorCode == 426)
                        it.errorString?.let { it1 ->
                            MethodClass.showErrorDialog(
                                activity,
                                it1, it.errorCode
                            )
                        }
                    else
                        isStudentClassListEmpty = true
                }
                else -> {}
            }

        }
    }


      
    fun saveClick(activity: Activity) {
        if (validation(activity)) {
            //enabled false of save button for avoiding multiple click
            isSaveEnabled.value = false
            saveStudentDetailsApiCall(activity)
        }
    }

    fun saveStudentDetailsApiCall(activity: Activity) {
        MethodClass.showProgressDialog(activity)
        val jsonObject = MethodClass.getCommonJsonObject(activity)
        jsonObject.addProperty("l_name", studentLastName.value)
        jsonObject.addProperty("class_id", classId.value)
        jsonObject.addProperty("user_type", userType.value)
        jsonObject.addProperty("f_name", studentFirstName.value)
        jsonObject.addProperty("student_id", studentId.value)
        editStudent(jsonObject, Constants.TOKEN)
    }

      
    fun getSaveStudentDetailsResponse(activity: Activity, view: View) {
        activity as DashBoardActivity
        editStudentResponse?.observe(activity) {
            when (it) {
                is Resource.Success -> {
                    MethodClass.hideProgressDialog(activity)
                    Toast.makeText(
                        activity,
                        it.value.response.raws.successMessage,
                        Toast.LENGTH_LONG
                    ).show()
                    Navigation.findNavController(view)
                        .navigate(
                            R.id.action_editStudentprofilefragment_to_viewaddedstudentprofilelistFragment
                        )
                    isSaveEnabled.value = true
                }
                is Resource.Failure -> {
                    MethodClass.hideProgressDialog(activity)
                    if (it.errorBody != null) {
                        it.errorString?.let { _ ->
                            it.errorString.let { it1 ->
                                if (it.errorCode == 401)
                                    AppController.getInstance()
                                        .refreshTokenResponse(
                                            activity,
                                            editStudentProfileFragment,
                                            EDIT_STUDENT,
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


                    }
                    isSaveEnabled.value = true
                }
                else -> {}
            }

        }
    }

    fun studentClass() {

        if (schoolType == 1 || (schoolType == 2 && userType.value.equals(Constants.TEACHER, true)))
            schoolClassBottomDialog?.show()
    }


    override fun setBottomDialogListener(
        activity: Activity,
        dialogPlus: DialogPlus,
        recyclerView: RecyclerView,
        noDataFoundTv: TextView,
        tag: String
    ) {
        activity as DashBoardActivity
        schoolClassBottomDialog = dialogPlus
        if (!isStudentClassListEmpty) {
            noDataFoundTv.visibility = View.GONE
            recyclerView.visibility = View.VISIBLE
            schoolClassListAdapter =
                SchoolClassListAdapter(
                    activity,
                    studentClassList,
                    this
                )
            recyclerView.adapter = schoolClassListAdapter
            recyclerView.isFocusable = false
        } else {

            noDataFoundTv.visibility = View.VISIBLE
            recyclerView.visibility = View.GONE

        }

        recyclerView.scrollToPosition(SELECTED_CLASS_POSITION)


    }

    @Suppress("UNCHECKED_CAST")
    override fun onAdapterItemClick(arrayList: List<Any?>?, position: Int, tag: String) {
        val classList: List<ie.healthylunch.app.data.model.studentClassModel.DataItem> =
            arrayList as List<ie.healthylunch.app.data.model.studentClassModel.DataItem>
        classId.value = classList[position].classId
        className.value = classList[position].className
        SELECTED_CLASS_POSITION = position
        schoolClassListAdapter?.notifyItemRangeChanged(0, classList.size)
        schoolClassBottomDialog!!.dismiss()

    }


}