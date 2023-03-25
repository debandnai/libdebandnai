package com.merkaaz.app.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.merkaaz.app.R
import com.merkaaz.app.data.model.ChildList
import com.merkaaz.app.data.model.FilterCategoryModel
import com.merkaaz.app.databinding.FilterCategoryRowBinding
import com.merkaaz.app.databinding.FilterProductNameRowBinding
import com.merkaaz.app.interfaces.AdapterItemClickListener
import com.merkaaz.app.utils.Constants
import kotlin.collections.ArrayList

class FilterAdapter(
    var mContext: Context,
    var filterList: ArrayList<FilterCategoryModel>,
    var collapseParentRow: CollapseParentRow,
    var adapterItemClickListener: AdapterItemClickListener
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        return if (viewType == Constants.FILTER_CATEGORY_TYPE) {
            val inflater = LayoutInflater.from(parent.context)
            val listItem = FilterCategoryRowBinding.inflate(inflater, parent, false)
            GroupViewHolder(listItem)
        } else {
            val inflater = LayoutInflater.from(parent.context)
            val listItem = FilterProductNameRowBinding.inflate(inflater, parent, false)
            ChildViewHolder(listItem)
        }
    }

    override fun getItemCount(): Int = filterList.size

    override fun getItemViewType(position: Int): Int = filterList[position].type

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }


    @SuppressLint("NotifyDataSetChanged", "SetTextI18n")
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

        val dataList = filterList[position]

        if (dataList.type == Constants.FILTER_CATEGORY_TYPE) {
            holder as GroupViewHolder
            holder.binding.parentTitle.text = dataList.parentTitle
            holder.binding.downIv.setOnClickListener {
                if (dataList.isExpanded)
                    holder.binding.downIv.setImageResource(R.drawable.botton_shape_till_color)
                else
                    holder.binding.downIv.setImageResource(R.drawable.ic_up_arrow)
                expandOrCollapseParentItem(dataList, position)
            }

        } else {
            holder as ChildViewHolder

            val singleService = dataList.childList.first()
            holder.binding.txtName.text = singleService.name
            holder.binding.cbFilterProductItems.isChecked = singleService.isChecked

            holder.binding.constraintChild.setOnClickListener{
                set_data(position, !dataList.childList.first().isChecked,holder.binding)
            }
            holder.binding.cbFilterProductItems.setOnClickListener {
                set_data(position,!dataList.childList.first().isChecked,holder.binding)
            }

        }
    }

    private fun set_data(position: Int, ischecked: Boolean, binding: FilterProductNameRowBinding) {
        filterList[position].childList.first().isChecked = ischecked
        binding.cbFilterProductItems.isChecked = ischecked
        println("position...parent..$ischecked.${filterList[position].childList.first().parentPosition}.....child...${filterList[position].childList.first().childPosition}")
        adapterItemClickListener.onAdapterItemClick(filterList,position,null,Constants.FILTER_DATE)
    }


    @SuppressLint("NotifyDataSetChanged")
    private fun expandParentRow(position: Int) {
        var i = 0
        val currentBoardingRow = filterList[position]
        val services = currentBoardingRow.childList
        currentBoardingRow.isExpanded = true

        var nextPosition = position
        if (currentBoardingRow.type == Constants.FILTER_CATEGORY_TYPE) {

            services.forEach { service ->
                val parentModel = FilterCategoryModel()
                parentModel.type = Constants.FILTER_PRODUCT_TYPE
                val subList: ArrayList<ChildList> = ArrayList()

                filterList[position].childList[i].parentPosition = position
                filterList[position].childList[i].childPosition = i
                subList.add(service)
                parentModel.childList = subList
                filterList.add(++nextPosition, parentModel)
                i++
            }
            notifyDataSetChanged()
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun collapseParentRow(position: Int) {

        val currentBoardingRow = filterList[position]
        val services = currentBoardingRow.childList
        filterList[position].isExpanded = false
        if (filterList[position].type == Constants.FILTER_CATEGORY_TYPE) {
            services.forEach { _ ->
                filterList.removeAt(position + 1)
            }
            notifyDataSetChanged()
        }
        collapseParentRow.collapseParentItems()
    }

    class GroupViewHolder(val binding: FilterCategoryRowBinding) :
        RecyclerView.ViewHolder(binding.root)


    class ChildViewHolder(val binding: FilterProductNameRowBinding) :
        RecyclerView.ViewHolder(binding.root)


    private fun expandOrCollapseParentItem(singleBoarding: FilterCategoryModel, position: Int) {

        if (singleBoarding.isExpanded) {
            collapseParentRow(position)
        } else {

            expandParentRow(position)
        }
    }

    interface CollapseParentRow {
        fun collapseParentItems()
    }

}