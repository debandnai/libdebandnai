package com.salonsolution.app.ui.fragment

import android.annotation.SuppressLint
import android.content.res.Resources
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.navArgs
import com.salonsolution.app.R
import com.salonsolution.app.data.model.CmsModel
import com.salonsolution.app.data.network.RequestBodyHelper
import com.salonsolution.app.data.network.ResponseState
import com.salonsolution.app.data.network.RetrofitHelper.handleApiError
import com.salonsolution.app.data.viewModel.StaffListViewModel
import com.salonsolution.app.data.viewModel.TermsAndConditionsViewModel
import com.salonsolution.app.databinding.FragmentTermsAndConditionsBinding
import com.salonsolution.app.ui.activity.DashboardActivity
import com.salonsolution.app.ui.base.BaseFragment
import com.salonsolution.app.ui.popup.ErrorPopUp
import com.salonsolution.app.ui.popup.LoadingPopup
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class TermsAndConditionsFragment : BaseFragment() {
    private lateinit var binding: FragmentTermsAndConditionsBinding
    private lateinit var termsAndConditionsViewModel: TermsAndConditionsViewModel

    var loadingPopup: LoadingPopup? = null
    var errorPopUp: ErrorPopUp? = null

    @Inject
    lateinit var requestBodyHelper: RequestBodyHelper

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_terms_and_conditions, container, false)
        termsAndConditionsViewModel = ViewModelProvider(this)[TermsAndConditionsViewModel::class.java]

        (activity as? DashboardActivity)?.manageNavigationView(
            isToolbarMenuShow = false,
            isTopLevelDestination = true,
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

    @SuppressLint("SetJavaScriptEnabled")
    private fun initView() {
        loadingPopup = LoadingPopup(activity)
        errorPopUp = ErrorPopUp(binding.root.context)
        binding.webView.apply {
//                setInitialScale(1)
//                settings.loadWithOverviewMode = true
//                settings.useWideViewPort = true
//                scrollBarStyle = WebView.SCROLLBARS_OUTSIDE_OVERLAY
//                isScrollbarFadingEnabled = false
//                val res: Resources = resources
//                val fontSize = res.getDimension(R.dimen.tv_forgot_password_text_size_15sp)
//                settings.defaultFontSize = fontSize.toInt()
            settings.javaScriptEnabled = true
        }
        loadData()
    }

    private fun loadData() {
        //1 -> Terms & Conditions; 2 -> Privacy Policy
        termsAndConditionsViewModel.cmsDetails(requestBodyHelper.getCmsDetailsRequest(1))
    }

    private fun setObserver() {
        termsAndConditionsViewModel.cmsDetailsLiveData.observe(viewLifecycleOwner) {
            when (it) {
                is ResponseState.Loading -> {
                    loadingPopup?.show()
                }
                is ResponseState.Success -> {
                    loadingPopup?.dismiss()
                    updateView(it.data?.response?.data)
                    Log.d("tag", it.toString())
                }
                is ResponseState.Error -> {
                    loadingPopup?.dismiss()
                    Log.d("tag", it.errorMessage.toString())
                    handleApiError(it.isNetworkError, it.errorCode, binding.root.context.getString(R.string.something_went_wrong))
                }
            }
        }
    }

    private fun updateView(data: CmsModel?) {
        data?.let {
            val justifyTag = "<html><body style='text-align:left;'></body></html>"
            binding.webView.loadDataWithBaseURL(null,it.cmsContent + justifyTag, "text/html; charset=utf-8", "UTF-8",null)
        }
    }


}