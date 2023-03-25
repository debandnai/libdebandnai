package com.merkaaz.app.ui.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.activityViewModels
import com.merkaaz.app.R
import com.merkaaz.app.data.viewModel.DashboardViewModel
import com.merkaaz.app.databinding.FragmentTermsOfUseBinding
import com.merkaaz.app.ui.activity.DashBoardActivity
import com.merkaaz.app.ui.base.BaseFragment

class TermsOfUseFragment : BaseFragment() {
    lateinit var binding: FragmentTermsOfUseBinding
    private val dashboardViewModel: DashboardViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_terms_of_use, container, false)
        binding.lifecycleOwner = this
        viewOnClick()
        return binding.root
    }

    private fun viewOnClick() {
        binding.btnOk.setOnClickListener{
            activity?.let { it.startActivity(Intent(it,DashBoardActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP))
            it.finish()
            }
       }
    }

    override fun onResume() {
        super.onResume()
        dashboardViewModel.isShowToolbar.value = true
        dashboardViewModel.isShowSearchbar.value = false
        dashboardViewModel.isShowBottomNavigation.value = false
        dashboardViewModel.isSetCollapsingToolbar.value = false
        dashboardViewModel.isShowToolbarOptionsWithLogo.value = false
        dashboardViewModel.pendingapprovalStat.value = true
        dashboardViewModel.headerText.value = getString(R.string.terms_of_use)
        dashboardViewModel.isShowHelpLogo.value = true

    }
}