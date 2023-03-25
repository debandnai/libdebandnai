package com.salonsolution.app.ui.fragment

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.navigation.fragment.findNavController
import com.salonsolution.app.R
import com.salonsolution.app.data.preferences.AppSettingsPref
import com.salonsolution.app.data.preferences.UserSharedPref
import com.salonsolution.app.databinding.FragmentSignupSuccessBinding
import com.salonsolution.app.ui.activity.DashboardActivity
import com.salonsolution.app.ui.base.BaseFragment
import com.salonsolution.app.utils.Constants
import com.salonsolution.app.utils.SettingsUtils
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject


@AndroidEntryPoint
class SignupSuccessFragment : BaseFragment() {
    private lateinit var binding: FragmentSignupSuccessBinding

    @Inject
    lateinit var appSettingsPref: AppSettingsPref
    @Inject
    lateinit var userSharedPref: UserSharedPref

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_signup_success, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
    }

    private fun initView() {
        if (SettingsUtils.getLanguage() == Constants.ENGLISH_CODE) {
            binding.language.ivChangeLanguage.setImageResource(R.drawable.english_flag)
        } else {
            binding.language.ivChangeLanguage.setImageResource(R.drawable.portuguese_flag)
        }
        binding.language.ivChangeLanguage.setOnClickListener {
            val action = SignupFragmentDirections.actionGlobalLanguageChangeFragment()
            findNavController().navigate(action)
        }

        val token = userSharedPref.getUserToken()
        if(token==""){
            binding.btGoToLogin.text = binding.root.context.getString(R.string.go_to_login)
        }else{
            binding.btGoToLogin.text = binding.root.context.getString(R.string.go_to_dashboard)
        }

        binding.btGoToLogin.setOnClickListener {
            if(token==""){
                val action = SignupSuccessFragmentDirections.actionSignupSuccessFragmentToLoginFragment()
                findNavController().navigate(action)
            }else{
                Log.d("tag","isActive: ${userSharedPref.getIsActive()}")
                val intent = Intent(context, DashboardActivity::class.java)
               // intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
                startActivity(intent)
                activity?.finish()
            }
        }
    }


}