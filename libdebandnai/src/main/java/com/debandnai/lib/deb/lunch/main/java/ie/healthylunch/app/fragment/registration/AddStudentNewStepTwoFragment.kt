package ie.healthylunch.app.fragment.registration

import android.annotation.SuppressLint
import android.app.Activity
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.databinding.DataBindingUtil
import androidx.navigation.fragment.NavHostFragment
import androidx.recyclerview.widget.RecyclerView
import com.orhanobut.dialogplus.DialogPlus
import ie.healthylunch.app.R
import ie.healthylunch.app.adapter.SchoolClassListAdapter
import ie.healthylunch.app.data.model.isLoginModel.IsLogin
import ie.healthylunch.app.data.model.loginModel.LoginResponse
import ie.healthylunch.app.data.model.studentClassModel.DataItem
import ie.healthylunch.app.data.network.ApiInterface
import ie.healthylunch.app.data.network.Resource
import ie.healthylunch.app.data.repository.StudentRepository

import ie.healthylunch.app.data.viewModel.AddNewStudentStepTwoViewModel
import ie.healthylunch.app.data.viewModel.DashBoardViewModel
import ie.healthylunch.app.databinding.FragmentAddStudentNewStepTwoBinding
import ie.healthylunch.app.ui.base.BaseFragment
import ie.healthylunch.app.utils.*
import ie.healthylunch.app.utils.Constants.Companion.ADD_NEW_STUDENT
import ie.healthylunch.app.utils.Constants.Companion.CLASS_ID_TAG
import ie.healthylunch.app.utils.Constants.Companion.ERROR_STATUS_401
import ie.healthylunch.app.utils.Constants.Companion.F_NAME_TAG
import ie.healthylunch.app.utils.Constants.Companion.LOGIN_CHECK
import ie.healthylunch.app.utils.Constants.Companion.L_NAME_TAG
import ie.healthylunch.app.utils.Constants.Companion.REFRESH_TOKEN
import ie.healthylunch.app.utils.Constants.Companion.SCHOOL_CLASS_LIST
import ie.healthylunch.app.utils.Constants.Companion.SCHOOL_ID_TAG
import ie.healthylunch.app.utils.Constants.Companion.SCHOOL_ID_TAG_2
import ie.healthylunch.app.utils.Constants.Companion.SCHOOL_NAME_TAG
import ie.healthylunch.app.utils.Constants.Companion.SELECTED_CLASS_POSITION
import ie.healthylunch.app.utils.Constants.Companion.SELECTED_STUDENT_ID
import ie.healthylunch.app.utils.Constants.Companion.SELECTED_STUDENT_POSITION
import ie.healthylunch.app.utils.Constants.Companion.STATUS_ONE
import ie.healthylunch.app.utils.Constants.Companion.STATUS_ZERO
import ie.healthylunch.app.utils.Constants.Companion.STUDENT
import ie.healthylunch.app.utils.Constants.Companion.STUDENT_ID
import ie.healthylunch.app.utils.Constants.Companion.STUDENT_ID_TAG
import ie.healthylunch.app.utils.Constants.Companion.STUDENT_LIST
import ie.healthylunch.app.utils.Constants.Companion.STUDENT_NAME_TAG
import ie.healthylunch.app.utils.Constants.Companion.TEACHER
import ie.healthylunch.app.utils.Constants.Companion.TOKEN
import ie.healthylunch.app.utils.Constants.Companion.USER_DETAILS
import ie.healthylunch.app.utils.Constants.Companion.USER_TYPE
import ie.healthylunch.app.utils.Constants.Companion.USER_TYPE_TAG
import java.util.*


class AddStudentNewStepTwoFragment :
    BaseFragment<AddNewStudentStepTwoViewModel, StudentRepository>(),
    DialogPlusListener, AdapterItemOnclickListener {
    private lateinit var classList: List<DataItem>

    lateinit var fragmentAddStudentNewStepTwoBinding:FragmentAddStudentNewStepTwoBinding

      
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        // Inflate the layout for this fragment
        setUpViewModel()


        fragmentAddStudentNewStepTwoBinding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_add_student_new_step_two,
            container,
            false
        )
        fragmentAddStudentNewStepTwoBinding.context = this
        viewModel.addStudentNewStepTwoFragment = this
        fragmentAddStudentNewStepTwoBinding.viewModel = viewModel
        fragmentAddStudentNewStepTwoBinding.lifecycleOwner = this
        onViewClick()
        //get Add New Student Response
        getAddNewStudentResponse()
        return fragmentAddStudentNewStepTwoBinding.root
    }

    private fun onViewClick() {
        fragmentAddStudentNewStepTwoBinding.continueLayout.setOnClickListener{
            val bundle = Bundle()
            if (studentDetailsValidation()) {
                activity?.let {act->
                    viewModel.isSubmitEnabled.value = false
                    val jsonObject = MethodClass.getCommonJsonObject(act)
                    bundle.putString(SCHOOL_NAME_TAG, viewModel.schoolName.value)
                    viewModel.schoolId.value?.let { bundle.putInt(SCHOOL_ID_TAG, it) }
                    //jsonObject.addProperty("user_type", STUDENT)
                    jsonObject.addProperty(SCHOOL_ID_TAG_2, viewModel.schoolId.value)
                    jsonObject.addProperty(CLASS_ID_TAG, viewModel.classId.value)
                    jsonObject.addProperty(L_NAME_TAG, viewModel.studentSurName.value)
                    jsonObject.addProperty(F_NAME_TAG, viewModel.studentFirstName.value)
                    MethodClass.showProgressDialog(act)
                    viewModel.getAddNewStudent(jsonObject, TOKEN)
                }
            }

        }
    }
      
    fun getStudentList() {
        activity?.let {act->
            val loginResponse: LoginResponse? =
                UserPreferences.getAsObject<LoginResponse>(
                    act,
                    USER_DETAILS
                )

            loginResponse?.response?.raws?.data?.token?.let { token ->
                val jsonObject = MethodClass.getCommonJsonObject(act)
                viewModel.studentList(jsonObject, token)
                getStudentListResponse()
            }

        }
    }

      
    fun getStudentListResponse() {
        activity?.let {act->
            viewModel.studentListResponse?.observe(viewLifecycleOwner) {response->


                when (response) {
                    is Resource.Success -> {
                        var responseData=response.value.response.raws.data
                        val bundle = Bundle()
                        bundle.putString(STUDENT_NAME_TAG, viewModel.studentFirstName.value)
                        bundle.putInt(STUDENT_ID_TAG, STUDENT_ID)
                        bundle.putString(USER_TYPE_TAG, USER_TYPE)

                        var i = STATUS_ZERO
                        if (!responseData.isNullOrEmpty())
                            for (studentIds in responseData) {

                                if (Objects.equals(
                                        STUDENT_ID,
                                        studentIds.studentid
                                    ) && Objects.equals(USER_TYPE, STUDENT)
                                ) {
                                    SELECTED_STUDENT_ID = studentIds.studentid
                                    SELECTED_STUDENT_POSITION = i
                                    DashBoardViewModel.selectedStudentPrePosition = i
                                    break
                                }
                                if (Objects.equals(
                                        STUDENT_ID,
                                        studentIds.studentid
                                    ) && Objects.equals(USER_TYPE, TEACHER)
                                ) {
                                    SELECTED_STUDENT_ID = studentIds.studentid
                                    SELECTED_STUDENT_POSITION = i
                                    DashBoardViewModel.selectedStudentPrePosition = i
                                    break
                                }

                                ++i
                            }
                        val navHostFragment =
                            act.supportFragmentManager.findFragmentById(R.id.fragmentContainerView) as NavHostFragment
                        val navController = navHostFragment.navController

                        navController.navigate(
                            R.id.action_addStudentNewStepTwoFragment_to_checkHasAllergenFragment,
                            bundle
                        )

                        MethodClass.hideProgressDialog(act)
                        viewModel._studentListResponse.value = null
                        viewModel.studentListResponse = viewModel._studentListResponse

                    }
                    is Resource.Failure -> {

                        if (response.errorBody != null) {
                            MethodClass.hideProgressDialog(act)
                            response.errorString?.let { error ->
                                if (response.errorCode == 401)
                                    AppController.getInstance()
                                        .refreshTokenResponse(
                                            act,
                                            this,
                                            STUDENT_LIST,
                                            REFRESH_TOKEN
                                        )
                                else if (response.errorCode == 307 || response.errorCode == 426)
                                    MethodClass.showErrorDialog(act, error, response.errorCode)

                            }


                        }
                        viewModel._studentListResponse.value = null
                        viewModel.studentListResponse = viewModel._studentListResponse
                    }
                    else -> {}
                }

            }
        }


    }
      
    fun getAddNewStudentResponse() {

        activity?.let { act->
            viewModel.addNewStudentResponse?.observe(viewLifecycleOwner) {response->
                when (response) {
                    is Resource.Success -> {
                        val bundle = Bundle()
                        with(response.value.response.raws.data){
                            STUDENT_ID = studentId
                            USER_TYPE = userType
                            bundle.putString(STUDENT_NAME_TAG, viewModel.studentFirstName.value)
                            bundle.putInt(STUDENT_ID_TAG, studentId)
                            bundle.putString(USER_TYPE_TAG, userType)
                        }

                        val isLogin: IsLogin? = UserPreferences.getAsObject<IsLogin>(
                            act,
                            LOGIN_CHECK
                        )
                        if (isLogin?.isLogin == STATUS_ONE)
                            getStudentList()
                        else {
                            val navHostFragment =
                                act.supportFragmentManager.findFragmentById(R.id.fragmentContainerView) as NavHostFragment
                            val navController = navHostFragment.navController

                            navController.navigate(
                                R.id.action_addStudentNewStepTwoFragment_to_checkHasAllergenFragment,
                                bundle
                            )
                            MethodClass.hideProgressDialog(act)
                        }
                        viewModel.isSubmitEnabled.value = true
                        viewModel._addNewStudentResponse.value = null
                        viewModel.addNewStudentResponse = viewModel._addNewStudentResponse

                    }
                    is Resource.Failure -> {

                        if (response.errorBody != null) {
                            MethodClass.hideProgressDialog(act)
                            response.errorString?.let { _ ->

                                response.errorString.let { error ->
                                    if (response.errorCode == ERROR_STATUS_401)
                                        AppController.getInstance()
                                            .refreshTokenResponse(
                                                act,
                                                this,
                                                ADD_NEW_STUDENT,
                                                REFRESH_TOKEN
                                            )
                                    else {
                                        MethodClass.showErrorDialog(
                                            act,
                                            error,
                                            response.errorCode
                                        )
                                    }
                                }


                            }


                        }

                        viewModel.isSubmitEnabled.value = true
                        viewModel._addNewStudentResponse.value = null
                        viewModel.addNewStudentResponse = viewModel._addNewStudentResponse
                    }
                    else -> {}
                }

            }
        }
    }
    private fun studentDetailsValidation(): Boolean {
        viewModel.invisibleErrorTexts()

        if(viewModel.className.value == getString(R.string.which_class2)){
            viewModel.classNameError.value = getString(R.string.please_select_your_class)
            viewModel.classNameErrorVisible.value = true
            return false
        }
//        if (viewModel.className.value.isNullOrBlank()) {
//            viewModel.classNameError.value = getString(R.string.please_select_your_class)
//            viewModel.classNameErrorVisible.value = true
//            return false
//        }
//        if (viewModel.className.value == getString(R.string.which_class)) {
//            viewModel.classNameError.value = getString(R.string.please_select_your_class)
//            viewModel.classNameErrorVisible.value = true
//            return false
//        }
        if (viewModel.studentFirstName.value.isNullOrBlank()) {
            viewModel.studentFirstNameError.value =
                getString(R.string.please_enter_student_first_name)
            viewModel.studentFirstNameErrorVisible.value = true
            return false
        }

        if (viewModel.studentSurName.value.isNullOrBlank()) {
            viewModel.studentSurNameError.value = getString(R.string.please_enter_student_surname)
            viewModel.studentSurNameErrorVisible.value = true
            return false
        }
        return true
    }
    companion object {

        @SuppressLint("StaticFieldLeak")
        var classBottomDialog: DialogPlus? = null
        var isClassListEmpty = false
    }

    override fun getViewModel() = AddNewStudentStepTwoViewModel::class.java
    override fun getRepository() =
        StudentRepository(remoteDataSource.buildApi(ApiInterface::class.java))

      
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.schoolName.value = arguments?.getString("schoolName")
        viewModel.schoolId.value = arguments?.getInt("schoolId")

        //call county list api
        getClassList()
        viewModel.schoolClassListResponse?.observe(requireActivity()) {
            when (it) {
                is Resource.Success -> {
                    SELECTED_CLASS_POSITION = -1
                    if (it.value.response.raws.data!!.isNotEmpty()) {
                        isClassListEmpty = false
                        classList = it.value.response.raws.data

                    } else
                        isClassListEmpty = true

                    CustomDialog.setBottomDialog(
                        requireActivity(),
                        getString(R.string.school_name),
                        this,
                        "countyList"
                    )


                }
                is Resource.Failure -> {
                    if (it.errorCode == 401)
                        AppController.getInstance()
                            .refreshTokenResponse(
                                requireActivity(),
                                null,
                                SCHOOL_CLASS_LIST,
                                REFRESH_TOKEN
                            )
                    else if (it.errorCode == 307 || it.errorCode == 426)
                        it.errorString?.let { it1 ->
                            MethodClass.showErrorDialog(
                                requireActivity(),
                                it1, it.errorCode
                            )
                        }
                }
                else -> {}
            }

        }

    }

    fun getClassList() {
        val loginResponse: LoginResponse? =
            UserPreferences.getAsObject<LoginResponse>(
                requireContext(),
                Constants.USER_DETAILS
            )

        loginResponse?.response?.raws?.data?.token?.let { Constants.TOKEN = it }
        REFRESH_TOKEN = loginResponse?.response?.raws?.data?.refreshToken.toString()
        val jsonObject = MethodClass.getCommonJsonObject(requireContext())
        jsonObject.addProperty("school_id", viewModel.schoolId.value)
        viewModel.studentClassList(jsonObject, Constants.TOKEN)
    }


    override fun setBottomDialogListener(
        activity: Activity,
        dialogPlus: DialogPlus,
        recyclerView: RecyclerView,
        noDataFoundTv: TextView,
        tag: String
    ) {
        classBottomDialog = dialogPlus
        //   if ("countyList" == tag) {
        if (!isClassListEmpty) {

            noDataFoundTv.visibility = View.GONE
            recyclerView.visibility = View.VISIBLE

            val schoolClassListAdapter =
                SchoolClassListAdapter(
                    activity,
                    classList,
                    this
                )
            recyclerView.adapter = schoolClassListAdapter
            recyclerView.isFocusable = false
            recyclerView.scrollToPosition(SELECTED_CLASS_POSITION)
            //viewModel.className.value = classList?.get(Constants.SELECTED_CLASS_POSITION)?.className
            // viewModel.classId.value = classList?.get(Constants.SELECTED_CLASS_POSITION)?.classId
        } else {
            noDataFoundTv.visibility = View.VISIBLE
            recyclerView.visibility = View.GONE
        }


        //   }
    }

    @Suppress("UNCHECKED_CAST")
    override fun onAdapterItemClick(arrayList: List<Any?>?, position: Int, tag: String) {
        val classList: List<DataItem> = arrayList as List<DataItem>
        classBottomDialog!!.dismiss()
        SELECTED_CLASS_POSITION = position
        viewModel.className.value = classList[position].className
        viewModel.classId.value = classList[position].classId
        viewModel.classNameErrorVisible.value = false


    }
}

