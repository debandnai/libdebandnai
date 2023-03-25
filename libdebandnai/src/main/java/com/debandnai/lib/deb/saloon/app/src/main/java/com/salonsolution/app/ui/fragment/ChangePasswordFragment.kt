package com.salonsolution.app.ui.fragment

import android.app.Dialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.salonsolution.app.R
import com.salonsolution.app.data.network.ResponseMessageConverter
import com.salonsolution.app.data.network.ResponseState
import com.salonsolution.app.data.network.RetrofitHelper.handleApiError
import com.salonsolution.app.data.preferences.AppSettingsPref
import com.salonsolution.app.data.viewModel.ChangePasswordViewModel
import com.salonsolution.app.databinding.FragmentChangePasswordBinding
import com.salonsolution.app.interfaces.DialogButtonClick
import com.salonsolution.app.ui.activity.DashboardActivity
import com.salonsolution.app.ui.base.BaseFragment
import com.salonsolution.app.ui.popup.CustomPopup
import com.salonsolution.app.ui.popup.ErrorPopUp
import com.salonsolution.app.ui.popup.LoadingPopup
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject


@AndroidEntryPoint
class ChangePasswordFragment : BaseFragment() {
    private lateinit var binding: FragmentChangePasswordBinding
    private lateinit var changePasswordViewModel: ChangePasswordViewModel
    var loadingPopup: LoadingPopup? = null
    var errorPopUp: ErrorPopUp? = null
    private val mTag = "tag"

    @Inject
    lateinit var appSettingsPref: AppSettingsPref

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_change_password, container, false)
        changePasswordViewModel = ViewModelProvider(this)[ChangePasswordViewModel::class.java]
        binding.viewModel = changePasswordViewModel
        binding.lifecycleOwner = this
        (activity as? DashboardActivity)?.manageNavigationView(
            isToolbarMenuShow = false,
            isTopLevelDestination = false,
            isShowBottomNav = false,
            isShowToolbar = true
        )

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
        setObserver()
    }

    private fun initView() {

        loadingPopup = LoadingPopup(activity)
        errorPopUp = ErrorPopUp(binding.root.context)

    }

    private fun setObserver() {
        changePasswordViewModel.errorResetObserver.observe(viewLifecycleOwner) {
            Log.d("tag", "changed.")
            //This blank observer is required for MediatorLiveData
            //MediatorLiveData observe other LiveData objects
        }

        changePasswordViewModel.updatePasswordLiveData.observe(viewLifecycleOwner) {
            when (it) {
                is ResponseState.Loading -> {
                    // loadingPopup?.show()
                    loadingPopup?.show()
                }
                is ResponseState.Success -> {
                    loadingPopup?.dismiss()
                    showSuccessfullyUpdatePopup()
                }
                is ResponseState.Error -> {
                    Log.d(mTag,it.toString())
                    loadingPopup?.dismiss()
                    val msg = if(it.errorCode==400)
                        ResponseMessageConverter.getUpdatePasswordErrorMessage(binding.root.context,it.errorMessage)
                    else
                        it.errorMessage
                    handleApiError(it.isNetworkError,it.errorCode, msg)
                }
            }
        }
    }

    private fun showSuccessfullyUpdatePopup() {
        context?.let {
            CustomPopup.showPopupMessageButtonClickDialog(it,
                null,
                it.getString(R.string.password_changed_successfully),
                it.getString(R.string.ok),
                false,
                object : DialogButtonClick {
                    override fun dialogButtonCallBack(dialog: Dialog) {
                        findNavController().popBackStack()
                    }

                }
            )
        }

    }

}