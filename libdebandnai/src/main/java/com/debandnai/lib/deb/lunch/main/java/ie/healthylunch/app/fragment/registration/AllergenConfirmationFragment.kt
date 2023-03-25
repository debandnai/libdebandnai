package ie.healthylunch.app.fragment.registration

import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.navigation.Navigation
import ie.healthylunch.app.R
import ie.healthylunch.app.data.model.isLoginModel.IsLogin
import ie.healthylunch.app.data.viewModel.AllergenConfirmationViewModel
import ie.healthylunch.app.databinding.FragmentCheckHasAllergenBinding
import ie.healthylunch.app.ui.DashBoardActivity
import ie.healthylunch.app.ui.RegistrationActivity
import ie.healthylunch.app.ui.base.BaseFragmentForViewModelOnly
import ie.healthylunch.app.utils.*
import ie.healthylunch.app.utils.Constants.Companion.EDIT_ALLERGEN
import ie.healthylunch.app.utils.Constants.Companion.PAGE
import ie.healthylunch.app.utils.Constants.Companion.STATUS_ONE
import ie.healthylunch.app.utils.Constants.Companion.STUDENT_ID
import ie.healthylunch.app.utils.Constants.Companion.STUDENT_ID_TAG
import ie.healthylunch.app.utils.Constants.Companion.STUDENT_NAME
import ie.healthylunch.app.utils.Constants.Companion.STUDENT_NAME_TAG
import ie.healthylunch.app.utils.Constants.Companion.USER_TYPE
import ie.healthylunch.app.utils.Constants.Companion.USER_TYPE_TAG
import ie.healthylunch.app.utils.Constants.Companion.y_TAG

class AllergenConfirmationFragment : BaseFragmentForViewModelOnly<AllergenConfirmationViewModel>(),
    DialogLogoutListener {
    lateinit var fragmentCheckHasAllergenBinding: FragmentCheckHasAllergenBinding
    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View {
        setUpViewModel()
        fragmentCheckHasAllergenBinding= DataBindingUtil.inflate(
                inflater,
                R.layout.fragment_check_has_allergen,
                container,
                false
        )
        fragmentCheckHasAllergenBinding.context = this@AllergenConfirmationFragment
        fragmentCheckHasAllergenBinding.viewModel = viewModel
        viewModel.allergenConfirmationFragment = this@AllergenConfirmationFragment
        fragmentCheckHasAllergenBinding.lifecycleOwner = this@AllergenConfirmationFragment

        viewModel.isSubmitEnabled.value = true
        onViewClick()
        return fragmentCheckHasAllergenBinding.root
    }

    private fun onViewClick() {
        fragmentCheckHasAllergenBinding.rlContinue.setOnClickListener {
            val bundle = Bundle()
            bundle.putString(PAGE, viewModel.pageFrom.value)
            bundle.putString(STUDENT_NAME, viewModel.studentFirstName.value)
            viewModel.studentId.value?.let {  }
            bundle.putString(USER_TYPE_TAG, viewModel.userType.value)
            viewModel.studentId.value?.let { studentId->
                STUDENT_ID =  studentId
                bundle.putInt(STUDENT_ID_TAG, studentId)
            }
            viewModel.userType.value?.let { userType-> USER_TYPE = userType}
            if (yesNoValidation()) {
                viewModel.isSubmitEnabled.value = false
                if (viewModel.promotionAlert.value == y_TAG) {
                    Navigation.findNavController(fragmentCheckHasAllergenBinding.rlContinue)
                            .navigate(
                                    R.id.action_checkHasAllergenFragment_to_allergenFragment,
                                    bundle
                            )
                } else if (viewModel.pageFrom.value?.equals(EDIT_ALLERGEN,true) == true) {
                    (activity as? RegistrationActivity)?.finish()
                }else
                {
                    Navigation.findNavController(fragmentCheckHasAllergenBinding.rlContinue)
                            .navigate(
                                    R.id.action_checkHasAllergenFragment_to_addStudentSuccessFragment,
                                    bundle
                            )

                }
            }

        }

        fragmentCheckHasAllergenBinding.backIv.setOnClickListener{
            activity?.let {act->
                val isLogin: IsLogin? = UserPreferences.getAsObject<IsLogin>(
                    act,
                    Constants.LOGIN_CHECK
                )
                if (isLogin?.isLogin != STATUS_ONE) {
                    CustomDialog.logoutDialog(
                        act,
                        act.resources.getString(R.string.do_you_want_to_exit_this_page_),
                        this@AllergenConfirmationFragment
                    )
                } else {
                    val intent = Intent(activity, DashBoardActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    act.startActivity(intent)
                    Constants.HAS_STUDENT_ADDED = false
                    Constants.ADD_ANOTHER_STUDENT = false


                }

            }
        }

    }

    //validation
    private fun yesNoValidation(): Boolean {
        viewModel.invisibleErrorTexts()
        if (viewModel.promotionAlert.value.isNullOrBlank()) {
            viewModel.promotionAlertError.value =getString(R.string.please_select_yes__no_option)
            viewModel.promotionAlertErrorVisible.value = true
            return false
        }
        return true
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.studentId.value = arguments?.getInt(STUDENT_ID_TAG)
        viewModel.userType.value = arguments?.getString(USER_TYPE_TAG)
        viewModel.studentFirstName.value = arguments?.getString(STUDENT_NAME_TAG)
        viewModel.pageFrom.value = arguments?.getString(PAGE)
    }

    override fun getViewModel() = AllergenConfirmationViewModel::class.java
    override fun yesOnClick(dialog: Dialog) {
        dialog.dismiss()
        Constants.ADD_ANOTHER_STUDENT = false
        (activity as? RegistrationActivity)?.finish()
    }

    override fun noOnClick(dialog: Dialog) {
        dialog.dismiss()
    }



}