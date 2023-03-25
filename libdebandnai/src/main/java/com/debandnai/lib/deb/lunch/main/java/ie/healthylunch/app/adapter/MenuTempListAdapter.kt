package ie.healthylunch.app.adapter

import android.app.Activity
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import ie.healthylunch.app.data.model.menuTemplate.MenuTemplateListItem
import ie.healthylunch.app.databinding.MenuTempListRecyclerRowBinding
import ie.healthylunch.app.utils.AdapterItemOnclickListener


class MenuTempListAdapter(
    var menuTempList: List<MenuTemplateListItem?>?,
    var listener: AdapterItemOnclickListener,
    var activity: Activity,
    val isXpLayoutVisible: Boolean
) : RecyclerView.Adapter<MenuTempListAdapter.ViewHolder>() {
    lateinit var infoIv: ImageView

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): MenuTempListAdapter.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val listItem = MenuTempListRecyclerRowBinding.inflate(inflater, parent, false)
        return ViewHolder(listItem)
    }

    override fun onBindViewHolder(holder: MenuTempListAdapter.ViewHolder, position: Int) {
        menuTempList?.get(position)?.let { holder.bind(it, position) }
    }

    override fun getItemCount(): Int = menuTempList?.size ?: 0
    inner class ViewHolder(val binding: MenuTempListRecyclerRowBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: MenuTemplateListItem, itemPosition: Int) {
            item.isXpLayoutVisible =
                this@MenuTempListAdapter.isXpLayoutVisible && (item.menuTemplateIXpPoint ?: 0) > 0
            binding.dataItem = item
            binding.itemLayout.setOnClickListener {
                listener.onAdapterItemClick(
                    menuTempList,
                    itemPosition,
                    "menuTemplate"
                )

            }
            binding.executePendingBindings()

        }
    }


}