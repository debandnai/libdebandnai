package ie.healthylunch.app.fragment.forgot_password

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import ie.healthylunch.app.R
import ie.healthylunch.app.data.network.ApiInterface
import ie.healthylunch.app.data.repository.ForgotPasswordRepository
import ie.healthylunch.app.data.viewModel.ForgotPasswordSuccessViewModel
import ie.healthylunch.app.databinding.FragmentForgotPasswordSuccessBinding
import ie.healthylunch.app.ui.base.BaseFragment


class ForgotPasswordSuccessFragment :
    BaseFragment<ForgotPasswordSuccessViewModel, ForgotPasswordRepository>() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        setUpViewModel()
        val binding: FragmentForgotPasswordSuccessBinding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_forgot_password_success,
            container,
            false

        )
        binding.context = this
        binding.viewModel = viewModel
        binding.lifecycleOwner = requireActivity()

        return binding.root
    }


    override fun getViewModel() = ForgotPasswordSuccessViewModel::class.java
    override fun getRepository()=
        ForgotPasswordRepository(remoteDataSource.buildApi(ApiInterface::class.java))

}