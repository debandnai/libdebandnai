package com.salonsolution.app.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.salonsolution.app.R
import com.salonsolution.app.data.model.FooterBar
import com.salonsolution.app.databinding.CartFooterListItemBinding

class CartFooterListAdapter :
    ListAdapter<FooterBar, CartFooterListAdapter.CartFooterListViewHolder>(CartFooterListDiffUtil()) {

    inner class CartFooterListViewHolder(val itemBinding: CartFooterListItemBinding) :
        RecyclerView.ViewHolder(itemBinding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartFooterListViewHolder {
        val binding: CartFooterListItemBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.cart_footer_list_item,
            parent,
            false
        )
        return CartFooterListViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CartFooterListViewHolder, position: Int) {
        val item = getItem(position)
        with(holder.itemBinding) {
            tvCategoryName.text = item.categoryName
            tvServicesContain.text = item.serviceNames

            root.setOnClickListener {
                Log.d("tag","Position: $position")
            }
        }

    }


    class CartFooterListDiffUtil : DiffUtil.ItemCallback<FooterBar>() {
        override fun areItemsTheSame(
            oldItem: FooterBar,
            newItem: FooterBar
        ): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(
            oldItem: FooterBar,
            newItem: FooterBar
        ): Boolean {
            return oldItem == newItem
        }


    }


}