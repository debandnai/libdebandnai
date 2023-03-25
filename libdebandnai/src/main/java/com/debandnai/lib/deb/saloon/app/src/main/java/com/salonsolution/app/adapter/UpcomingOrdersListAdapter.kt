package com.salonsolution.app.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.salonsolution.app.R
import com.salonsolution.app.data.model.UpcomingOrderList
import com.salonsolution.app.databinding.CategoriesListItemBinding
import com.salonsolution.app.databinding.UpcomingOrdersListItemBinding
import com.salonsolution.app.interfaces.CategoriesItemClickListener
import com.salonsolution.app.interfaces.HomePageClickListener
import com.salonsolution.app.utils.AvatarGenerator

class UpcomingOrdersListAdapter(private val listener:HomePageClickListener) :
    ListAdapter<UpcomingOrderList, UpcomingOrdersListAdapter.UpcomingOrdersListViewHolder>(ListDiffUtil()) {

    inner class UpcomingOrdersListViewHolder(val itemBinding: UpcomingOrdersListItemBinding) :
        RecyclerView.ViewHolder(itemBinding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UpcomingOrdersListViewHolder {
        val binding: UpcomingOrdersListItemBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.upcoming_orders_list_item,
            parent,
            false
        )
        return UpcomingOrdersListViewHolder(binding)
    }

    override fun onBindViewHolder(holder: UpcomingOrdersListViewHolder, position: Int) {
        val item = getItem(position)
        with(holder.itemBinding) {
            tvName.text = item.serviceName
            tvDate.text = item.bookingDate
            Glide.with(root.context)
                .load(item.serviceImage)
                .placeholder(R.drawable.placeholder_image)
                .centerInside()
                .into(ivOrders)

            root.setOnClickListener {
                listener.onOrderItemClickListener(holder.absoluteAdapterPosition,item)
            }
        }

    }


    class ListDiffUtil : DiffUtil.ItemCallback<UpcomingOrderList>() {
        override fun areItemsTheSame(
            oldItem: UpcomingOrderList,
            newItem: UpcomingOrderList
        ): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(
            oldItem: UpcomingOrderList,
            newItem: UpcomingOrderList
        ): Boolean {
            return oldItem == newItem
        }


    }


}