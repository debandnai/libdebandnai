package com.salonsolution.app.ui.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.salonsolution.app.R
import com.salonsolution.app.data.network.ResponseMessageConverter
import com.salonsolution.app.data.network.ResponseState
import com.salonsolution.app.data.network.RetrofitHelper.handleApiError
import com.salonsolution.app.data.preferences.AppSettingsPref
import com.salonsolution.app.data.preferences.UserSharedPref
import com.salonsolution.app.data.viewModel.OtpVerificationViewModel
import com.salonsolution.app.databinding.FragmentOtpVerificationBinding
import com.salonsolution.app.ui.base.BaseFragment
import com.salonsolution.app.ui.popup.ErrorPopUp
import com.salonsolution.app.ui.popup.LoadingPopup
import com.salonsolution.app.utils.Constants
import com.salonsolution.app.utils.SettingsUtils
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject


@AndroidEntryPoint
class OtpVerificationFragment : BaseFragment() {
    private lateinit var binding: FragmentOtpVerificationBinding
    private lateinit var viewModel: OtpVerificationViewModel
    private val navArgs: OtpVerificationFragmentArgs by navArgs()
    private val mTag = "tag"
    var loadingPopup: LoadingPopup? = null
    var errorPopUp: ErrorPopUp? = null

    @Inject
    lateinit var appSettingsPref: AppSettingsPref
    @Inject
    lateinit var userSharedPref: UserSharedPref

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_otp_verification, container, false)
        viewModel = ViewModelProvider(this)[OtpVerificationViewModel::class.java]
        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        setObserver()
        getIntentData()
    }


    private fun initView() {
        loadingPopup = LoadingPopup(activity)
        errorPopUp = ErrorPopUp(binding.root.context)
        if (SettingsUtils.getLanguage() == Constants.ENGLISH_CODE) {
            binding.language.ivChangeLanguage.setImageResource(R.drawable.english_flag)
        } else {
            binding.language.ivChangeLanguage.setImageResource(R.drawable.portuguese_flag)
        }
        binding.language.ivChangeLanguage.setOnClickListener {
            val action = SignupFragmentDirections.actionGlobalLanguageChangeFragment()
            findNavController().navigate(action)
        }
    }

    private fun setObserver() {
        viewModel.errorResetObserver.observe(viewLifecycleOwner) {
            Log.d("tag", "changed.")
            //This blank observer is required for MediatorLiveData
            //MediatorLiveData observe other LiveData objects
        }

        viewModel.verifyOtpResponseLiveData.observe(viewLifecycleOwner) {
            when (it) {
                is ResponseState.Loading -> {
                     loadingPopup?.show()
                }
                is ResponseState.Success -> {
//                    Toast.makeText(context,"Success: ${it.data?.response?.status?.msg}",
//                        Toast.LENGTH_LONG).show()
                    loadingPopup?.dismiss()
                    userSharedPref.setIsActive(1)
                    goToNextDestination()
                    Log.d(mTag,it.toString())
                    viewModel.clearUpdateState()
                }
                is ResponseState.Error -> {
                    Log.d(mTag,it.toString())
                    loadingPopup?.dismiss()
                    val msg = if(it.errorCode==400)
                        ResponseMessageConverter.getVerifyOtpErrorMessage(binding.root.context,it.errorMessage)
                    else
                        it.errorMessage
                    viewModel.otpError.value = Pair(true,msg?:"")
                    handleApiError(it.isNetworkError, it.errorCode, msg, false)
                    viewModel.clearUpdateState()
                }
            }
        }

        viewModel.resendOtpResponseLiveData.observe(viewLifecycleOwner) {
            when (it) {
                is ResponseState.Loading -> {
                     loadingPopup?.show()
                    viewModel.startTimer()
                }
                is ResponseState.Success -> {
                    loadingPopup?.dismiss()
                    Toast.makeText(context,binding.root.context.getString(R.string.otp_sent_successfully),
                        Toast.LENGTH_LONG).show()
                    Log.d(mTag,it.data?.response.toString())
                }
                is ResponseState.Error -> {
                    loadingPopup?.dismiss()
                    Log.d(mTag,it.toString())
                    val msg = if(it.errorCode==400)
                        ResponseMessageConverter.getResendOtpErrorMessage(binding.root.context,it.errorMessage)
                    else
                        it.errorMessage
                    handleApiError(it.isNetworkError,it.errorCode, msg)
                }
            }
        }

    }

    private fun goToNextDestination() {
        if(viewModel.isRegister) {
            val action = OtpVerificationFragmentDirections.actionOtpVerificationFragmentToSignupSuccessFragment()
            findNavController().navigate(action)
        }else {
            viewModel.email.value?.let {
               val action= OtpVerificationFragmentDirections.actionOtpVerificationFragmentToResetPasswordFragment(it)
                findNavController().navigate(action)
            }

        }
    }

    private fun getIntentData() {
        viewModel.email.value = navArgs.email
        viewModel.isRegister= navArgs.isRegister
    }


}