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
import com.salonsolution.app.data.model.ServicesList
import com.salonsolution.app.databinding.ServiceListItemBinding
import com.salonsolution.app.interfaces.ServiceListClickListener
import com.salonsolution.app.utils.AvatarGenerator

class ServiceListPagingAdapter(val listener: ServiceListClickListener) :
    PagingDataAdapter<ServicesList, ServiceListPagingAdapter.ServiceListViewHolder>(
        ServiceListDiffUtil()
    ) {

    inner class ServiceListViewHolder(val itemBinding: ServiceListItemBinding) :
        RecyclerView.ViewHolder(itemBinding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ServiceListViewHolder {
        val binding: ServiceListItemBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.service_list_item,
            parent,
            false
        )
        return ServiceListViewHolder(binding)
    }


    override fun onBindViewHolder(holder: ServiceListViewHolder, position: Int) {

        val item = getItem(position)
        item?.let { list ->
            with(holder.itemBinding){
                val imgAvatar = AvatarGenerator.avatarImage(
                    context = root.context,
                    width = 500,
                    height = 500,
                    avatarTextSizeRatio = 0.33f,
                    name = list.serviceName,
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
                tvTitle.text = list.serviceName
                tvServiceTime.text = list.hourMin
                tvPrice.text = list.price
                tvDetails.text = list.description

                btAdd.setOnClickListener {
                    listener.onAddClick(holder.absoluteAdapterPosition,list)
                }

                root.setOnClickListener {
                    listener.onItemClick(holder.absoluteAdapterPosition,list)
                }
            }
        }

    }


    class ServiceListDiffUtil : DiffUtil.ItemCallback<ServicesList>() {
        override fun areItemsTheSame(oldItem: ServicesList, newItem: ServicesList): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: ServicesList, newItem: ServicesList): Boolean {
            return oldItem == newItem
        }

    }

}