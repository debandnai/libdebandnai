package com.salonsolution.app.adapter

import android.content.Context
import android.graphics.Typeface
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.salonsolution.app.R
import com.salonsolution.app.data.model.CategoryListModel
import com.salonsolution.app.databinding.CategoriesListItemBinding
import com.salonsolution.app.interfaces.CategoriesItemClickListener
import com.salonsolution.app.utils.AvatarGenerator

class CategoriesListAdapter(val listener: CategoriesItemClickListener) :
    ListAdapter<CategoryListModel, CategoriesListAdapter.ServiceListViewHolder>(ListDiffUtil()) {

    inner class ServiceListViewHolder(val itemBinding: CategoriesListItemBinding) :
        RecyclerView.ViewHolder(itemBinding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ServiceListViewHolder {
        val binding: CategoriesListItemBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.categories_list_item,
            parent,
            false
        )
        return ServiceListViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ServiceListViewHolder, position: Int) {
        val item = getItem(position)
        with(holder.itemBinding) {
            tvTitle.text = item.categoryName
            val imgAvatar = AvatarGenerator.avatarImage(
                context = root.context,
                width = 500,
                height = 500,
                avatarTextSizeRatio = 0.33f,
                name = item.categoryName,
                avatarTextStyle = Typeface.NORMAL,
                backgroundColor = ContextCompat.getColor(
                    root.context,
                    R.color.avatar_background_color
                ),
                avatarTextColor = ContextCompat.getColor(
                    root.context,
                    R.color.avatar_text_color
                )
            )
            Glide.with(root.context)
                .load(item.image)
                .placeholder(imgAvatar)
                .centerInside()
                .into(ivCategories)
        }

        holder.itemBinding.root.setOnClickListener {
            listener.onItemClick(holder.absoluteAdapterPosition, item)
        }

    }


    class ListDiffUtil : DiffUtil.ItemCallback<CategoryListModel>() {
        override fun areItemsTheSame(
            oldItem: CategoryListModel,
            newItem: CategoryListModel
        ): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(
            oldItem: CategoryListModel,
            newItem: CategoryListModel
        ): Boolean {
            return oldItem == newItem
        }


    }


}