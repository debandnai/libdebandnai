package com.salonsolution.app.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.salonsolution.app.R
import com.salonsolution.app.databinding.FragmentListSortDialogBinding
import com.salonsolution.app.utils.Constants

class ListSortDialogFragment : BottomSheetDialogFragment() {
    private lateinit var binding: FragmentListSortDialogBinding
    private val navArgs: ListSortDialogFragmentArgs by navArgs()
    private var sortField = Constants.PRICE
    private var sortOrder = -1
    private var isShowPrice = true
    //    "sortField": "price/name",
//    "sortOrder": 1/-1, //1 -> ASC,-1 -> DESC

    companion object{
        const val SORT_BY = "sort_by"
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_list_sort_dialog, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        getIntentData()
        initView()
    }

    private fun initView() {
        with(binding){

            rgGroup.setOnCheckedChangeListener { _, checkId ->

                    when (checkId) {
                        R.id.rbPriceHighToLow -> {
                            if(!binding.rbPriceHighToLow.isPressed) return@setOnCheckedChangeListener
                                sortField = Constants.PRICE
                                sortOrder = -1
                        }
                        R.id.rbPriceLowToHigh -> {
                            if(!binding.rbPriceLowToHigh.isPressed) return@setOnCheckedChangeListener
                                sortField = Constants.PRICE
                                sortOrder = 1
                        }
                        R.id.rbAToZ -> {
                            if(!binding.rbAToZ.isPressed) return@setOnCheckedChangeListener
                            sortField = Constants.NAME
                            sortOrder = 1
                        }
                        R.id.rbZToA -> {
                            if(!binding.rbZToA.isPressed) return@setOnCheckedChangeListener
                            sortField = Constants.NAME
                            sortOrder = -1
                        }
                        else -> {
                           // nothing to do
                        }

                    }
                    findNavController().previousBackStackEntry?.savedStateHandle?.set(
                        SORT_BY,
                        Pair(sortField, sortOrder)
                    )
                    findNavController().popBackStack()
            }
            manageList()
            setSelectedButton()
        }
    }

    private fun manageList() {
        if(!isShowPrice){
            binding.rbPriceHighToLow.visibility = View.GONE
            binding.rbPriceLowToHigh.visibility = View.GONE
        }
    }

    private fun setSelectedButton() {
        if(sortField== Constants.PRICE && sortOrder==1){
            binding.rbPriceLowToHigh.isChecked = true
        }else if(sortField== Constants.PRICE && sortOrder==-1){
            binding.rbPriceHighToLow.isChecked = true
        }else if(sortField== Constants.NAME && sortOrder==1){
            binding.rbAToZ.isChecked = true
        }else if(sortField== Constants.NAME && sortOrder==-1){
            binding.rbZToA.isChecked = true
        }else{
            //nothing to do
        }
    }


    private fun getIntentData() {
        sortField = navArgs.sortField
        sortOrder = navArgs.sortOrder
        isShowPrice = navArgs.isShowPrice


    }


}