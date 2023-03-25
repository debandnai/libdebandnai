package com.salonsolution.app.adapter

import android.content.Context
import android.graphics.Typeface
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.salonsolution.app.R
import com.salonsolution.app.data.model.StaffList
import com.salonsolution.app.databinding.StaffListItemBinding
import com.salonsolution.app.interfaces.StaffListClickListener
import com.salonsolution.app.utils.AvatarGenerator

class StaffListPagingAdapter(val listener: StaffListClickListener) :
    PagingDataAdapter<StaffList, StaffListPagingAdapter.StaffListViewHolder>(StaffListDiffUtil()) {

    inner class StaffListViewHolder(val itemBinding: StaffListItemBinding) :
        RecyclerView.ViewHolder(itemBinding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StaffListViewHolder {
        val binding: StaffListItemBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.staff_list_item,
            parent,
            false
        )
        return StaffListViewHolder(binding)
    }


    override fun onBindViewHolder(holder: StaffListViewHolder, position: Int) {

        val item = getItem(position)
        item?.let { list ->
            with(holder.itemBinding){
                val imgAvatar = AvatarGenerator.avatarImage(
                    context = root.context,
                    width = 500,
                    height = 500,
                    avatarTextSizeRatio = 0.33f,
                    name = list.staffName,
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
                    .into(ivService)
                tvTitle.text = list.staffName
                tvDesc.text = list.about

                tvViewMore.setOnClickListener {
                    listener.onItemClick(holder.absoluteAdapterPosition,list)
                }

                root.setOnClickListener {
                    listener.onItemClick(holder.absoluteAdapterPosition,list)
                }
            }
        }

    }


    class StaffListDiffUtil : DiffUtil.ItemCallback<StaffList>() {
        override fun areItemsTheSame(oldItem: StaffList, newItem: StaffList): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: StaffList, newItem: StaffList): Boolean {
            return oldItem == newItem
        }

    }

}