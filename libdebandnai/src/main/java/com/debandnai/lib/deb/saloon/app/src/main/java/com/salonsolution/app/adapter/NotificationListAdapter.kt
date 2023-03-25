package com.salonsolution.app.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.salonsolution.app.R
import com.salonsolution.app.data.model.NotificationList
import com.salonsolution.app.databinding.NotificationListItemBinding
import com.salonsolution.app.interfaces.NotificationListClickListener

class NotificationListAdapter(val listener: NotificationListClickListener) :
    ListAdapter<NotificationList, NotificationListAdapter.NotificationViewHolder>(
        NotificationListDiffUtil()
    ) {

    inner class NotificationViewHolder(val itemBinding: NotificationListItemBinding) :
        RecyclerView.ViewHolder(itemBinding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotificationViewHolder {
        val binding: NotificationListItemBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.notification_list_item,
            parent,
            false
        )
        return NotificationViewHolder(binding)
    }


    override fun onBindViewHolder(holder: NotificationViewHolder, position: Int) {

        val item = getItem(position)
        with(holder.itemBinding) {
            tvDateTime.text = root.context.getString(R.string.date_time_concat,item.notificationDate,item.notificationTime)
            tvMessage.text = item.notificationMessage
            root.setOnClickListener {
                listener.onItemClick(holder.absoluteAdapterPosition,item)
            }
            ivDelete.setOnClickListener {
                listener.onDeleteClick(holder.absoluteAdapterPosition,item)
            }
        }

    }


    class NotificationListDiffUtil : DiffUtil.ItemCallback<NotificationList>() {
        override fun areItemsTheSame(
            oldItem: NotificationList,
            newItem: NotificationList
        ): Boolean {
            return oldItem.notificationId == newItem.notificationId
        }

        override fun areContentsTheSame(
            oldItem: NotificationList,
            newItem: NotificationList
        ): Boolean {
            return oldItem == newItem
        }

    }

}