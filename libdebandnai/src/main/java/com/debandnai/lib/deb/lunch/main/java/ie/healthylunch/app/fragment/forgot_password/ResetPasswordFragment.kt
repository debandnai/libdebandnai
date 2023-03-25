package ie.healthylunch.app.fragment.forgot_password

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.databinding.DataBindingUtil
import ie.healthylunch.app.R
import ie.healthylunch.app.data.network.ApiInterface
import ie.healthylunch.app.data.repository.ForgotPasswordRepository
import ie.healthylunch.app.data.viewModel.ResetPasswordViewModel
import ie.healthylunch.app.databinding.FragmentResetPasswordBinding
import ie.healthylunch.app.fragment.registration.ParentRegistrationStepTwoFragment
import ie.healthylunch.app.ui.base.BaseFragment

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class ResetPasswordFragment :
    BaseFragment<ResetPasswordViewModel, ForgotPasswordRepository>() {
    private var param1: String? = null
    private var param2: String? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

      
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        setUpViewModel()
        val binding: FragmentResetPasswordBinding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_reset_password,
            container,
            false
        )
        binding.context = this
        binding.viewModel = viewModel
        binding.lifecycleOwner = requireActivity()

        //get Reset Password Response
        viewModel.getResetPasswordResponse(requireActivity(), binding.rlReset)

        return binding.root
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment ParentRegistrationStepTwoFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            ParentRegistrationStepTwoFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.email.value = arguments?.getString("email")

    }

    override fun getViewModel() = ResetPasswordViewModel::class.java
    override fun getRepository() =
        ForgotPasswordRepository(remoteDataSource.buildApi(ApiInterface::class.java))

}