package com.salonsolution.app.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.navigation.fragment.findNavController
import com.salonsolution.app.R
import com.salonsolution.app.data.preferences.AppSettingsPref
import com.salonsolution.app.databinding.FragmentPreLogingBinding
import com.salonsolution.app.ui.base.BaseFragment
import com.salonsolution.app.utils.Constants
import com.salonsolution.app.utils.SettingsUtils
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class PreLoginFragment : BaseFragment() {

    private lateinit var binding: FragmentPreLogingBinding

    @Inject
    lateinit var appSettingsPref: AppSettingsPref

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_pre_loging, container, false)

        initView()

        return binding.root
    }

    private fun initView() {

        if (SettingsUtils.getLanguage() == Constants.ENGLISH_CODE) {
            binding.language.ivChangeLanguage.setImageResource(R.drawable.english_flag)
        } else {
            binding.language.ivChangeLanguage.setImageResource(R.drawable.portuguese_flag)
        }

        binding.language.ivChangeLanguage.setOnClickListener {
            val action = LoginFragmentDirections.actionGlobalLanguageChangeFragment()
            findNavController().navigate(action)
        }

        binding.btSignup.setOnClickListener {
            appSettingsPref.setFirstTimeToFalse()
            val action = PreLoginFragmentDirections.actionPreLoginFragmentToSignupFragment()
            findNavController().navigate(action)
        }

        binding.btLogin.setOnClickListener {
            appSettingsPref.setFirstTimeToFalse()
            val action = PreLoginFragmentDirections.actionPreLoginFragmentToLoginFragment()
            findNavController().navigate(action)
        }
    }

}