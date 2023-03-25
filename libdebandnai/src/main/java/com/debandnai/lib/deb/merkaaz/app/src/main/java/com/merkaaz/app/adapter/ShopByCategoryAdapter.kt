package com.merkaaz.app.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.merkaaz.app.data.model.CategoryData
import com.merkaaz.app.databinding.ShopByCategoryItemsLayoutBinding
import com.merkaaz.app.interfaces.AdapterItemClickListener
import com.merkaaz.app.utils.Constants


class ShopByCategoryAdapter(val listener: AdapterItemClickListener) :
    ListAdapter<CategoryData, ShopByCategoryAdapter.ViewHolder>(DiffCallBack()) {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val listItem = ShopByCategoryItemsLayoutBinding.inflate(inflater, parent, false)
        return ViewHolder(listItem)
    }

    //shop_by_category_items_layout
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position),position)
    }

  inner  class ViewHolder(val binding: ShopByCategoryItemsLayoutBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: CategoryData, position: Int) {
            /*binding.categoryData = item

            binding.clItemLayout.setOnClickListener{
                listener.onAdapterItemClick(currentList, position, binding.tvProductName, Constants.SHOP_BY_CATEGORY)
            }
            binding.executePendingBindings()*/
        }

    }

    class DiffCallBack : DiffUtil.ItemCallback<CategoryData>() {
        override fun areItemsTheSame(oldItem: CategoryData, newItem: CategoryData): Boolean {
            return oldItem.categoryId == newItem.categoryId
        }

        override fun areContentsTheSame(oldItem: CategoryData, newItem: CategoryData): Boolean {
            return oldItem == newItem
        }

    }


}