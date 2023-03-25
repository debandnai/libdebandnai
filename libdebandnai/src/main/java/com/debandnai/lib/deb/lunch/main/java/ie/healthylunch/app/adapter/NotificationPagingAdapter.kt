package ie.healthylunch.app.adapter

import android.annotation.SuppressLint
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import ie.healthylunch.app.data.model.notificationListPagingModel.NotificationList
import ie.healthylunch.app.databinding.NotificationViewListPageBinding
import ie.healthylunch.app.utils.AdapterItemOnclickListener
import ie.healthylunch.app.utils.Constants.Companion.DELETE_NOTIFICATION_TAG


class NotificationPagingAdapter(
    private val listener: AdapterItemOnclickListener
) : PagingDataAdapter<NotificationList, NotificationPagingAdapter.ViewHolder>(Comparator()){


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {

        val inflater = LayoutInflater.from(parent.context)
        val listItem = NotificationViewListPageBinding.inflate(inflater, parent, false)
        return ViewHolder(listItem)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
        holder.binding.notificationClearIv.setOnClickListener {
            listener.onAdapterItemClick(snapshot(),  holder.layoutPosition, DELETE_NOTIFICATION_TAG)

        }
    }


    inner class ViewHolder(val binding: NotificationViewListPageBinding) :
        RecyclerView.ViewHolder(binding.root) {
        @SuppressLint("SetTextI18n")

        fun bind(listItem: NotificationList?) = with(itemView) {
            binding.listItem=listItem
            binding.executePendingBindings()
        }

    }



    class Comparator : DiffUtil.ItemCallback<NotificationList>() {
        override fun areItemsTheSame(oldItem: NotificationList, newItem: NotificationList): Boolean {
            return oldItem.notificationid == newItem.notificationid
        }

        override fun areContentsTheSame(oldItem: NotificationList, newItem: NotificationList): Boolean {
            return oldItem == newItem
        }

    }
}