package ie.healthylunch.app.fragment.registration

import android.annotation.SuppressLint
import android.app.Activity
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.orhanobut.dialogplus.DialogPlus
import ie.healthylunch.app.R
import ie.healthylunch.app.adapter.CountyListAdapter
import ie.healthylunch.app.adapter.SchoolListAdapter
import ie.healthylunch.app.data.model.countryModel.DataItem
import ie.healthylunch.app.data.model.loginModel.LoginResponse
import ie.healthylunch.app.data.model.parentRegistrationModel.ParentRegistrationResponse
import ie.healthylunch.app.data.network.ApiInterface
import ie.healthylunch.app.data.network.Resource
import ie.healthylunch.app.data.repository.StudentRepository
import ie.healthylunch.app.data.viewModel.AddNewStudentStepOneViewModel
import ie.healthylunch.app.databinding.FragmentAddStudentNewStepOneBinding
import ie.healthylunch.app.ui.base.BaseFragment
import ie.healthylunch.app.utils.*
import ie.healthylunch.app.utils.Constants.Companion.COUNTRY_LIST
import ie.healthylunch.app.utils.Constants.Companion.REFRESH_TOKEN
import ie.healthylunch.app.utils.Constants.Companion.SCHOOL_LIST
import ie.healthylunch.app.utils.Constants.Companion.SELECTED_COUNTY_POSITION
import ie.healthylunch.app.utils.Constants.Companion.SELECTED_SCHOOL_POSITION
import ie.healthylunch.app.utils.Constants.Companion.TOKEN


class AddStudentNewStepOneFragment :
    BaseFragment<AddNewStudentStepOneViewModel, StudentRepository>(), AdapterItemOnclickListener,
    DialogPlusListener {

    private lateinit var binding: FragmentAddStudentNewStepOneBinding
    private lateinit var countyList: List<DataItem>
    private lateinit var schoolList: List<ie.healthylunch.app.data.model.schoolListModel.DataItem>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Log.d("checkAddStudentOne", "onCreate")
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        setUpViewModel()

        Log.d("checkAddStudentOne", "onCreateView")
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_add_student_new_step_one,
            container,
            false
        )
        binding.context = this
        binding.viewModel = viewModel
        binding.lifecycleOwner = requireActivity()

        schoolBottomDialog = null
        countyList= ArrayList()

        viewModel.isSubmitEnabled.value = true

        return binding.root
    }

    companion object {

        @SuppressLint("StaticFieldLeak")
        var countyBottomDialog: DialogPlus? = null

        @SuppressLint("StaticFieldLeak")
        var schoolBottomDialog: DialogPlus? = null

        @SuppressLint("StaticFieldLeak")
        var schoolListAdapter: SchoolListAdapter? = null

        @SuppressLint("StaticFieldLeak")
        var  countyListAdapter :CountyListAdapter ?=null

        var isCountyListEmpty = false
        var isStudentListEmpty = false

        var county = 0
    }

      
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d("checkAddStudentOne", "onViewCreated")
        val parentRegistrationResponse: ParentRegistrationResponse? =
            UserPreferences.getAsObject<ParentRegistrationResponse>(
                requireActivity(),
                Constants.USER_DETAILS
            )
        TOKEN = "Bearer " + parentRegistrationResponse?.response?.raws?.data?.token.toString()


        //call county list api
        getCountyList()

        //county List Response
        viewModel.countyListResponse?.observe(requireActivity()) {

            when (it) {
                is Resource.Success -> {

                    if (it.value.response.raws.data?.isNotEmpty() == true) {
                        countyList = it.value.response.raws.data
                        isCountyListEmpty = false


                        CustomDialog.setBottomDialog(
                            requireActivity(),
                            getString(R.string.country_name),
                            this,
                            "countyList"
                        )
                        //      viewModel.county.value=countyList[SELECTED_COUNTRY_POSITION].countyName
                        //    viewModel.countyId=countyList[SELECTED_COUNTRY_POSITION].id

                    } else {

                        viewModel.schoolName.value = "School"
                        isCountyListEmpty = true

                    }
                    Handler(Looper.getMainLooper()).postDelayed({
                        MethodClass.hideProgressDialog(this)
                    }, 500)
                    viewModel._countyListResponse.value = null
                    viewModel.countyListResponse = viewModel._countyListResponse
                }
                is Resource.Failure -> {
                    MethodClass.hideProgressDialog(this)


                    when (it.errorCode) {
                        401 -> AppController.getInstance()
                            .refreshTokenResponse(
                                requireActivity(),
                                null,
                                COUNTRY_LIST,
                                REFRESH_TOKEN
                            )
                        307, 426 -> it.errorString?.let { it1 ->
                            MethodClass.showErrorDialog(
                                requireActivity(),
                                it1,
                                it.errorCode
                            )
                        }
                        else -> viewModel.schoolName.value = "School"
                    }
                    viewModel._countyListResponse.value = null
                    viewModel.countyListResponse = viewModel._countyListResponse

                }
            }

        }

        //School List Response
        viewModel.schoolListResponse?.observe(requireActivity()) {
            when (it) {
                is Resource.Success -> {

                    if (it.value.response.raws.data!!.isNotEmpty()) {
                        schoolList = it.value.response.raws.data
                        isStudentListEmpty = false


                    } else {
                        isStudentListEmpty = true
                        schoolList = ArrayList()

                    }

                    CustomDialog.setBottomDialog(
                        requireActivity(),
                        getString(R.string.school_name),
                        this,
                        "schoolList"
                    )

                    viewModel.schoolName.value = "School"

                    Handler(Looper.getMainLooper()).postDelayed({
                        MethodClass.hideProgressDialog(this)
                    }, 500)

                    viewModel._schoolListResponse.value = null
                    viewModel.schoolListResponse = viewModel._schoolListResponse
                }
                is Resource.Failure -> {
                    MethodClass.hideProgressDialog(this)
                    when (it.errorCode) {
                        401 -> AppController.getInstance()
                            .refreshTokenResponse(
                                requireActivity(),
                                null,
                                SCHOOL_LIST,
                                REFRESH_TOKEN
                            )
                        307, 426 -> it.errorString?.let { it1 ->
                            MethodClass.showErrorDialog(
                                requireActivity(),
                                it1, it.errorCode
                            )
                        }
                        else -> {
                            viewModel.schoolName.value = "School"
                        }
                    }
                    viewModel._schoolListResponse.value = null
                    viewModel.schoolListResponse = viewModel._schoolListResponse
                }
            }

        }

    }

    override fun getViewModel() = AddNewStudentStepOneViewModel::class.java
    override fun getRepository() =
        StudentRepository(remoteDataSource.buildApi(ApiInterface::class.java))

    @Suppress("UNCHECKED_CAST")
    override fun onAdapterItemClick(arrayList: List<Any?>?, position: Int, tag: String) {
        Log.d("checkAddStudentOne", "onAdapterItemClick")
        if ("countyList" == tag) {
            countyBottomDialog?.dismiss()
            val countryList: List<DataItem> = arrayList as List<DataItem>
            SELECTED_COUNTY_POSITION = position
            viewModel.county.value = countryList[position].countyName
            viewModel.countyId = countryList[position].id
            county = countyList[position].id


            getSchoolList()

        } else if ("schoolList" == tag) {
            schoolBottomDialog?.dismiss()
            val schoolList: List<ie.healthylunch.app.data.model.schoolListModel.DataItem> =
                arrayList as List<ie.healthylunch.app.data.model.schoolListModel.DataItem>
            SELECTED_SCHOOL_POSITION = position
            viewModel.schoolName.value = schoolList[position].schoolName
            viewModel.schoolType.value = schoolList[position].schoolType
            viewModel.schoolId.value = schoolList[position].id
        }

    }

    @SuppressLint("LongLogTag")
    override fun setBottomDialogListener(
        activity: Activity,
        dialogPlus: DialogPlus,
        recyclerView: RecyclerView,
        noDataFoundTv: TextView,
        tag: String
    ) {
        Log.d("checkAddStudentOne", "setBottomDialogListener")
        if ("countyList" == tag) {

            countyBottomDialog = dialogPlus
            if (!isCountyListEmpty) {
                noDataFoundTv.visibility = View.GONE
                recyclerView.visibility = View.VISIBLE

                countyListAdapter =
                    CountyListAdapter(
                        requireActivity(),
                        countyList,
                        this
                    )
                recyclerView.adapter = countyListAdapter
                recyclerView.isFocusable = false
                Log.d("SELECTED_COUNTRY_POSITION", "" + SELECTED_COUNTY_POSITION)
                recyclerView.scrollToPosition(SELECTED_COUNTY_POSITION)

            } else {
                noDataFoundTv.visibility = View.VISIBLE
                recyclerView.visibility = View.GONE
            }


        } else if ("schoolList" == tag) {
            schoolBottomDialog = dialogPlus
            if (!isStudentListEmpty) {
                noDataFoundTv.visibility = View.GONE
                recyclerView.visibility = View.VISIBLE

                schoolListAdapter =
                    SchoolListAdapter(
                        activity,
                        schoolList,
                        this
                    )

                recyclerView.adapter = schoolListAdapter
                recyclerView.isFocusable = false
                recyclerView.scrollToPosition(SELECTED_SCHOOL_POSITION)
                viewModel.schoolName.value =
                    schoolList[SELECTED_SCHOOL_POSITION].schoolName

                viewModel.schoolType.value =
                    schoolList[SELECTED_SCHOOL_POSITION].schoolType
                viewModel.schoolId.value =
                    schoolList[SELECTED_SCHOOL_POSITION].id

            } else {
                noDataFoundTv.visibility = View.VISIBLE
                recyclerView.visibility = View.GONE
            }

        }


    }

    fun getCountyList() {
        MethodClass.showProgressDialog(this)
        SELECTED_COUNTY_POSITION = 0
        val jsonObject = MethodClass.getCommonJsonObject(requireContext())
        viewModel.countryListApiCall(jsonObject, TOKEN)
        Log.d("checkAddStudentOne", "getCountyList")
    }

    fun getSchoolList() {
        MethodClass.showProgressDialog(this)
        val loginResponse: LoginResponse? =
            UserPreferences.getAsObject<LoginResponse>(
                binding.root.context,
                Constants.USER_DETAILS
            )

        loginResponse?.response?.raws?.data?.token?.let { TOKEN = it }
        REFRESH_TOKEN = loginResponse?.response?.raws?.data?.refreshToken.toString()
        SELECTED_SCHOOL_POSITION = 0
        val countyId = county
        if (isAdded) {
            val jsonObject = MethodClass.getCommonJsonObject(requireContext())
            jsonObject.addProperty("county_id", countyId)
            viewModel.schoolList(jsonObject, TOKEN)
            Log.d("checkAddStudentOne", "getSchoolList")
        }

    }

}