package com.merkaaz.app.ui.fragments

import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.google.gson.JsonArray
import com.merkaaz.app.R

import com.merkaaz.app.adapter.FilterAdapter
import com.merkaaz.app.data.model.*
import com.merkaaz.app.data.viewModel.CategoryViewModel
import com.merkaaz.app.data.viewModel.FilterByTabViewModel
import com.merkaaz.app.databinding.FragmentFilterByTabBinding
import com.merkaaz.app.interfaces.AdapterItemClickListener
import com.merkaaz.app.utils.Constants
import com.merkaaz.app.utils.MethodClass
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FilterByTabFragment : Fragment() , FilterAdapter.CollapseParentRow,AdapterItemClickListener{
   private var listData : ArrayList<FilterCategoryModel> ?=null
    lateinit var binding: FragmentFilterByTabBinding
    private val  categoryViewModel: CategoryViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_filter_by_tab, container, false)
        binding.lifecycleOwner = this
        set_data()
        return binding.root
    }

    private fun set_data() {
        activity?.let {act->
            listData = ArrayList()

            categoryViewModel.filter_livedata.observe(viewLifecycleOwner){list->
                Log.d("TAG", "set_data:priceList "+list.sizeList.size)
                list?.brandList?.let {
                        brandlist-> FilterCategoryModel(parentTitle = act.getString(R.string.brand), childList = brandlist)
                }?.let {data->
                    if (data.childList.size > 0)
                        listData!!.add(data)
                }

                list?.priceList?.let {
                        pricelist-> FilterCategoryModel(parentTitle = act.getString(R.string.price), childList = pricelist)
                }?.let {data->
                    if (data.childList.size > 0)
                        listData!!.add(data)
                    Log.d("TAG", "set_data:priceList "+data.childList.size)
                }

                list?.discountList?.let {
                        discountlist-> FilterCategoryModel(parentTitle = act.getString(R.string.discount), childList = discountlist)
                }?.let {data->
                    if (data.childList.size > 0)
                        listData!!.add(data)
                    Log.d("TAG", "set_data:discountList "+data.childList.size)
                }

                list?.sizeList?.let {
                        sizelist-> FilterCategoryModel(parentTitle = act.getString(R.string.pack_sizes), childList = sizelist)
                }?.let {data->
                    if (data.childList.size > 0)
                        listData!!.add(data)
                    Log.d("TAG", "set_data:sizeList "+listData!!.size)
                }
            }

            listData?.let {list->

                setAdapter(list)
            }

        }
    }

    private fun setAdapter(listData: ArrayList<FilterCategoryModel>) {
        binding.rvOrderBy.adapter = FilterAdapter(requireActivity(),listData,this,this)
        (binding.rvOrderBy.adapter as FilterAdapter).notifyDataSetChanged()
    }

    override fun collapseParentItems() {
        setAdapter( ArrayList())
        (binding.rvOrderBy.adapter as FilterAdapter).notifyDataSetChanged()
        listData?.let { setAdapter(it) }

    }

    override fun onAdapterItemClick(
        arrayList: List<Any?>?,
        position: Int,
        clickView: View?,
        tag: String
    ) {
        if (tag.equals(Constants.FILTER_DATE,true)) {
            categoryViewModel.filterList = arrayList as ArrayList<FilterCategoryModel>
            categoryViewModel.filter_list_live_data.value = categoryViewModel.filterList
        }

    }
    fun reset(){
        val list = listData
        for (j in 0 until list?.size!!) {
            if (list[j].parentTitle !=null) {
                for (i in 0 until list[j].childList.size) {
                    list[j].childList[i].isChecked = false
                }
            }
        }
        (binding.rvOrderBy.adapter as FilterAdapter).notifyDataSetChanged()
    }


}