package ie.healthylunch.app.fragment.registration

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.databinding.DataBindingUtil
import androidx.navigation.findNavController
import androidx.navigation.fragment.navArgs
import ie.healthylunch.app.R
import ie.healthylunch.app.data.model.loginModel.LoginResponse
import ie.healthylunch.app.data.network.ApiInterface
import ie.healthylunch.app.data.network.Resource
import ie.healthylunch.app.data.repository.DeisStudentSchoolDetailsRepository
import ie.healthylunch.app.data.viewModel.DeisStudentSchoolDetailsViewModel
import ie.healthylunch.app.databinding.FragmentDeisStudentSchoolDetailsBinding
import ie.healthylunch.app.ui.base.BaseFragment
import ie.healthylunch.app.utils.AppController
import ie.healthylunch.app.utils.Constants
import ie.healthylunch.app.utils.Constants.Companion.TOKEN
import ie.healthylunch.app.utils.MethodClass
import ie.healthylunch.app.utils.UserPreferences

class DeisStudentSchoolDetailsFragment :
    BaseFragment<DeisStudentSchoolDetailsViewModel, DeisStudentSchoolDetailsRepository>() {
    private val args: DeisStudentSchoolDetailsFragmentArgs by navArgs()

    lateinit var binding: FragmentDeisStudentSchoolDetailsBinding

      
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        setUpViewModel()
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_deis_student_school_details,
            container,
            false
        )

        binding.context = this
        binding.viewModel = viewModel
        binding.lifecycleOwner = requireActivity()

        init()
        onViewClick()
        return binding.root
    }

    override fun getViewModel() = DeisStudentSchoolDetailsViewModel::class.java

    override fun getRepository() = DeisStudentSchoolDetailsRepository(
        remoteDataSource.buildApi(
            ApiInterface::class.java
        )
    )

      
    private fun init() {
        val studentDetails = args.studentDetails
        viewModel.schoolName.value = studentDetails.schoolName
        viewModel.schoolAddress.value = studentDetails.schoolAddress
        viewModel.className.value = studentDetails.className
        viewModel.studentFirstName.value = studentDetails.fName
        viewModel.studentLastName.value = studentDetails.lName
        viewModel.userType.value = studentDetails.userType
        viewModel.studentId = studentDetails.studentId
        viewModel.classId = studentDetails.classId

        //get SaveStudent Details Response
        getSaveStudentDetailsResponse(binding.continueButton)
    }

    private fun onViewClick() {
        binding.continueButton.setOnClickListener { view ->
            if (viewModel.validation(view)) {
                //enabled false of save button for avoiding multiple click
                viewModel.isContinueEnabled.value = false
                saveStudentDetailsApiCall()
            }
        }
    }

    fun saveStudentDetailsApiCall() {
        activity?.let { activity ->
            MethodClass.showProgressDialog(activity)

            val loginResponse: LoginResponse? =
                UserPreferences.getAsObject<LoginResponse>(
                    activity,
                    Constants.USER_DETAILS
                )

            loginResponse?.response?.raws?.data?.token?.let { TOKEN = it }

            val jsonObject = MethodClass.getCommonJsonObject(activity)
            jsonObject.addProperty("l_name", viewModel.studentLastName.value)
            jsonObject.addProperty("class_id", viewModel.classId)
            jsonObject.addProperty("user_type", viewModel.userType.value)
            jsonObject.addProperty("f_name", viewModel.studentFirstName.value)
            jsonObject.addProperty("student_id", viewModel.studentId)
            viewModel.editStudent(jsonObject, TOKEN)
        }

    }

      
    fun getSaveStudentDetailsResponse(view: View) {
        activity?.let { activity ->
            viewModel.editStudentResponse?.observe(activity) {
                when (it) {
                    is Resource.Success -> {
                        MethodClass.hideProgressDialog(activity)

                        val bundle = Bundle()
                        bundle.putString("studentName", viewModel.studentFirstName.value)
                        viewModel.studentId?.let { studentId ->
                            bundle.putInt(
                                "studentId",
                                studentId
                            )
                        }
                        bundle.putString("userType", viewModel.userType.value)

                        view.findNavController().navigate(
                            R.id.action_deisStudentSchoolDetailsFragment_to_checkHasAllergenFragment,
                            bundle
                        )
                        viewModel.isContinueEnabled.value = true
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
                                                this,
                                                Constants.EDIT_STUDENT,
                                                Constants.REFRESH_TOKEN
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
                        viewModel.isContinueEnabled.value = true
                    }
                    else -> {}
                }

            }

        }
    }

}