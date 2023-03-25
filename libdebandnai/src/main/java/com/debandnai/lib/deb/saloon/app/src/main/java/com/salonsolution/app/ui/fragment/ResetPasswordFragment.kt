package com.salonsolution.app.ui.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.salonsolution.app.R
import com.salonsolution.app.data.network.ResponseMessageConverter
import com.salonsolution.app.data.network.ResponseState
import com.salonsolution.app.data.network.RetrofitHelper.handleApiError
import com.salonsolution.app.data.preferences.AppSettingsPref
import com.salonsolution.app.data.viewModel.ResetPasswordViewModel
import com.salonsolution.app.databinding.FragmentResetPasswordBinding
import com.salonsolution.app.ui.base.BaseFragment
import com.salonsolution.app.ui.popup.ErrorPopUp
import com.salonsolution.app.ui.popup.LoadingPopup
import com.salonsolution.app.utils.Constants
import com.salonsolution.app.utils.SettingsUtils
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class ResetPasswordFragment : BaseFragment() {
   private lateinit var binding: FragmentResetPasswordBinding
    private lateinit var viewModel: ResetPasswordViewModel
    private val navArgs: ResetPasswordFragmentArgs by navArgs()
    private val mTag = "tag"
    var loadingPopup: LoadingPopup? = null
    var errorPopUp: ErrorPopUp? = null

    @Inject
    lateinit var appSettingsPref: AppSettingsPref

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_reset_password, container, false)
        viewModel = ViewModelProvider(this)[ResetPasswordViewModel::class.java]
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
            val action = ForgotPasswordFragmentDirections.actionGlobalLanguageChangeFragment()
            findNavController().navigate(action)
        }

        binding.tvSignUp.setOnClickListener {
            val action = ResetPasswordFragmentDirections.actionResetPasswordFragmentToSignupFragment()
            findNavController().navigate(action)
        }
    }

    private fun setObserver() {
        viewModel.errorResetObserver.observe(viewLifecycleOwner) {
            Log.d("tag", "changed.")
            //This blank observer is required for MediatorLiveData
            //MediatorLiveData observe other LiveData objects
        }

        viewModel.resetPasswordResponseLiveData.observe(viewLifecycleOwner) {
            when (it) {
                is ResponseState.Loading -> {
                     loadingPopup?.show()
                }
                is ResponseState.Success -> {
                   loadingPopup?.dismiss()
                    Log.d(mTag,it.toString())
                    gotoNextDestination()
                }
                is ResponseState.Error -> {
                    loadingPopup?.dismiss()
                    Log.d(mTag,it.toString())
                    val msg = if(it.errorCode==400)
                        ResponseMessageConverter.getResetPasswordErrorMessage(binding.root.context,it.errorMessage)
                    else
                        it.errorMessage
                    handleApiError(it.isNetworkError, it.errorCode, msg)
                }
            }
        }
    }


    private fun getIntentData() {
        viewModel.email.value = navArgs.email
    }

    private fun gotoNextDestination() {
        if(!viewModel.email.value.isNullOrEmpty()) {
            val action = ResetPasswordFragmentDirections.actionResetPasswordFragmentToPasswordResetSuccessFragment()
            findNavController().navigate(action)
        }
    }

}