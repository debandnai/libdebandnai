package com.merkaaz.app.ui.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.activityViewModels
import com.merkaaz.app.R
import com.merkaaz.app.data.viewModel.CategoryViewModel
import com.merkaaz.app.databinding.FragmentSortByTabBinding
import com.merkaaz.app.ui.base.BaseFragment
import com.merkaaz.app.utils.Constants
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SortByTabFragment : BaseFragment() {
    lateinit var binding: FragmentSortByTabBinding
    private val  categoryViewModel: CategoryViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_sort_by_tab, container, false)
        binding.lifecycleOwner = this
        get_data()
        set_data()
       // reset()
        return binding.root
    }

    private fun get_data() {
        binding.rgSortBy.setOnCheckedChangeListener { _, checkedID ->
            categoryViewModel.filter_applicable.value = true
            when (checkedID) {
                R.id.rd_name -> categoryViewModel.sort_field.value = Constants.SORT_NAME
                R.id.rd_price -> categoryViewModel.sort_field.value = Constants.SORT_PRICE
                R.id.rd_discount -> categoryViewModel.sort_field.value = Constants.SORT_DISCOUNT
            }
        }
        binding.rgSortOrder.setOnCheckedChangeListener { _, checkedID ->
            categoryViewModel.filter_applicable.value = true
            when (checkedID) {
                R.id.rd_asc -> categoryViewModel.sort_order.value = Constants.ASCENDING
                R.id.rd_dsc -> categoryViewModel.sort_order.value = Constants.DESCENDING
            }
        }
    }


    private fun set_data() {
        println("filter data sort order...${categoryViewModel.sort_field.value}")
        categoryViewModel.sort_field.observe(viewLifecycleOwner){
            if (it.equals(Constants.SORT_PRICE,true)) {
                binding.rgSortBy.check(binding.rgSortBy.getChildAt(0).id)
            }else if (it.equals(Constants.SORT_NAME,true)) {
                binding.rgSortBy.check(binding.rgSortBy.getChildAt(1).id)
            }else if (it.equals(Constants.SORT_DISCOUNT,true)) {
                binding.rgSortBy.check(binding.rgSortBy.getChildAt(2).id)
            }
        }
        categoryViewModel.sort_order.observe(viewLifecycleOwner){
            it?.let {
                if (it == Constants.ASCENDING)
                    binding.rgSortOrder.check(binding.rgSortOrder.getChildAt(0).id)
                else binding.rgSortOrder.check(binding.rgSortOrder.getChildAt(1).id)
            }
        }
    }

    fun reset() {
        categoryViewModel.sort_order.value = null
        categoryViewModel.sort_field.value = ""
        binding.rgSortOrder.clearCheck()
        binding.rgSortBy.clearCheck()
        categoryViewModel.sort_order.value=null
        categoryViewModel.sort_field.value=""
    }
}