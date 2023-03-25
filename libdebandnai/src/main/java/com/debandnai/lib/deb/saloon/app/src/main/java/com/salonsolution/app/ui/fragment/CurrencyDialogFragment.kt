package com.salonsolution.app.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.salonsolution.app.R
import com.salonsolution.app.data.preferences.AppSettingsPref
import com.salonsolution.app.data.viewModel.DashboardViewModel
import com.salonsolution.app.databinding.FragmentCurrencyDialogBinding
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class CurrencyDialogFragment : BottomSheetDialogFragment() {
    private lateinit var binding: FragmentCurrencyDialogBinding
    private val dashboardViewModel: DashboardViewModel by activityViewModels()
//    companion object{
//        const val CURRENCY_CHANGED = "currency_changed"
//    }

    @Inject
    lateinit var appSettingsPref: AppSettingsPref

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding =
            DataBindingUtil.inflate(inflater, R.layout.fragment_currency_dialog, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView()
    }

    private fun initView() {
        with(binding) {

            dollarLayout.setOnClickListener {
                if(appSettingsPref.getCurrency()==1) return@setOnClickListener
                appSettingsPref.setCurrency(1) // 1-> USD, 2-> KZ
                updateView()
                dashboardViewModel.isCurrencyChanged.postValue(true)
                findNavController().popBackStack()
            }

            kwanzasLayout.setOnClickListener {
                if(appSettingsPref.getCurrency()==2) return@setOnClickListener
                appSettingsPref.setCurrency(2) // 1-> USD, 2-> KZ
                updateView()

                dashboardViewModel.isCurrencyChanged.postValue(true)
                findNavController().popBackStack()
            }
            updateView()
        }
    }

    private fun updateView() {
        when (appSettingsPref.getCurrency()) {
            1 -> {
                binding.dollarLayout.background = ContextCompat.getDrawable(binding.root.context, R.drawable.choose_currency_selected_bg)
                binding.ivDollarCheck.visibility = View.VISIBLE
                binding.ivKzCheck.visibility = View.GONE
                binding.kwanzasLayout.setBackgroundResource(0)
            }
            2 -> {
                binding.kwanzasLayout.background = ContextCompat.getDrawable(binding.root.context, R.drawable.choose_currency_selected_bg)
                binding.ivKzCheck.visibility = View.VISIBLE
                binding.ivDollarCheck.visibility = View.GONE
                binding.dollarLayout.setBackgroundResource(0)
            }
            else -> {
                //nothing to do
            }
        }
    }


}