package com.salonsolution.app.adapter

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
import com.salonsolution.app.data.model.BookedServiceList
import com.salonsolution.app.data.model.PopularCategoryList
import com.salonsolution.app.databinding.RecentServicesListItemBinding
import com.salonsolution.app.interfaces.HomePageClickListener
import com.salonsolution.app.utils.AvatarGenerator

class PopularServicesListAdapter(private val listener: HomePageClickListener) :
    ListAdapter<PopularCategoryList, PopularServicesListAdapter.PopularServicesListViewHolder>(ListDiffUtil()) {

    inner class PopularServicesListViewHolder(val itemBinding: RecentServicesListItemBinding) :
        RecyclerView.ViewHolder(itemBinding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PopularServicesListViewHolder {
        val binding: RecentServicesListItemBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.recent_services_list_item,
            parent,
            false
        )
        return PopularServicesListViewHolder(binding)
    }

    override fun onBindViewHolder(holder: PopularServicesListViewHolder, position: Int) {
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
                .load(item.categoryImage)
                .placeholder(imgAvatar)
                .centerInside()
                .into(ivService)

            root.setOnClickListener {
                listener.onCategoryItemClickListener(holder.absoluteAdapterPosition,item)
            }
        }

    }


    class ListDiffUtil : DiffUtil.ItemCallback<PopularCategoryList>() {
        override fun areItemsTheSame(
            oldItem: PopularCategoryList,
            newItem: PopularCategoryList
        ): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(
            oldItem: PopularCategoryList,
            newItem: PopularCategoryList
        ): Boolean {
            return oldItem == newItem
        }


    }


}