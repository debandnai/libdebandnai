package com.salonsolution.app.data.viewModel

import androidx.lifecycle.ViewModel
import com.salonsolution.app.data.repository.AuthRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(private val authRepository: AuthRepository): ViewModel(){


}