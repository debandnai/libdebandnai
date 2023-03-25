package ie.healthylunch.app.fragment.students


import android.app.Activity
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import ie.healthylunch.app.R
import ie.healthylunch.app.data.model.loginModel.LoginResponse
import ie.healthylunch.app.data.network.ApiInterface
import ie.healthylunch.app.data.network.Resource
import ie.healthylunch.app.data.repository.StudentRepository
import ie.healthylunch.app.data.viewModel.ViewAddedStudentProfileListViewModel
import ie.healthylunch.app.databinding.FragmentViewAddedStudentProfileListBinding
import ie.healthylunch.app.ui.DashBoardActivity
import ie.healthylunch.app.ui.RegistrationActivity
import ie.healthylunch.app.ui.base.BaseFragment
import ie.healthylunch.app.utils.AppController
import ie.healthylunch.app.utils.Constants
import ie.healthylunch.app.utils.Constants.Companion.EDIT_ALLERGEN
import ie.healthylunch.app.utils.Constants.Companion.FOR
import ie.healthylunch.app.utils.Constants.Companion.HAS_ALLERGEN__POPUP_SHOW_TAG
import ie.healthylunch.app.utils.Constants.Companion.SELECTED_STUDENT_POSITION
import ie.healthylunch.app.utils.Constants.Companion.STATUS_ZERO
import ie.healthylunch.app.utils.Constants.Companion.STUDENT_ID_TAG
import ie.healthylunch.app.utils.Constants.Companion.STUDENT_ID_TAG_2
import ie.healthylunch.app.utils.Constants.Companion.STUDENT_NAME_TAG
import ie.healthylunch.app.utils.Constants.Companion.USER_TYPE
import ie.healthylunch.app.utils.Constants.Companion.USER_TYPE_TAG
import ie.healthylunch.app.utils.Constants.Companion.USER_TYPE_TAG_2
import ie.healthylunch.app.utils.Constants.Companion.YES
import ie.healthylunch.app.utils.MethodClass
import ie.healthylunch.app.utils.UserPreferences


class ViewAddedStudentProfileListFragment :
    BaseFragment<ViewAddedStudentProfileListViewModel, StudentRepository>() {
    lateinit var fragmentViewAddedStudentProfileListBinding:FragmentViewAddedStudentProfileListBinding


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        setUpViewModel()
        fragmentViewAddedStudentProfileListBinding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_view_added_student_profile_list,
            container,
            false
        )
        requireActivity().window.statusBarColor = ContextCompat.getColor(requireContext(), R.color.sky_bg_2)
        fragmentViewAddedStudentProfileListBinding.context = this
        fragmentViewAddedStudentProfileListBinding.viewModel = viewModel
        viewModel.viewAddedStudentProfileListFragment = this
        fragmentViewAddedStudentProfileListBinding.lifecycleOwner = this

        onViewClick()
        //get Student List Response
        viewModel.getStudentListResponse(requireActivity())

        //get Delete Student Response
        getDeleteStudentResponse()
        studentAllergenCountResponse()
        studentListApiCall()
        return fragmentViewAddedStudentProfileListBinding.root
    }

    private fun onViewClick() {
        fragmentViewAddedStudentProfileListBinding.rlViewAllergen.setOnClickListener{view->
//            view.context?.startActivity(
//                Intent(activity, RegistrationActivity::class.java)
//                    .putExtra("for", EDIT_ALLERGEN)
//                    .putExtra("studentId", viewModel.studentId.value)
//                    .putExtra("userType", USER_TYPE)
//                    .putExtra("studentName", viewModel.studentFirstName.value)
//            )

            studentAllergenCountCall()
        }
    }

    fun studentAllergenCountCall(){

        fragmentViewAddedStudentProfileListBinding.root.context.let { ctx ->
            MethodClass.showProgressDialog(ctx as Activity)

            getToken()?.let { token ->
                val jsonObject = MethodClass.getCommonJsonObject(ctx)
                jsonObject.addProperty(USER_TYPE_TAG_2, USER_TYPE)
                jsonObject.addProperty(STUDENT_ID_TAG_2, viewModel.studentId.value)
                viewModel.getStudentAllergenCount(jsonObject, token)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        DashBoardActivity.dashBoardToolbar.setBackgroundColor(
            ContextCompat.getColor(
                requireContext(),
                R.color.dashboard_toolbar_color2
            )
        )

        DashBoardActivity.dashBoardNotificationLayout.visibility = View.GONE
        DashBoardActivity.dashBoardHelpLayout.visibility = View.GONE
        DashBoardActivity.dashBoardWalletLayout.visibility = View.GONE
        DashBoardActivity.dashBoardToolbar.visibility = View.VISIBLE

    }


    override fun getViewModel() = ViewAddedStudentProfileListViewModel::class.java
    override fun getRepository() =
        StudentRepository(remoteDataSource.buildApi(ApiInterface::class.java))

    private fun getToken() : String? {
        val loginResponse: LoginResponse? =
            UserPreferences.getAsObject<LoginResponse>(
                fragmentViewAddedStudentProfileListBinding.root.context,
                Constants.USER_DETAILS
            )
        return loginResponse?.response?.raws?.data?.token
    }


    fun studentListApiCall() {
        fragmentViewAddedStudentProfileListBinding.root.context.let { ctx->
            MethodClass.showProgressDialog(ctx as Activity)
//            activity as DashBoardActivity
//
//            val loginResponse: LoginResponse? =
//                UserPreferences.getAsObject<LoginResponse>(
//                    activity,
//                    Constants.USER_DETAILS
//                )
//            loginResponse?.response?.raws?.data?.token?.let { token ->
//                val jsonObject = MethodClass.getCommonJsonObject(act)
//                viewModel.studentList(jsonObject, getToken())
//            }
            getToken()?.let { token ->
                val jsonObject = MethodClass.getCommonJsonObject(ctx)
                viewModel.studentList(jsonObject, token)
            }

        }
    }

    fun getDeleteStudentResponse() {
       activity?.let { act->
           viewModel.deleteStudentListResponse?.observe(viewLifecycleOwner) {

               when (it) {
                   is Resource.Success -> {
                       Toast.makeText(
                           activity,
                           it.value.response.raws.successMessage,
                           Toast.LENGTH_LONG
                       ).show()
                       SELECTED_STUDENT_POSITION = STATUS_ZERO
                       MethodClass.hideProgressDialog(act)
                       studentListApiCall()

                       viewModel._deleteStudentListResponse.value = null
                       viewModel.deleteStudentListResponse = viewModel.deleteStudentListResponse

                   }
                   is Resource.Failure -> {
                       if (it.errorBody != null) {
                           MethodClass.hideProgressDialog(act)
                           it.errorString?.let { _ ->

                               it.errorString.let { it1 ->
                                   if (it.errorCode == 401)
                                       AppController.getInstance()
                                           .refreshTokenResponse(
                                               act,
                                               this,
                                               Constants.DELETE_STUDENT,
                                               Constants.REFRESH_TOKEN
                                           )
                                   else {
                                       MethodClass.showErrorDialog(
                                           act,
                                           it1,
                                           it.errorCode
                                       )
                                   }
                               }


                           }


                       }
                       viewModel._deleteStudentListResponse.value = null
                       viewModel.deleteStudentListResponse = viewModel.deleteStudentListResponse
                       MethodClass.hideProgressDialog(act)
                   }

                   else -> {}
               }


           }
       }
    }


    private fun studentAllergenCountResponse(){
        fragmentViewAddedStudentProfileListBinding.root.context.let { ctx ->

            viewModel.studentAllergenCountLiveData?.observe(viewLifecycleOwner){
                when (it) {
                    is Resource.Success -> {
                        MethodClass.hideProgressDialog(ctx as Activity)
                        ctx.startActivity(
                            Intent(ctx, RegistrationActivity::class.java)
                                .putExtra(FOR, EDIT_ALLERGEN)
                                .putExtra(STUDENT_ID_TAG, viewModel.studentId.value)
                                .putExtra(USER_TYPE_TAG, USER_TYPE)
                                .putExtra(STUDENT_NAME_TAG, viewModel.studentFirstName.value)
                                .putExtra(HAS_ALLERGEN__POPUP_SHOW_TAG, it.value.response.raws.data.allergen_popup))
                    }
                    is Resource.Failure -> {
                        if (it.errorBody != null) {
                            MethodClass.hideProgressDialog(ctx as Activity)
                            it.errorString?.let { error ->

                                if (it.errorCode == 401)
                                    AppController.getInstance()
                                        .refreshTokenResponse(
                                            ctx,
                                            this,
                                            Constants.ALLERGEN_COUNT,
                                            Constants.REFRESH_TOKEN
                                        )
                                else {
                                    MethodClass.showErrorDialog(
                                        ctx,
                                        error,
                                        it.errorCode
                                    )
                                }
                            }
                        }
                        MethodClass.hideProgressDialog(ctx as Activity)
                    }
                    else -> {}
                }
            }
        }

    }
}