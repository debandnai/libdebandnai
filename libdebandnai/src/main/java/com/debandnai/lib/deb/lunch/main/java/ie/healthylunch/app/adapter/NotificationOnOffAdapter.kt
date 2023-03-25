package ie.healthylunch.app.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import ie.healthylunch.app.data.model.notificationSettingsModel.DataItem
import ie.healthylunch.app.databinding.NotificationViewOnOffListBinding
import ie.healthylunch.app.ui.NotificationOnOffActivity
import ie.healthylunch.app.utils.AdapterItemOnclickListener
import ie.healthylunch.app.utils.Constants.Companion.STATUS_MINUS_ONE
import ie.healthylunch.app.utils.Constants.Companion.STATUS_ZERO

class NotificationOnOffAdapter(
    var notificationSettingList: List<DataItem>,
    var listener: AdapterItemOnclickListener,
    var isNotificationEnable:Boolean
) : RecyclerView.Adapter<NotificationOnOffAdapter.ViewHolder>() {


    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): NotificationOnOffAdapter.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val listItem = NotificationViewOnOffListBinding.inflate(inflater, parent, false)
        return ViewHolder(listItem)
    }

    override fun onBindViewHolder(holder: NotificationOnOffAdapter.ViewHolder, position: Int) {

        holder.bind(notificationSettingList[position])
        holder.binding.rlOnOff.setOnClickListener {
            listener.onAdapterItemClick(
                notificationSettingList,
                position,
                "notificationSetting"
            )

        }
        holder.binding.rlOnOff.isEnabled = isNotificationEnable
    }

    override fun getItemCount(): Int = notificationSettingList.size
    inner class ViewHolder(val binding: NotificationViewOnOffListBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: DataItem) {
            binding.dataItem = item
            binding.executePendingBindings()
        }
    }

}
