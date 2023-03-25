package com.merkaaz.app.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.merkaaz.app.data.model.CategoryData
import com.merkaaz.app.databinding.HomeTopProductsItemBinding
import com.merkaaz.app.interfaces.AdapterItemClickListener
import com.merkaaz.app.utils.Constants.HOME_CATEGORY

class HomeCategoryAdapter(val listener: AdapterItemClickListener) :
    ListAdapter<CategoryData, HomeCategoryAdapter.ViewHolder>(DiffCallBack()) {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val listItem = HomeTopProductsItemBinding.inflate(inflater, parent, false)
        return ViewHolder(listItem)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
        holder.binding.llItemLayout.setOnClickListener { v ->
            listener.onAdapterItemClick(currentList, position, v, HOME_CATEGORY)
        }
    }


    class ViewHolder(val binding: HomeTopProductsItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: CategoryData) {
            binding.dataItem = item
            binding.executePendingBindings()
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