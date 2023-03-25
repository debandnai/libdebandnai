package ie.healthylunch.app.fragment.registration

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.databinding.DataBindingUtil
import androidx.navigation.NavOptions
import androidx.navigation.Navigation
import androidx.navigation.findNavController
import ie.healthylunch.app.R
import ie.healthylunch.app.data.model.loginModel.LoginResponse
import ie.healthylunch.app.data.network.ApiInterface
import ie.healthylunch.app.data.network.Resource
import ie.healthylunch.app.data.repository.DeisStudentUniqueCodeRepository
import ie.healthylunch.app.data.viewModel.DeisStudentUniqueCodeViewModel
import ie.healthylunch.app.databinding.FragmentDeisStudentUniqueCodeBinding
import ie.healthylunch.app.ui.base.BaseFragment
import ie.healthylunch.app.utils.AppController
import ie.healthylunch.app.utils.Constants
import ie.healthylunch.app.utils.Constants.Companion.TOKEN
import ie.healthylunch.app.utils.MethodClass
import ie.healthylunch.app.utils.UserPreferences

class DeisStudentUniqueCodeFragment :
    BaseFragment<DeisStudentUniqueCodeViewModel, DeisStudentUniqueCodeRepository>() {
    lateinit var binding: FragmentDeisStudentUniqueCodeBinding

      
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        setUpViewModel()
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_deis_student_unique_code,
            container,
            false
        )

        binding.context = this
        binding.viewModel = viewModel
        binding.lifecycleOwner = requireActivity()

        viewModel.schoolName = arguments?.getString("schoolName")
        viewModel.schoolId = arguments?.getInt("schoolId")

        init()
        onViewClick()

        return binding.root
    }

    override fun getViewModel() = DeisStudentUniqueCodeViewModel::class.java

    override fun getRepository() = DeisStudentUniqueCodeRepository(
        remoteDataSource.buildApi(
            ApiInterface::class.java
        )
    )

      
    private fun init() {
        getDeisStudentAddResponse()
    }

    private fun onViewClick() {
        binding.studentNextBtn.setOnClickListener { view ->
            if (viewModel.uniqueCodeValidation(view)) {
                viewModel.isSubmitEnabled.value = false
                activity?.let { activity ->
                    MethodClass.showProgressDialog(activity)
                    deisStudentAdd()
                }
            }

        }

        binding.backIv.setOnClickListener { view ->
            viewModel.isSubmitEnabled.value = false
            Constants.HAS_STUDENT_ADDED = false
            Constants.ADD_ANOTHER_STUDENT = false
            val navOptions: NavOptions =
                NavOptions.Builder().setPopUpTo(R.id.addStudentNewStepOneFragment, true).build()

            Navigation.findNavController(view)
                .navigate(
                    R.id.action_deisStudentUniqueCodeFragment_to_addStudentNewStepOneFragment,
                    null, navOptions
                )
            viewModel.isSubmitEnabled.value = true
        }


        binding.staffNextBtn.setOnClickListener { view ->
            viewModel.isSubmitEnabled.value = false
            val bundle = Bundle()
            bundle.putString("schoolName", viewModel.schoolName)
            viewModel.schoolId?.let { bundle.putInt("schoolId", it) }

            view.findNavController().navigate(
                R.id.action_deisStudentUniqueCodeFragment_to_addStudentNewStepTwoFragment,
                bundle
            )
            viewModel.isSubmitEnabled.value = true
        }
    }

    fun deisStudentAdd() {
        activity?.let { activity ->
            MethodClass.showProgressDialog(activity)
            val loginResponse: LoginResponse? =
                UserPreferences.getAsObject<LoginResponse>(
                    context,
                    Constants.USER_DETAILS
                )
            TOKEN = loginResponse?.response?.raws?.data?.token.toString()

            val jsonObject = MethodClass.getCommonJsonObject(activity)
            jsonObject.addProperty("student_code", viewModel.uniqueCode.value)
            jsonObject.addProperty("school_id", viewModel.schoolId)
            viewModel.deisStudentAdd(jsonObject, TOKEN)
        }

    }

      
    private fun getDeisStudentAddResponse() {
        viewModel.deisStudentAddResponse?.observe(viewLifecycleOwner) {
            activity?.let { activity ->
                when (it) {
                    is Resource.Success -> {
                        MethodClass.hideProgressDialog(activity)

                        val action =
                            DeisStudentUniqueCodeFragmentDirections.actionDeisStudentUniqueCodeFragmentToDeisStudentSchoolDetailsFragment(
                                it.value.response.raws.data
                            )
                        binding.studentNextBtn.findNavController().navigate(action)

                        viewModel.isSubmitEnabled.value = true
                        viewModel._deisStudentAddResponse.value = null
                        viewModel.deisStudentAddResponse = viewModel._deisStudentAddResponse

                    }
                    is Resource.Failure -> {
                        if (it.errorBody != null) {
                            it.errorString?.let { _ ->

                                it.errorString.let { it1 ->
                                    if (it.errorCode == 401)
                                        AppController.getInstance()
                                            .refreshTokenResponse(
                                                activity,
                                                this,
                                                Constants.ADD_NEW_STUDENT_DEIS,
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
                        viewModel.isSubmitEnabled.value = true
                        viewModel._deisStudentAddResponse.value = null
                        viewModel.deisStudentAddResponse = viewModel._deisStudentAddResponse
                        MethodClass.hideProgressDialog(activity)
                    }
                    else -> {}
                }

            }
        }
    }
}