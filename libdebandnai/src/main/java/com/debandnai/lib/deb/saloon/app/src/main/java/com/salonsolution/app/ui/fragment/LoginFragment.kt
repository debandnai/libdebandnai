package com.salonsolution.app.ui.fragment

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.salonsolution.app.R
import com.salonsolution.app.data.model.LoginModel
import com.salonsolution.app.data.network.ResponseMessageConverter
import com.salonsolution.app.data.network.ResponseState
import com.salonsolution.app.data.network.RetrofitHelper.handleApiError
import com.salonsolution.app.data.preferences.AppSettingsPref
import com.salonsolution.app.data.preferences.UserSharedPref
import com.salonsolution.app.data.viewModel.LoginViewModel
import com.salonsolution.app.databinding.FragmentLoginBinding
import com.salonsolution.app.ui.activity.DashboardActivity
import com.salonsolution.app.ui.base.BaseFragment
import com.salonsolution.app.ui.popup.ErrorPopUp
import com.salonsolution.app.ui.popup.LoadingPopup
import com.salonsolution.app.utils.Constants
import com.salonsolution.app.utils.SettingsUtils
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class LoginFragment : BaseFragment() {

    private lateinit var binding: FragmentLoginBinding
    private lateinit var viewModel: LoginViewModel
    private val mTag = "tag"
    var loadingPopup: LoadingPopup? = null
    var errorPopUp: ErrorPopUp? = null

    @Inject
    lateinit var appSettingsPref: AppSettingsPref

    @Inject
    lateinit var userSharedPref: UserSharedPref

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_login, container, false)
        viewModel = ViewModelProvider(this)[LoginViewModel::class.java]
        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner
        manageFlow()


        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        setObserver()
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
            val action = LoginFragmentDirections.actionGlobalLanguageChangeFragment()
            findNavController().navigate(action)
        }

        binding.tvSignUp.setOnClickListener {
            val action = LoginFragmentDirections.actionLoginFragmentToSignupFragment()
            findNavController().navigate(action)
        }
        binding.tvForgotPassword.setOnClickListener {
            val action = LoginFragmentDirections.actionLoginFragmentToForgotPasswordFragment()
            findNavController().navigate(action)
        }

        if (appSettingsPref.getRememberMe().isNotEmpty()) {
            viewModel.email.postValue(appSettingsPref.getRememberMe())
            viewModel.rememberMe.postValue(true)
        }
    }

    private fun setObserver() {
        viewModel.errorResetObserver.observe(viewLifecycleOwner) {
            Log.d("tag", "changed.")
            //This blank observer is required for MediatorLiveData
            //MediatorLiveData observe other LiveData objects
        }

        viewModel.loginResponseLiveData.observe(viewLifecycleOwner) {
            when (it) {
                is ResponseState.Loading -> {
                    loadingPopup?.show()
                }
                is ResponseState.Success -> {
                    loadingPopup?.dismiss()
                    manageRememberMe()
                    goToNextDestination(it.data?.response?.data)
                    viewModel.clearUpdateState()

                    Log.d(mTag, it.toString())
                }
                is ResponseState.Error -> {
                    loadingPopup?.dismiss()
                    Log.d(mTag, it.toString())
                    val msg = if(it.errorCode==400)
                        ResponseMessageConverter.getLoginErrorMessage(binding.root.context,it.errorMessage)
                    else
                        it.errorMessage
                    handleApiError(it.isNetworkError, it.errorCode, msg)
                    viewModel.clearUpdateState()
                }
            }
        }
    }

    private fun goToNextDestination(data: LoginModel?) {
        data.let {
//            1 ->Acive user[Go to dashboard]; 0 -> Inactive user[Cannot Login]; 2 -> Unverified User[Go to otp page to verify email]
            when (it?.isActive) {
                0 -> {
                    errorPopUp?.showMessageDialog(binding.root.context.getString(R.string.your_account_is_inactive))
                }
                1 -> {
                    userSharedPref.setLoginData(it)
                    val intent = Intent(context, DashboardActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                    startActivity(intent)
                    activity?.finish()
                }
                2 -> {
                    userSharedPref.setLoginData(it)
                    it.email?.let { email ->
                        val action =
                            LoginFragmentDirections.actionLoginFragmentToOtpVerificationFragment(
                                email,
                                true
                            )
                        findNavController().navigate(action)
                    }
                }
                else -> {
                    errorPopUp?.showMessageDialog(binding.root.context.getString(R.string.something_went_wrong))
                }
            }
        }
    }

    private fun manageRememberMe() {
        if (viewModel.rememberMe.value == true) {
            appSettingsPref.setRememberMe(viewModel.email.value?.trim())
        } else {
            appSettingsPref.setRememberMe(null)
        }
    }

    private fun manageFlow() {
        when {
            appSettingsPref.isFirstTimeInstall() -> {
                findNavController().navigate(R.id.action_global_chooseLanguageFragment)
            }
            userSharedPref.getIsActive() == 2 && userSharedPref.getUserToken().isNotEmpty() -> {
                val loginData = userSharedPref.getLoginData()
                loginData.email?.let {
                    val action =
                        LoginFragmentDirections.actionLoginFragmentToOtpVerificationFragment(
                            it,
                            true
                        )
                    findNavController().navigate(action)
                }

            }
            else -> {
                //do nothing
            }
        }
    }


}