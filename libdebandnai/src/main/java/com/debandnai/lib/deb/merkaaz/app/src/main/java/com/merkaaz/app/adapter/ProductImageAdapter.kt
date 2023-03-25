package com.merkaaz.app.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.merkaaz.app.data.model.ImagesItem
import com.merkaaz.app.databinding.ProductImgItemBinding
import com.merkaaz.app.interfaces.AdapterItemClickListener
import com.merkaaz.app.utils.Constants

class ProductImageAdapter(private val listener: AdapterItemClickListener) :
    ListAdapter<ImagesItem, ProductImageAdapter.ViewHolder>(DiffCallBack()) {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val listItem = ProductImgItemBinding.inflate(inflater, parent, false)
        return ViewHolder(listItem)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        holder.bind(getItem(position))
        holder.binding.ivProductImg.setOnClickListener {
            listener.onAdapterItemClick(currentList,position,it, Constants.PRODUCT_IMAGE)
        }
    }

    class ViewHolder(val binding: ProductImgItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: ImagesItem) {
            binding.dataItem = item
            binding.executePendingBindings()
        }

    }

    class DiffCallBack : DiffUtil.ItemCallback<ImagesItem>() {
        override fun areItemsTheSame(oldItem: ImagesItem, newItem: ImagesItem): Boolean {
            return oldItem.imageLink == oldItem.imageLink
        }

        override fun areContentsTheSame(oldItem: ImagesItem, newItem: ImagesItem): Boolean {
            return oldItem == newItem
        }

    }

}