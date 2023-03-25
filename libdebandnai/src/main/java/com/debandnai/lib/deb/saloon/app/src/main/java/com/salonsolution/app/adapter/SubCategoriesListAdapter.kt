package com.salonsolution.app.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.salonsolution.app.R
import com.salonsolution.app.data.model.CategoryListModel
import com.salonsolution.app.databinding.SubCategoriesListItemBinding
import com.salonsolution.app.interfaces.CategoriesItemClickListener

class SubCategoriesListAdapter( val listener: CategoriesItemClickListener) :
    ListAdapter<CategoryListModel, SubCategoriesListAdapter.SubCategoriesViewHolder>(ListDiffUtil())  {



    inner class SubCategoriesViewHolder(val itemBinding: SubCategoriesListItemBinding) :
        RecyclerView.ViewHolder(itemBinding.root)


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SubCategoriesViewHolder {
        val binding: SubCategoriesListItemBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.sub_categories_list_item,
            parent,
            false
        )
        return SubCategoriesViewHolder(binding)
    }

    override fun onBindViewHolder(holder: SubCategoriesViewHolder, position: Int) {
        val item = getItem(position)
        with(holder.itemBinding) {
            tvCategoryName.text = item.categoryName
            if (item.isSelected) {

                tvCategoryName.setTextColor(ContextCompat.getColor(root.context, R.color.white))
                tvCategoryName.background =
                    ContextCompat.getDrawable(root.context, R.drawable.sub_categories_item_selected_bg)

            } else {

                tvCategoryName.setTextColor(ContextCompat.getColor(root.context, R.color.default_text_color2))
                tvCategoryName.background =
                    ContextCompat.getDrawable(root.context, R.drawable.sub_categories_item_normal_bg)
            }
        }

        holder.itemBinding.root.setOnClickListener {
            if(!item.isSelected)
                listener.onItemClick(holder.absoluteAdapterPosition, item)
        }
    }


    class ListDiffUtil : DiffUtil.ItemCallback<CategoryListModel>() {
        override fun areItemsTheSame(oldItem: CategoryListModel, newItem: CategoryListModel): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: CategoryListModel, newItem: CategoryListModel): Boolean {
            return oldItem == newItem
        }


    }


}