package com.movie.myapplication.ui.fragments

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.activityViewModels
import androidx.navigation.Navigation
import com.movie.myapplication.R
import com.movie.myapplication.data.viewModel.AuthViewModel
import com.movie.myapplication.databinding.FragmentLoginBinding
import com.movie.myapplication.db.entity.Auth
import com.movie.myapplication.db.entity.CurrentAuth
import com.movie.myapplication.ui.activity.AuthenticationActivity
import com.movie.myapplication.ui.base.BaseFragment
import com.movie.myapplication.utils.Constants
import com.movie.myapplication.utils.MethodClass
import java.util.Objects

class LoginFragment : BaseFragment() {
    private val authViewModel: AuthViewModel by activityViewModels()
    private lateinit var binding: FragmentLoginBinding
    private var loader: Dialog? = null
    var userData:List<Auth>?=null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_login, container, false)
        binding.viewModel=authViewModel
        binding.lifecycleOwner =this
        initialise()
        viewOnClick()
        return binding.root
    }
    private fun viewOnClick() {
        binding.signUpTv.setOnClickListener {
            Navigation.findNavController(binding.signUpTv)
                .navigate(
                    R.id.action_loginFragment_to_signupFragment,
                    null
                )
        }
        binding.loginBtn.setOnClickListener {
            if (validate()) {
                loader?.show()
                authenticate()
            }
        }

        binding.tvShow.setOnClickListener {
           if (binding.etPassword.inputType==1){
               binding.etPassword.inputType =225
               authViewModel.showHideText.value=getString(R.string.show)
           }
            else{
               binding.etPassword.inputType =1
               authViewModel.showHideText.value=getString(R.string.hide)
           }
        }
    }
    private fun initialise() {
        context?.let {
            authViewModel.titleTag.value=getString(R.string.welcome_back)
            loader = MethodClass.custom_loader(it, getString(R.string.please_wait))
        }

    }


    private fun validate():Boolean{
        activity?.let {act->

            if (authViewModel.userName.value?.isBlank() == true) {
                Toast.makeText(act, getString(R.string.valid_user_name), Toast.LENGTH_SHORT).show()
                binding.etUserName.requestFocus()
                return false
            }
            else  if (authViewModel.password.value?.isBlank() == true) {
                Toast.makeText(act, getString(R.string.password), Toast.LENGTH_SHORT).show()
                binding.etPassword.requestFocus()
                return false
            }
        }
        return true
    }
    private fun authenticate() {
        activity?.let { act->
            var authSuccess=false
            var currentPosition=-1
            authViewModel.userDetails.observe(viewLifecycleOwner) { user ->
                if (!user.isNullOrEmpty()) {
                    userData=user
                }

            }
            if (userData?.isEmpty() == false){
                for (i in userData!!.indices) {

                    if (Objects.equals(authViewModel.userName.value, userData!![i].user_email)){
                        //authViewModel.logoutAllUser()
                        //userItem.is_Active=true
                        authSuccess=true
                        currentPosition=i
                        break
                    }

                }

                if (authSuccess){

                    //authViewModel.updateAuth(user[currentPosition])
                    authViewModel.logoutAllUser()
                    var currentAuth = CurrentAuth(
                        null,
                        userData!![currentPosition].id
                    )
                    authViewModel.insertSession(currentAuth)
                    (act as? AuthenticationActivity)?.isLogin()
                }
                else{
                    loader?.dismiss()
                    Toast.makeText(act, getString(R.string.wrong_credentials), Toast.LENGTH_SHORT).show()
                }

            }
            else{
                Toast.makeText(act, getString(R.string.try_again), Toast.LENGTH_SHORT).show()
                loader?.dismiss()
            }


        }
    }


}