package ie.healthylunch.app.fragment.registration

import android.app.Dialog
import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.databinding.DataBindingUtil
import androidx.navigation.Navigation
import androidx.navigation.fragment.NavHostFragment
import ie.healthylunch.app.R
import ie.healthylunch.app.data.model.loginModel.LoginResponse
import ie.healthylunch.app.data.network.ApiInterface
import ie.healthylunch.app.data.network.Resource
import ie.healthylunch.app.data.repository.RemoveProductsHavingMayAllergenRepository
import ie.healthylunch.app.data.viewModel.RemoveProductsHavingMayAllergenViewModel
import ie.healthylunch.app.databinding.FragmentAllergenProductRemoveConfirmationBinding
import ie.healthylunch.app.ui.RegistrationActivity
import ie.healthylunch.app.ui.base.BaseFragment
import ie.healthylunch.app.utils.*
import ie.healthylunch.app.utils.Constants.Companion.LAST_ADDED_STUDENT_NAME
import ie.healthylunch.app.utils.Constants.Companion.N
import ie.healthylunch.app.utils.Constants.Companion.ONE
import ie.healthylunch.app.utils.Constants.Companion.STATUS_ONE
import ie.healthylunch.app.utils.Constants.Companion.STATUS_ZERO
import ie.healthylunch.app.utils.Constants.Companion.STUDENT_ID_TAG_2
import ie.healthylunch.app.utils.Constants.Companion.USER_TYPE_TAG_2
import ie.healthylunch.app.utils.Constants.Companion.Y
import ie.healthylunch.app.utils.Constants.Companion.ZERO
import ie.healthylunch.app.utils.Constants.Companion.remove_product_mayallergen

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [AllergenProductRemoveConfirmationFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class AllergenProductRemoveConfirmationFragment :
    BaseFragment<RemoveProductsHavingMayAllergenViewModel, RemoveProductsHavingMayAllergenRepository>() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    private lateinit var binding: FragmentAllergenProductRemoveConfirmationBinding
    private lateinit var loader: Dialog
    val bundle = Bundle()

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
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_allergen_product_remove_confirmation,
            container,
            false
        )
        binding.context = this
        binding.viewModel = viewModel
        //viewModel.allergenProductRemoveConfirmationFragment = this
        binding.lifecycleOwner = this
        //viewModel.activity = requireActivity() as RegistrationActivity

        viewModel.isSubmitEnabled.value = true

        //remove Product May Allergen Response
        removeProductMayAllergenResponse()
        loader = MethodClass.loaderDialog(binding.root.context)
        return binding.root
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment AllergenProductRemoveConfirmationFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            AllergenProductRemoveConfirmationFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        LAST_ADDED_STUDENT_NAME = arguments?.getString("studentName").toString()
        viewModel.page.value = arguments?.getString(Constants.PAGE)

        onViewClick()
    }


    override fun getViewModel() = RemoveProductsHavingMayAllergenViewModel::class.java

    override fun getRepository() = RemoveProductsHavingMayAllergenRepository(
        remoteDataSource.buildApi(
            ApiInterface::class.java
        )
    )

    private fun onViewClick(){
        binding.continueRl.setOnClickListener {
            continueClick(binding.continueRl)
        }
        binding.noLayoutRl.setOnClickListener {
            promotionOnClick(ZERO)
        }
        binding.yesLayoutRl.setOnClickListener {
            promotionOnClick(ONE)
        }
        binding.backIv.setOnClickListener {
            back(binding.backIv)
        }
    }

    fun continueClick(v: View) {
        bundle.putString("studentName", LAST_ADDED_STUDENT_NAME)

        viewModel.studentId.value?.let { bundle.putInt("studentId", it) }
        bundle.putString("userType", viewModel.userType.value)
        if (yesNoValidation(v)) {
            viewModel.isSubmitEnabled.value = false
            if (viewModel.promotionAlert.value == Y) {
                viewModel.isRemoveProduct = Constants.YES
                removeProductMayAllergenApiCall()
            } else {
//                Navigation.findNavController(v)
//                    .navigate(
//                        R.id.action_allergenProductRemoveConfirmation_to_addStudentSuccessFragment,
//                        bundle
//                    )
                viewModel.isRemoveProduct = Constants.NO
                removeProductMayAllergenApiCall()
            }
        }
    }

    //validation
    private fun yesNoValidation(view: View): Boolean {

        invisibleErrorTexts()


        if (viewModel.promotionAlert.value.isNullOrBlank()) {
            viewModel.promotionAlertError.value =
                view.context.getString(R.string.please_select_yes__no_option)
            viewModel.promotionAlertErrorVisible.value = true
            return false
        }
        return true
    }

    //error texts primary state
    fun invisibleErrorTexts() {

        viewModel.promotionAlertErrorVisible.value = false
    }

    fun promotionOnClick(promotionAlertInt: Int) {
        invisibleErrorTexts()

        if (promotionAlertInt == 1) {
            viewModel.promotionAlert.value = Y
            viewModel.yesRadioButton.value = true
            viewModel.noRadioButton.value = false
        } else {
            viewModel.promotionAlert.value = N
            viewModel.yesRadioButton.value = false
            viewModel.noRadioButton.value = true
        }
    }

    //Remove Product May Allergen api call
    fun removeProductMayAllergenApiCall() {
        //MethodClass.showProgressDialog(activity)
        loader.show()

        val loginResponse: LoginResponse? =
            UserPreferences.getAsObject<LoginResponse>(
                binding.root.context,
                Constants.USER_DETAILS
            )

        loginResponse?.response?.raws?.data?.token?.let { Constants.TOKEN = it }
        Constants.REFRESH_TOKEN = loginResponse?.response?.raws?.data?.refreshToken.toString()
        val jsonObject = MethodClass.getCommonJsonObject(binding.root.context)
        jsonObject.addProperty(remove_product_mayallergen, viewModel.isRemoveProduct)
        jsonObject.addProperty(STUDENT_ID_TAG_2, Constants.STUDENT_ID)
        jsonObject.addProperty(USER_TYPE_TAG_2, Constants.USER_TYPE)

        viewModel.removeProductMayAllergen(jsonObject, Constants.TOKEN)

    }

    //initiate PaymentIntent Response
    fun removeProductMayAllergenResponse() {
        with(binding.root.context){
            viewModel.removeProductMayAllergenResponse?.observe(viewLifecycleOwner) { response ->

                when (response) {
                    is Resource.Success -> {
                        //MethodClass.hideProgressDialog(this)
                        loader.dismiss()

                        Toast.makeText(this, response.value.response.raws.successMessage, Toast.LENGTH_SHORT).show()
                        val navHostFragment =
                            requireActivity().supportFragmentManager.findFragmentById(R.id.fragmentContainerView) as NavHostFragment
                        val navController = navHostFragment.navController

                        if (viewModel.page.value?.equals(Constants.EDIT_ALLERGEN,true) == true){
                            requireActivity().finish()
                        } else{
                            navController.navigate(
                                R.id.action_allergenProductRemoveConfirmation_to_addStudentSuccessFragment,
                                bundle
                            )
                        }

                        viewModel.isSubmitEnabled.value = true
                    }
                    is Resource.Failure -> {
                        if (response.errorBody != null) {
                            response.errorString?.let { _ ->


                                if (response.errorCode == 401) {

                                    AppController.getInstance()
                                        .refreshTokenResponse(
                                            requireActivity(),
                                            this@AllergenProductRemoveConfirmationFragment,
                                            Constants.REMOVE_PRODUCTS_HAVING_MAY_ALLERGEN,
                                            Constants.REFRESH_TOKEN
                                        )
                                } else
                                    response.errorString.let { it1 ->

                                        MethodClass.showErrorDialog(
                                            requireActivity(),
                                            it1,
                                            response.errorCode
                                        )
                                    }
                            }


                        }
                        //MethodClass.hideProgressDialog(activity)
                        loader.dismiss()
                        viewModel._removeProductMayAllergenResponse.value = null
                        viewModel.removeProductMayAllergenResponse = viewModel._removeProductMayAllergenResponse
                        viewModel.isSubmitEnabled.value = true

                    }


                    else -> {}
                }

            }
        }
    }

    fun back(v: View) {

        Navigation.findNavController(v).popBackStack()
    }
}