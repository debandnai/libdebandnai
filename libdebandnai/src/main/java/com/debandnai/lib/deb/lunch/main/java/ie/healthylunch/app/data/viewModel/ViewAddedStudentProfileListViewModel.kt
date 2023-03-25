package ie.healthylunch.app.data.viewModel

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.TextView
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
import ie.healthylunch.app.data.model.deleteStudentModel.DeleteStudentResponse
import ie.healthylunch.app.data.model.loginModel.LoginResponse
import ie.healthylunch.app.data.model.studentAllergenCount.StudentAllergenCountResponse
import ie.healthylunch.app.data.model.studentListModel.DataItem
import ie.healthylunch.app.data.model.studentListModel.StudenListResponse
import ie.healthylunch.app.data.network.Resource
import ie.healthylunch.app.data.repository.StudentRepository

import ie.healthylunch.app.data.viewModel.DashBoardViewModel.Companion.selectedStudentPrePosition
import ie.healthylunch.app.fragment.students.ViewAddedStudentProfileListFragment
import ie.healthylunch.app.ui.DashBoardActivity
import ie.healthylunch.app.ui.RegistrationActivity
import ie.healthylunch.app.utils.*
import ie.healthylunch.app.utils.Constants.Companion.ADD_ANOTHER_STUDENT
import ie.healthylunch.app.utils.Constants.Companion.HAS_STUDENT_ADDED
import ie.healthylunch.app.utils.Constants.Companion.REFRESH_TOKEN
import ie.healthylunch.app.utils.Constants.Companion.SELECTED_STUDENT_POSITION
import ie.healthylunch.app.utils.Constants.Companion.STUDENT
import ie.healthylunch.app.utils.Constants.Companion.STUDENT_ID
import ie.healthylunch.app.utils.Constants.Companion.USER_TYPE
import ie.healthylunch.app.utils.DialogPlusListener
import ie.healthylunch.app.utils.UserPreferences
import kotlinx.coroutines.launch
import kotlin.collections.ArrayList

class ViewAddedStudentProfileListViewModel(private val repository: StudentRepository) : ViewModel(),
        AdapterItemOnclickListener,
        DialogYesNoListener, DialogPlusListener {
    lateinit var viewAddedStudentProfileListFragment: ViewAddedStudentProfileListFragment

    //var userType: MutableLiveData<String> = MutableLiveData("student")
    var studentName: MutableLiveData<String> = MutableLiveData("")
    var studentFirstName: MutableLiveData<String> = MutableLiveData("")
    private var studentLastName: MutableLiveData<String> = MutableLiveData("")
    private var schoolType: Int? = 0
    var classId: MutableLiveData<Int> = MutableLiveData(-1)
    var studentId: MutableLiveData<Int> = MutableLiveData(-1)

    var className: MutableLiveData<String> = MutableLiveData("")
    var email: MutableLiveData<String> = MutableLiveData("")
    var schoolName: MutableLiveData<String> = MutableLiveData("")

    //private var schoolId: MutableLiveData<String> = MutableLiveData("")
    private var studentNameList = ArrayList<DataItem>()
    private var dialogPlus: DialogPlus? = null
    private var isStudentListEmpty = false

    @SuppressLint("StaticFieldLeak")
    lateinit var v: View

    // Student  list
    private val _studentListResponse: SingleLiveEvent<Resource<StudenListResponse>?> =
            SingleLiveEvent()
    private var studentListResponse: SingleLiveEvent<Resource<StudenListResponse>?>? = null
        get() = _studentListResponse

    fun studentList(
            jsonObject: JsonObject,
            token: String
    ) = viewModelScope.launch {
        _studentListResponse.value = repository.studentListRepository(jsonObject, token)
    }


    // Delete Student
    val _deleteStudentListResponse: SingleLiveEvent<Resource<DeleteStudentResponse>?> =
            SingleLiveEvent()
    var deleteStudentListResponse: LiveData<Resource<DeleteStudentResponse>?>? = null
        get() = _deleteStudentListResponse

    private fun deleteStudent(
            jsonObject: JsonObject,
            token: String
    ) = viewModelScope.launch {
        _deleteStudentListResponse.value = repository.deleteStudentApi(jsonObject, token)
    }

    // Delete Student
    private val _studentAllergenCountLiveData: SingleLiveEvent<Resource<StudentAllergenCountResponse>?> =
        SingleLiveEvent()
    val studentAllergenCountLiveData: LiveData<Resource<StudentAllergenCountResponse>?>?
        get() = _studentAllergenCountLiveData

    fun getStudentAllergenCount(
        jsonObject: JsonObject,
        token: String
    ) = viewModelScope.launch {
        _studentAllergenCountLiveData.value = repository.studentAllergenCountApi(jsonObject, token)
    }


      
    fun getStudentListResponse(activity: Activity) {
        activity as DashBoardActivity
        studentListResponse?.observe(activity) {
            when (it) {
                is Resource.Success -> {
                    studentNameList = ArrayList()
                    it.value.response.raws.data?.let { it1 ->
                        studentNameList =
                                if (it1.isNotEmpty()) it1 as ArrayList<DataItem> else ArrayList()
                    }

                    if (studentNameList.isNotEmpty()) {
                        studentName.value =
                                studentNameList[SELECTED_STUDENT_POSITION].fName + " " + studentNameList[SELECTED_STUDENT_POSITION].lName
                        studentFirstName.value = studentNameList[SELECTED_STUDENT_POSITION].fName
                        studentLastName.value = studentNameList[SELECTED_STUDENT_POSITION].lName
                        studentId.value = studentNameList[SELECTED_STUDENT_POSITION].studentid
                        className.value = studentNameList[SELECTED_STUDENT_POSITION].student_class
                        schoolName.value = studentNameList[SELECTED_STUDENT_POSITION].school
                        studentId.value = studentNameList[SELECTED_STUDENT_POSITION].studentid
                        STUDENT_ID = studentNameList[SELECTED_STUDENT_POSITION].studentid
                        USER_TYPE = studentNameList[SELECTED_STUDENT_POSITION].user_type
                        schoolType = studentNameList[SELECTED_STUDENT_POSITION].schoolType

                        //set isSelected = true as pre selected value
                        studentNameList[SELECTED_STUDENT_POSITION].isSelected = true

                        CustomDialog.dialogPlus(
                                activity,
                                activity.getString(R.string.student_name),
                                this,
                                "studentList"
                        )

                        Handler(Looper.getMainLooper()).postDelayed({
                            MethodClass.hideProgressDialog(activity)
                        }, 500)


                    } else {
                        MethodClass.hideProgressDialog(activity)
                        isStudentListEmpty = true
                        AppController.getInstance().isDeActivatedAllStudents = true
                        AppController.getInstance().logoutAppController(activity, true)
                    }


                    _studentListResponse.value = null
                    studentListResponse = _studentListResponse
                }
                is Resource.Failure -> {
                    if (it.errorBody != null) {
                        MethodClass.hideProgressDialog(activity)
                        it.errorString?.let { _ ->
                            it.errorString.let { it1 ->
                                if (it.errorCode == 401)
                                    AppController.getInstance()
                                            .refreshTokenResponse(
                                                    activity,
                                                    viewAddedStudentProfileListFragment,
                                                    Constants.STUDENT_LIST,
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
                    _studentListResponse.value = null
                    studentListResponse = _studentListResponse
                    MethodClass.hideProgressDialog(activity)
                }

                else -> {}
            }


        }

    }


    fun editStudent(v: View, activity: Activity?) {
        activity?.let {
            if (studentId.value != -1) {

                val bundle = Bundle()
                bundle.putString("studentFirstName", studentFirstName.value)
                bundle.putString("studentLastName", studentLastName.value)
                bundle.putString("schoolName", schoolName.value)
                schoolType?.let { it1 -> bundle.putInt("schoolType", it1) }
                bundle.putString("userType", USER_TYPE)
                classId.value?.let { bundle.putInt("classId", it) }
                studentId.value?.let { bundle.putInt("studentId", it) }

                val navOptions: NavOptions =
                        NavOptions.Builder()
                                .setPopUpTo(R.id.viewaddedstudentprofilelistFragment, true)
                                .build()
                Navigation.findNavController(v)
                        .navigate(
                                R.id.action_viewaddedstudentprofilelistFragment_to_editStudentprofilefragment,
                                bundle, navOptions
                        )

            }
        }

    }

    fun viewAllergen(v: View) {
        val bundle = Bundle()
        if (studentId.value != -1) {
            bundle.putString("studentName", studentFirstName.value)
            bundle.putString("userType", USER_TYPE)
            studentId.value?.let { bundle.putInt("studentId", it) }
            val navOptions: NavOptions =
                    NavOptions.Builder().setPopUpTo(R.id.editStudentAllergenFragment, true)
                            .build()

            Navigation.findNavController(v)
                    .navigate(
                            R.id.action_viewaddedstudentprofilelistFragment_to_viewallergenfragment,
                            bundle, navOptions
                    )
        }

    }

    fun deActivateStudent(activity: Activity?, view: View) {
        v = view
        activity?.let {
            if (studentId.value != -1) {
                if (schoolType == 2 && USER_TYPE.equals(STUDENT, true)) {
                    CustomDialog.showOkTypeDialog(
                            activity,
                            activity.getString(R.string.unable_to_de_active_student_details_msg),
                            object : DialogOkListener {
                                override fun okOnClick(dialog: Dialog, activity: Activity) {
                                    dialog.dismiss()
                                }

                            })
                } else {
                    CustomDialog.clearFoodItemPopup(
                            activity,
                            activity.resources.getString(R.string.Are_you_sure_that_you_want_to_delete_this_student_),
                            activity.resources.getString(R.string.confirm),
                            activity.resources.getString(R.string.cancel),
                            this@ViewAddedStudentProfileListViewModel
                    )

                }
            }
        }


    }

    fun newStudent(activity: Activity) {
        HAS_STUDENT_ADDED = true
        ADD_ANOTHER_STUDENT = true
        val loginResponse: ie.healthylunch.app.data.model.loginModel.LoginResponse? =
                UserPreferences.getAsObject<ie.healthylunch.app.data.model.loginModel.LoginResponse>(
                        activity,
                        Constants.USER_DETAILS
                )
        loginResponse?.response?.raws?.data?.username?.let {
            email.value = it
        }
        activity.startActivity(
                Intent(activity, RegistrationActivity::class.java)
                        .putExtra("for", "add_student")
                        .putExtra("email", email.value)
        )
    }



    fun showStudentBottomDialog() {
        dialogPlus?.show()
    }

      
    override fun yesOnClick(dialog: Dialog, activity: Activity) {
        activity as DashBoardActivity
        dialog.dismiss()

        getDeleteStudent(activity)
    }

    override fun noOnClick(dialog: Dialog, activity: Activity) {
        dialog.dismiss()
    }

      
    fun getDeleteStudent(activity: DashBoardActivity) {
        val loginResponse: LoginResponse? =
                UserPreferences.getAsObject<LoginResponse>(
                        activity,
                        Constants.USER_DETAILS
                )

        loginResponse?.response?.raws?.data?.token?.let { Constants.TOKEN = it }
        REFRESH_TOKEN = loginResponse?.response?.raws?.data?.refreshToken.toString()
        MethodClass.showProgressDialog(activity)
        val jsonObject = MethodClass.getCommonJsonObject(activity)
        jsonObject.addProperty("student_id", studentId.value)
        jsonObject.addProperty("user_type", USER_TYPE)
        deleteStudent(jsonObject, Constants.TOKEN)
    }



    @SuppressLint("LongLogTag")
    override fun setBottomDialogListener(
            activity: Activity,
            dialogPlus: DialogPlus,
            recyclerView: RecyclerView,
            noDataFoundTv: TextView,
            tag: String
    ) {

        if (!isStudentListEmpty) {
            noDataFoundTv.visibility = View.GONE
            recyclerView.visibility = View.VISIBLE
            val studentNameListAdapter =
                    ie.healthylunch.app.adapter.StudentNameListAdapter2(
                            activity,
                            studentNameList,
                            this
                    )

            recyclerView.adapter = studentNameListAdapter
            recyclerView.isFocusable = false
            recyclerView.scrollToPosition(SELECTED_STUDENT_POSITION)
        } else {

            noDataFoundTv.visibility = View.VISIBLE
            recyclerView.visibility = View.GONE

        }

        this.dialogPlus = dialogPlus

    }

    @Suppress("UNCHECKED_CAST")
    override fun onAdapterItemClick(arrayList: List<Any?>?, position: Int, tag: String) {

        SELECTED_STUDENT_POSITION = position
        selectedStudentPrePosition =
                position// this is because in dashboard, selectedStudentPrePosition will be same as SELECTED_STUDENT_POSITION

        val studentList: List<DataItem> =
                arrayList as List<DataItem>
        studentName.value = studentList[position].fName + " " + studentList[position].lName
        studentFirstName.value = studentList[position].fName
        studentLastName.value = studentList[position].lName
        studentId.value = studentList[position].studentid
        className.value = studentList[position].student_class
        schoolName.value = studentList[position].school
        studentId.value = studentNameList[position].studentid
        USER_TYPE = studentNameList[position].user_type

        STUDENT_ID = studentNameList[position].studentid
        schoolType = studentNameList[position].schoolType
        dialogPlus?.dismiss()


    }

}
