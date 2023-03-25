package com.movie.myapplication.ui.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import com.movie.myapplication.R
import com.movie.myapplication.databinding.FragmentLoginBinding
import com.movie.myapplication.databinding.FragmentSplashBinding
import com.movie.myapplication.ui.base.BaseFragment

class SplashFragment: BaseFragment() {
    private lateinit var binding: FragmentSplashBinding
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_splash, container, false)
        binding.lifecycleOwner =this
        return binding.root
    }


}