package ie.healthylunch.app.fragment.forgot_password

import android.app.Activity
import android.os.Build
import android.os.Bundle
import android.util.Patterns
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.databinding.DataBindingUtil
import androidx.navigation.Navigation
import androidx.navigation.fragment.findNavController
import ie.healthylunch.app.R
import ie.healthylunch.app.data.network.ApiInterface
import ie.healthylunch.app.data.network.Resource
import ie.healthylunch.app.data.repository.ForgotPasswordRepository
import ie.healthylunch.app.data.viewModel.ForgotPasswordStepOneViewModel
import ie.healthylunch.app.databinding.FragmentAboutUsBinding
import ie.healthylunch.app.databinding.FragmentForgotPasswordStepOneBinding
import ie.healthylunch.app.ui.ForgotPasswordActivity
import ie.healthylunch.app.ui.base.BaseFragment
import ie.healthylunch.app.utils.AppController
import ie.healthylunch.app.utils.Constants
import ie.healthylunch.app.utils.Constants.Companion.EMAIL_TAG
import ie.healthylunch.app.utils.Constants.Companion.ERROR_STATUS_401
import ie.healthylunch.app.utils.Constants.Companion.STATUS_ONE
import ie.healthylunch.app.utils.Constants.Companion.otp_purpose
import ie.healthylunch.app.utils.MethodClass


class ForgotPasswordStepOneFragment :
    BaseFragment<ForgotPasswordStepOneViewModel, ForgotPasswordRepository>() {
    lateinit var forgotPasswordBinding: FragmentForgotPasswordStepOneBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        setUpViewModel()
        forgotPasswordBinding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_forgot_password_step_one,
            container,
            false
        )
        forgotPasswordBinding.context = this
        forgotPasswordBinding.viewModel = viewModel
        forgotPasswordBinding.lifecycleOwner = this

        //get Send Otp Response
        getSendOtpResponse()

        viewOnClick()
        return forgotPasswordBinding.root
    }

    private fun viewOnClick() {
        forgotPasswordBinding.SubmitForgotPassword.setOnClickListener{
            if (validation()) {
                viewModel.isSubmitEnabled.value = false

                sendOtp()
            }
        }

        forgotPasswordBinding.ivSkipWizard.setOnClickListener {
            back()
        }
    }

    fun sendOtp() {
        forgotPasswordBinding.root.context?.let { ctx->
            val jsonObject = MethodClass.getCommonJsonObject(ctx)
            jsonObject.addProperty(EMAIL_TAG, viewModel.email.value)
            jsonObject.addProperty(otp_purpose, STATUS_ONE)
            MethodClass.showProgressDialog(ctx as Activity)
            viewModel.sendOtpForReg(jsonObject)
        }
    }

    //validation
    private fun validation(): Boolean {
        viewModel.invisibleErrorTexts()
        if (viewModel.email.value.isNullOrBlank()) {
            setErrorText(getString(R.string.please_enter_email_address))
            return false
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(viewModel.email.value!!).matches()) {
            setErrorText(getString(R.string.please_enter_valid_email_address))
            return false
        }
        return true
    }

    private fun setErrorText(text: String){
        viewModel.emailError.value = text
        viewModel.emailErrorVisible.value = true
    }
    override fun getViewModel() = ForgotPasswordStepOneViewModel::class.java
    override fun getRepository() =
        ForgotPasswordRepository(remoteDataSource.buildApi(ApiInterface::class.java))



    private fun getSendOtpResponse() {
        forgotPasswordBinding.root.context?.let { ctx ->
            viewModel.parentOtpResponse?.observe(viewLifecycleOwner) { response->

                when (response) {

                    is Resource.Success -> {
                        MethodClass.hideProgressDialog(ctx as Activity)
                        val bundle = Bundle()
                        bundle.putString(EMAIL_TAG, viewModel.email.value)
                        findNavController().navigate(
                            R.id.action_forgotPasswordStepOneFragment_to_forgotPasswordStepTwoFragment,
                            bundle
                        )
                        viewModel.isSubmitEnabled.value = true
                        viewModel._parentOtpResponse.value = null
                        viewModel.parentOtpResponse = viewModel._parentOtpResponse
                    }
                    is Resource.Failure -> {
                        if (response.errorBody != null) {
                            MethodClass.hideProgressDialog(ctx as Activity)
                            response.errorString?.let { error ->
                                if (response.errorCode == ERROR_STATUS_401)
                                    AppController.getInstance()
                                        .refreshTokenResponse(
                                            ctx,
                                            null,
                                            Constants.SEND_OTP,
                                            Constants.REFRESH_TOKEN
                                        )
                                else {
                                    MethodClass.showErrorDialog(
                                        ctx,
                                        error,
                                        response.errorCode
                                    )
                                }

                            }


                        }
                        viewModel.isSubmitEnabled.value = true
                        viewModel._parentOtpResponse.value = null
                        viewModel.parentOtpResponse = viewModel._parentOtpResponse
                    }
                    else -> {}
                }

            }
        }
    }

    fun back() {
        requireActivity().finish()
    }

    }