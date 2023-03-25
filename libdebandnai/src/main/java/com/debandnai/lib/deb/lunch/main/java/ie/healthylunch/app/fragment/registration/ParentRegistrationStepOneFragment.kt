package ie.healthylunch.app.fragment.registration

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import ie.healthylunch.app.R
import ie.healthylunch.app.data.network.ApiInterface
import ie.healthylunch.app.data.repository.ParentRegistrationRepository
import ie.healthylunch.app.data.viewModel.ParentRegistrationStepOneViewModel
import ie.healthylunch.app.databinding.FragmentParentRegistrationStepOneBinding
import ie.healthylunch.app.ui.base.BaseFragment

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [ParentRegistrationStepOneFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ParentRegistrationStepOneFragment :
    BaseFragment<ParentRegistrationStepOneViewModel,ParentRegistrationRepository>() {

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
        // Inflate the layout for this fragment
        //return inflater.inflate(R.layout.fragment_parent_registration_step_one, container, false)
       // setUpViewModel()
        setUpViewModel()
        val binding: FragmentParentRegistrationStepOneBinding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_parent_registration_step_one,
            container,
            false
        )

        binding.context = this
        binding.viewModel = viewModel
        binding.lifecycleOwner = requireActivity()

        //get submit response
        viewModel.getSubmitResponse(requireActivity(),binding.Submit)

        return binding.root
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment ParentRegistrationStepOneFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            ParentRegistrationStepOneFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.firstName.value = arguments?.getString("f_name")
        viewModel.lastName.value = arguments?.getString("l_name")
        viewModel.email.value = arguments?.getString("email")
        viewModel.phNumber.value = arguments?.getString("ph_no")
    }
    override fun getViewModel() = ParentRegistrationStepOneViewModel::class.java
    override fun getRepository()=
        ParentRegistrationRepository(remoteDataSource.buildApi(ApiInterface::class.java))
}