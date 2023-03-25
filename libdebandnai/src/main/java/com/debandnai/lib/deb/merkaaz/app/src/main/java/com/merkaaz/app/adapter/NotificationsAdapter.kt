package com.merkaaz.app.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.merkaaz.app.data.model.NotificationItem
import com.merkaaz.app.databinding.NotificationItemBinding
import com.merkaaz.app.interfaces.AdapterItemClickListener
import com.merkaaz.app.utils.Constants

class NotificationsAdapter(
    var notificationList: List<NotificationItem?>?,
    val listener: AdapterItemClickListener
) :
    RecyclerView.Adapter<NotificationsAdapter.ViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val listItem = NotificationItemBinding.inflate(inflater, parent, false)
        return ViewHolder(listItem)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        notificationList?.get(position)?.let { holder.bind(it, position, itemCount) }
        holder.binding.ivDelete.setOnClickListener { v ->
            listener.onAdapterItemClick(
                notificationList,
                position,
                v,
                Constants.NOTIFICATION_DELETE
            )
        }
    }

    class ViewHolder(val binding: NotificationItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: NotificationItem, position: Int, itemCount: Int) {
            binding.dataItem = item

            if (position == (itemCount - 1))
                binding.viewUnderline.visibility = View.GONE
            else
                binding.viewUnderline.visibility = View.VISIBLE

            binding.executePendingBindings()

        }

    }

    override fun getItemCount() = notificationList?.size ?: 0


}