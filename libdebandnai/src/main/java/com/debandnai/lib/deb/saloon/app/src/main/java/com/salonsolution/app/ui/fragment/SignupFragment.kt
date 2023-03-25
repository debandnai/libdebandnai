package com.salonsolution.app.ui.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.salonsolution.app.R
import com.salonsolution.app.data.model.CountryListModel
import com.salonsolution.app.data.network.ResponseMessageConverter
import com.salonsolution.app.data.network.ResponseState
import com.salonsolution.app.data.network.RetrofitHelper.handleApiError
import com.salonsolution.app.data.preferences.AppSettingsPref
import com.salonsolution.app.data.viewModel.SignUpViewModel
import com.salonsolution.app.databinding.FragmentSignupBinding
import com.salonsolution.app.ui.base.BaseFragment
import com.salonsolution.app.ui.popup.ErrorPopUp
import com.salonsolution.app.ui.popup.LoadingPopup
import com.salonsolution.app.utils.Constants
import com.salonsolution.app.utils.SettingsUtils
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class SignupFragment : BaseFragment(){

    private lateinit var binding: FragmentSignupBinding
    private lateinit var viewModel: SignUpViewModel
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
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_signup, container, false)
        viewModel = ViewModelProvider(this)[SignUpViewModel::class.java]
        binding.viewModel = viewModel
        binding.lifecycleOwner = viewLifecycleOwner

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
            val action = SignupFragmentDirections.actionGlobalLanguageChangeFragment()
            findNavController().navigate(action)
        }

        binding.tvLogin.setOnClickListener {
            val action = SignupFragmentDirections.actionSignupFragmentToLoginFragment()
            findNavController().navigate(action)
        }

        binding.mySpinner.setOnItemClickListener { parent, _, position, _ ->
            val item = parent.getItemAtPosition(position) as? String
            viewModel.countryCode.value = item
           // binding.mySpinner.setText(item.countryCode)

        }
    }

    private fun setObserver() {
        viewModel.errorResetObserver.observe(viewLifecycleOwner) {
            Log.d("tag", "changed.")
            //This blank observer is required for MediatorLiveData
            //MediatorLiveData observe other LiveData objects
        }

        viewModel.countriesDetailsLiveData.observe(viewLifecycleOwner) {
            when (it) {
                is ResponseState.Loading -> {
                    loadingPopup?.show()
                }
                is ResponseState.Success -> {
//                    Toast.makeText(context,"Success: ${it.data?.response?.status?.msg}",
//                        Toast.LENGTH_LONG).show()
                    loadingPopup?.dismiss()
                    showCountryCode(it.data?.response?.data)
                    Log.d(mTag, it.toString())

                }
                is ResponseState.Error -> {
                    loadingPopup?.dismiss()
                    Log.d(mTag, it.toString())
                    handleApiError(it.isNetworkError, it.errorCode, it.errorMessage)
                    viewModel.clearUpdateState()
                }
            }
        }

        viewModel.registerResponseLiveData.observe(viewLifecycleOwner) {
            when (it) {
                is ResponseState.Loading -> {
                    loadingPopup?.show()
                }
                is ResponseState.Success -> {
//                    Toast.makeText(context,"Success: ${it.data?.response?.status?.msg}",
//                        Toast.LENGTH_LONG).show()
                    loadingPopup?.dismiss()
                    Log.d(mTag, it.toString())
                    gotoNextDestination()
                    viewModel.clearUpdateState()
                }
                is ResponseState.Error -> {
                    loadingPopup?.dismiss()
                    Log.d(mTag, it.toString())
                    val msg = if(it.errorCode==400)
                        ResponseMessageConverter.getRegisterErrorMessage(binding.root.context,it.errorMessage)
                    else
                        it.errorMessage
                    handleApiError(it.isNetworkError, it.errorCode, msg)
                    viewModel.clearUpdateState()
                }
            }
        }
    }

    private fun showCountryCode(data: CountryListModel?) {
        data?.let { it ->
            // val code = arrayOf("+91", "+344", "+677")
             // val customArrayAdapter = CustomArrayAdapter(binding.root.context,R.layout.spinner_layout,it.countryList)
            val list = it.countryList.map { item ->
                item.countryCode
            }
            val adapter = ArrayAdapter(binding.root.context, R.layout.spinner_layout, list)
            // binding.mySpinner.setText(it.countryList[0].countryCode)
            if (data.countryList.isNotEmpty()) {
                binding.mySpinner.setText(data.countryList[0].countryCode)
                viewModel.countryCode.value = data.countryList[0].countryCode
            }
            binding.mySpinner.setAdapter(adapter)
        }
    }

    private fun gotoNextDestination() {
        if (!viewModel.email.value.isNullOrEmpty()) {
            val action = SignupFragmentDirections.actionSignupFragmentToOtpVerificationFragment(
                email = viewModel.email.value!!,
                isRegister = true
            )
            findNavController().navigate(action)
        }
    }

}
