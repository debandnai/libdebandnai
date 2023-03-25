package com.merkaaz.app.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.merkaaz.app.data.model.VariationDataItem
import com.merkaaz.app.databinding.PackSizeItemBinding
import com.merkaaz.app.interfaces.AdapterItemClickListener
import com.merkaaz.app.utils.Constants

class PackSizeAdapter(
    private val listener: AdapterItemClickListener,
    var variationList: List<VariationDataItem?>?
) :
    RecyclerView.Adapter<PackSizeAdapter.ViewHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val listItem = PackSizeItemBinding.inflate(inflater, parent, false)
        return ViewHolder(listItem)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val variationItem = variationList?.get(position)
        variationItem?.let { holder.bind(it) }
        holder.binding.llItemLayout.setOnClickListener {
            listener.onAdapterItemClick(variationList, position, it, Constants.VARIATION)
        }
    }

    class ViewHolder(val binding: PackSizeItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: VariationDataItem) {
            binding.dataItem = item

            binding.executePendingBindings()
        }

    }

    override fun getItemCount() = variationList?.size ?: 0

}