package com.merkaaz.app.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.merkaaz.app.data.model.ProductAttrItem
import com.merkaaz.app.databinding.ProductSpecificationItemBinding

class ProductSpecificationAdapter(var productAttrList: List<ProductAttrItem?>?) :
    RecyclerView.Adapter<ProductSpecificationAdapter.ViewHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val listItem = ProductSpecificationItemBinding.inflate(inflater, parent, false)
        return ViewHolder(listItem)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        productAttrList?.get(position)?.let { holder.bind(it, position, itemCount) }

    }

    class ViewHolder(val binding: ProductSpecificationItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: ProductAttrItem, position: Int, itemCount: Int) {
            binding.dataItem = item
            if (position == itemCount - 1)
                binding.llUnderLine.visibility = View.INVISIBLE
            else
                binding.llUnderLine.visibility = View.VISIBLE
            binding.executePendingBindings()
        }

    }

    override fun getItemCount() = productAttrList?.size ?: 0
}