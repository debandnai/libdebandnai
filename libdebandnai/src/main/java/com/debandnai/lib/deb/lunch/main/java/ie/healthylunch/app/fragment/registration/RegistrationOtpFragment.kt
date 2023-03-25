package ie.healthylunch.app.fragment.registration

import android.app.Activity
import android.app.Dialog
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import ie.healthylunch.app.R
import ie.healthylunch.app.data.model.parentRegistrationModel.ParentRegistrationResponse
import ie.healthylunch.app.data.network.ApiInterface
import ie.healthylunch.app.data.network.Resource
import ie.healthylunch.app.data.repository.ParentRegistrationRepository
import ie.healthylunch.app.data.viewModel.ParentRegistrationOtpViewModel
import ie.healthylunch.app.databinding.FragmentRegistrationOtpBinding
import ie.healthylunch.app.ui.RegistrationActivity.Companion.emailForOtp
import ie.healthylunch.app.ui.base.BaseFragment
import ie.healthylunch.app.utils.*
import ie.healthylunch.app.utils.Constants.Companion.CHECK_EMAIL_EXIST
import ie.healthylunch.app.utils.Constants.Companion.CHECK_REGISTRATION
import ie.healthylunch.app.utils.Constants.Companion.NEW_PARENT_EMAIL
import ie.healthylunch.app.utils.Constants.Companion.REFRESH_TOKEN
import ie.healthylunch.app.utils.CustomDialog.Companion.showEmailEditPopup
import ie.healthylunch.app.utils.UserPreferences
import org.json.JSONException
import org.json.JSONObject

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [RegistrationOtpFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class RegistrationOtpFragment :
    BaseFragment<ParentRegistrationOtpViewModel, ParentRegistrationRepository>(),
    DialogEditEmailListener {


    private lateinit var newEmailStr: String
    private lateinit var oldEmailStr: String
    private lateinit var editEmail: TextView
    private var dialog1: Dialog? = null
    val bundle = Bundle()

    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        setUpViewModel()


        val binding: FragmentRegistrationOtpBinding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_registration_otp,
            container,
            false
        )
        binding.context = this
        binding.viewModel = viewModel
        viewModel.registrationOtpFragment = this
        binding.lifecycleOwner = requireActivity()
        binding.editEmail.setOnClickListener {
            editEmail = binding.editEmail
            viewModel._editParentEmail.value = null
            viewModel.getNewParentEmail = viewModel._editParentEmail
            showEmailEditPopup(activity, viewModel.emailValue.value, "", this)
        }

        //get Verify Otp Response
        viewModel.getVerifyOtpResponse(requireActivity(), binding.SubmitSendOtp)

        return binding.root
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment RegistrationOtpFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            RegistrationOtpFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    override fun getViewModel() = ParentRegistrationOtpViewModel::class.java

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (arguments?.getString("email") != null) {
            viewModel.email.value =
                "${requireContext().getString(R.string.a_one_time_passcode_has_been_emailed_to)} ${
                    arguments?.getString("email")
                }"
            viewModel.emailValue.value = "${arguments?.getString("email")}"
            viewModel.check.value = arguments?.getString("check")
        } else {
            viewModel.email.value =
                "${requireContext().getString(R.string.a_one_time_passcode_has_been_emailed_to)} $emailForOtp"
            viewModel.emailValue.value = emailForOtp
            viewModel.check.value = "false"
        }
        if (CHECK_REGISTRATION) {
            viewModel.resendOtpClick(requireActivity(), 2)
            viewModel.editEmailTv.value = ""
        }


    }

    override fun getRepository() =
        ParentRegistrationRepository(remoteDataSource.buildApi(ApiInterface::class.java))

      
    override fun saveOnClick(
        dialog: Dialog,
        activity: Activity,
        oldEmail: String,
        newEmail: String,
        errorTv: TextView
    ) {

        MethodClass.showProgressDialog(requireActivity())
        val parentRegistrationResponse: ParentRegistrationResponse? =
            UserPreferences.getAsObject<ParentRegistrationResponse>(
                activity,
                Constants.USER_DETAILS
            )
        Constants.TOKEN =
            "Bearer " + parentRegistrationResponse?.response?.raws?.data?.token.toString()
        REFRESH_TOKEN =
            "Bearer " + parentRegistrationResponse?.response?.raws?.data?.refresh_token.toString()

        emailForOtp = newEmail
        bundle.putString("email", newEmail)
        bundle.putBoolean("check", false)
        val jsonObject2 = MethodClass.getCommonJsonObject(requireActivity())
        jsonObject2.addProperty("email", newEmail)
        viewModel.checkEmail(jsonObject2)

        newEmailStr = newEmail
        oldEmailStr = oldEmail

        getCheckEmailResponse(errorTv, dialog)


    }

      
    private fun getCheckEmailResponse(
        errorTv: TextView,
        dialog: Dialog
    ) {
        viewModel.checkEmailExistResponse?.observe(requireActivity()) {
            when (it) {
                is Resource.Success -> {
                    errorTv.text = it.value.response.raws.successMessage
                    dialog.dismiss()
                    MethodClass.hideProgressDialog(requireActivity())
                    getParentEmailResponse(dialog)

                    viewModel._checkEmailExistResponse.value = null
                    viewModel.checkEmailExistResponse = viewModel._checkEmailExistResponse
                }
                is Resource.Failure -> {
                    MethodClass.hideProgressDialog(requireActivity())
                    it.errorString?.let { it1 ->

                        when (it.errorCode) {
                            401 -> AppController.getInstance()
                                .refreshTokenResponse(
                                    requireActivity(),
                                    null,
                                    CHECK_EMAIL_EXIST,
                                    REFRESH_TOKEN
                                )
                            307, 426 -> MethodClass.showErrorDialog(
                                requireActivity(),
                                it1,
                                it.errorCode
                            )
                            else -> {
                                try {

                                    val jsonObject1 = JSONObject(it1)

                                    errorTv.text =
                                        jsonObject1.getJSONObject("response")
                                            .getJSONObject("raws")
                                            .getString("error_message")
                                            ?: activity?.resources?.getString(R.string.something_went_wrong)


                                } catch (e: JSONException) {
                                    e.printStackTrace()

                                    errorTv.text =
                                        activity?.resources?.getString(R.string.something_went_wrong)


                                }
                            }
                        }

                    }

                    viewModel._checkEmailExistResponse.value = null
                    viewModel.checkEmailExistResponse = viewModel._checkEmailExistResponse
                }
            }
        }
    }


    fun getParentEmailResponseFromAppController() {
        dialog1?.let { getParentEmailResponse(it) }
    }


    private fun getParentEmailResponse(dialog: Dialog) {
        MethodClass.showProgressDialog(requireActivity())
        dialog1 = dialog

        val jsonObject = MethodClass.getCommonJsonObject(requireActivity())
        jsonObject.addProperty("old_email", oldEmailStr)
        jsonObject.addProperty("new_email", newEmailStr)
        viewModel.setParentEmail(jsonObject, Constants.TOKEN)
        updateParentEmailResponse(dialog)

    }


    private fun updateParentEmailResponse(dialog: Dialog) {
        viewModel.getNewParentEmail?.observe(requireActivity()) {
            when (it) {
                is Resource.Success -> {
                    dialog.dismiss()
                    viewModel.emailValue.value = newEmailStr
                    viewModel.email.value =
                        "${requireActivity().getString(R.string.a_one_time_passcode_has_been_emailed_to)} $newEmailStr"

                    Handler(Looper.getMainLooper()).postDelayed({
                        CustomDialog.showInfoTypeDialogWithMsg(
                            activity,
                            it.value.response.raws.successMessage
                        )
                    }, 500)



                    Navigation.findNavController(editEmail)
                        .navigate(R.id.action_registrationOtpFragment_self)
                    MethodClass.hideProgressDialog(requireActivity())


                }
                is Resource.Failure -> {
                    dialog.dismiss()
                    when (it.errorCode) {
                        401 -> AppController.getInstance()
                            .refreshTokenResponse(
                                requireActivity(),
                                null,
                                NEW_PARENT_EMAIL,
                                REFRESH_TOKEN
                            )
                        307, 426 -> it.errorString?.let { it1 ->
                            MethodClass.showErrorDialog(
                                requireActivity(),
                                it1, it.errorCode
                            )
                        }
                        else -> {
                            Navigation.findNavController(editEmail)
                                .navigate(R.id.action_registrationOtpFragment_self)
                            MethodClass.hideProgressDialog(requireActivity())
                            viewModel._editParentEmail.value = null
                            viewModel.getNewParentEmail = viewModel._editParentEmail
                        }
                    }

                }
            }
        }
    }
}